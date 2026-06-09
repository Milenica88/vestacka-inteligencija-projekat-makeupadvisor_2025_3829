# Makeup Advisor — Project Documentation

**Faculty of Organizational Sciences, University of Belgrade**
Course: *Tools and Methods of Artificial Intelligence and Software Engineering*

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [System Architecture](#2-system-architecture)
   - [2.1 Recommendation Engine](#21-recommendation-engine)
   - [2.2 Persistence Layer](#22-persistence-layer)
   - [2.3 Catalog Import](#23-catalog-import)
   - [2.4 Web Layer (REST API)](#24-web-layer-rest-api)
   - [2.5 Frontend](#25-frontend)
3. [Data Flow](#3-data-flow)
4. [Technologies](#4-technologies)
5. [Usage](#5-usage)
6. [Project Structure](#6-project-structure)
7. [References](#7-references)

---

## 1. Introduction

**Makeup Advisor** is a web application that gives personalized makeup recommendations based on a user profile: skin type, undertone, skin tone (depth), personal preferences, and the occasion. The goal is to take a large catalog of products and surface the ones that best match a specific user, while also suggesting a complete style (foundation, lipstick, blush, and eye makeup).

Choosing makeup is a problem with many variables — the same shade does not suit every skin tone, a matte foundation does not suit dry skin, and the right choice also depends on the occasion (an everyday look versus a formal one). With a huge number of products on the market, manual searching becomes impractical, which motivates an automated, personalized recommendation system.

The application solves this by combining two artificial intelligence approaches:

- **Rule-based (expert) system** — domain knowledge about what suits which skin type is encoded as a set of rules.
- **Content-based recommender** — each product is scored according to how well it matches the user's request, and products are ranked by that score.

The project applies software engineering principles: a layered architecture with clear separation of concerns, data persistence, a REST API, and fault tolerance (a local fallback catalog is used when the external data source is unavailable).

---

## 2. System Architecture

The application is split into a **backend** (Clojure) and a **frontend** (React). The backend is further organized into layers, each with a single responsibility: computing recommendations, storing data, importing the catalog, and exposing functionality over HTTP. This separation allows each part to be developed, tested, and changed independently.

The data flow when a user requests a recommendation:

```
User (React form)
      |  HTTP POST /recommend (JSON)
      v
  web.clj      receives the request, JSON -> Clojure map
      v
  core.clj     orchestration: loads products and calls the engine
      |-- db.clj      returns the catalog, saves the recommendation to history
      |-- engine.clj  COMPUTES the recommendation
      v
  web.clj      result -> JSON, returned to the user
```

The `import.clj` layer runs before everything else — it fills the database with products when the application starts.

### 2.1 Recommendation Engine

`engine.clj` is the central, "thinking" part of the application. It does not depend on any other part of the system (it only requires the standard `clojure.string` and `clojure.set` libraries), so it can be tested in isolation. It consists of two logical parts: the rule-based system and product scoring.

#### 2.1.1 Rule-based system

The expert (rule-based) system represents knowledge as rules of the form "if the skin has property X, recommend Y." These rules are implemented as functions using `case` branching. For example, `recommend-foundation` returns a foundation type based on skin type, with a universal default:

```clojure
(defn recommend-foundation
  [skin-type]
  (case skin-type
    "dry"         "Hydrating foundation"
    "oily"        "Oil-free matte foundation"
    "combination" "Semi-matte foundation"
    "sensitive"   "Hypoallergenic foundation"
    "Universal lightweight foundation"))
```

The same approach defines `recommend-lipstick` (shade by skin tone), `recommend-blush` (blush by undertone), and `recommend-eyes` (eye makeup by occasion). The knowledge base also contains helper maps: `desired-finish` links a skin type to its preferred foundation finish (e.g. oily → matte), and `occasion-intensity` assigns a numeric intensity to each occasion (1 = daily, 4 = formal).

The `style-tip` function generates a short textual tip shown to the user, based on occasion intensity and skin type:

```clojure
(defn style-tip
  [{:keys [skin-type occasion]}]
  (let [intensity (get occasion-intensity occasion 1)]
    (str "For the '" (or occasion "everyday") "' occasion we recommend "
         (cond
           (>= intensity 4) "a bold, long-lasting look ..."
           (= intensity 3)  "emphasized eyes and a stronger lipstick shade."
           (= intensity 2)  "a clean, natural look suitable for work."
           :else            "a light, fresh daytime look.")
         (when (= skin-type "sensitive")
           " Choose hypoallergenic, fragrance-free products."))))
```

#### 2.1.2 Product scoring (content-based)

 Instead of choosing products randomly or only by type, it assigns each product a numeric **score** expressing how well it matches the user's request. The score combines several factors:

- finish match between the product and the user's skin type (**+3.0**),
- each satisfied preference, e.g. vegan, cruelty-free, matte (**+2.0** per preference),
- product popularity expressed through its rating (**+0.5 × rating**),
- a bonus for an affordable price when the user requested budget products (**+1.0**).

`score-product` scores a single product and, besides the score, returns a list of **reasons** explaining why the product is a good fit — making the recommendation transparent. The `cond->` macro threads the value through steps only when a condition holds:

```clojure
(defn score-product
  [request product]
  (let [{:keys [skin-type preferences]} request
        tags          (tags-of product)
        prefs         (set (map ... (or preferences [])))
        finish        (get desired-finish skin-type)
        rating        (or (:rating product) 0)
        matched-prefs (set/intersection prefs tags)
        finish-hit?   (boolean (and finish (contains? tags finish)))
        score (cond-> 0.0
                finish-hit?         (+ 3.0)
                (seq matched-prefs) (+ (* 2.0 (count matched-prefs)))
                budget?             (+ 1.0)
                true                (+ (* 0.5 rating)))]
    {:score (round1 score) :reasons reasons}))
```

`rank-products` applies `score-product` to the whole catalog, sorts products by score in descending order, and returns the top six, ready for display:

```clojure
(defn rank-products
  ([request products] (rank-products request products 6))
  ([request products n]
   (->> products
        (map (fn [p]
               (let [{:keys [score reasons]} (score-product request p)]
                 (assoc p :skor score :razlozi reasons))))
        (sort-by :skor >)
        (take n)
        (map present-product))))
```

Finally, `advise` combines both approaches into one structured result: the user profile, the suggested style (from the rules), and the list of ranked products (from scoring). This result is the shape later sent to the frontend as JSON.

### 2.2 Persistence Layer

`db.clj` is responsible for storing data in a SQLite database. Database access uses the `next.jdbc` library, and SQL queries are built with `honeysql`, which lets queries be written as Clojure data structures instead of strings. The database has two tables: `products` (the product catalog) and `recommendations` (history of requests and computed recommendations). The `init!` function creates the tables if they do not exist:

```clojure
(defn init! []
  (jdbc/execute! ds ["CREATE TABLE IF NOT EXISTS products (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        brand TEXT, name TEXT NOT NULL,
                        product_type TEXT, price REAL, rating REAL,
                        tag_list TEXT, colors TEXT, ...)"]) ...)
```

Since SQLite has no list column type, product tags and colors are stored as JSON text (using the `cheshire` library) and parsed back into Clojure structures on read. `all-products` returns the whole catalog, while `save-recommendation!` records each request and its result into history, so the application remembers previous recommendations.

### 2.3 Catalog Import

`import.clj` fills the catalog with products. The primary source is the public **Makeup API**, from which about 931 products are fetched. The `normalize` function reduces a product from the API format to the internal shape the database expects (among other things, it extracts color hex values and converts the price from text to a number).

Because the Makeup API is a free, unstable service (it can be temporarily unavailable), a fault-tolerance mechanism is implemented: if the API call fails, a local fallback catalog from `resources/seed/products.edn` is used. This logic lives in `ensure-catalog!`, which fills the catalog only when the database is empty:

```clojure
(defn ensure-catalog! []
  (db/init!)
  (when (zero? (db/count-products))
    (let [products (try
                     (let [api (fetch-from-api)]
                       (if (seq api) api (load-seed)))
                     (catch Exception e
                       (load-seed)))]
      (doseq [p products] (db/insert-product! p)))))
```

Thanks to this, the application remains usable even without internet access or when the external service does not respond — an example of good practice for system reliability.

### 2.4 Web Layer (REST API)

`web.clj` exposes the application's functionality through a REST API using **Ring** and **Compojure**. Four routes are defined: a health check, returning the catalog, returning history, and the main recommendation endpoint. Middleware automatically converts JSON to Clojure maps and back, while `wrap-cors` allows the frontend (port 3000) to connect to the backend (port 3001).

```clojure
(defroutes app-routes
  (GET  "/health"    []           (response {:status "ok"}))
  (GET  "/products"  []           (response (db/all-products)))
  (GET  "/history"   []           (response (db/recent-recommendations 10)))
  (POST "/recommend" {body :body} (response (core/recommend body)))
  (route/not-found {:error "Not Found"}))
```

The `core/recommend` function, called by the `/recommend` route, ties the layers together: it loads the catalog from the database, passes it to the AI core for processing, saves the result to history, and returns it.

### 2.5 Frontend

The frontend is built with **React** (Create React App). The main interaction is a form (the `MakeupForm` component) with five fields: skin type, undertone, tone, occasion, and preferences. The form shows descriptive labels to the user but sends standardized values to the backend (e.g. `oily`, `warm`, `evening`). After the request is sent, the `ResultPanel` component displays the suggested style and the list of products, while `ProductCard` renders an individual product with its match score and recommendation reasons. Communication with the backend is isolated in the `api.js` module.

---

## 3. Data Flow

To summarize how the whole system works, consider a concrete scenario. Suppose the user selects: skin type **oily**, undertone **warm**, tone **fair**, occasion **evening**, and preferences **vegan** and **matte**. The frontend then sends:

```json
POST /recommend
{
  "skin-type": "oily",
  "undertone": "warm",
  "skin-tone": "fair",
  "occasion":  "evening",
  "preferences": ["vegan", "matte"]
}
```

The web layer receives the request and forwards it to `core/recommend`. That function loads all 931 products from the database and calls `engine/advise`. The core selects a style from the rules and then ranks products by scoring. The result:

```json
{
  "style": {
    "foundation": {"name": "Oil-free matte foundation", "description": "..."},
    "lipstick": "Rosy pink, Soft coral",
    "blush": "Peach / coral blush",
    "eyes": "Smokey eye, light glitter",
    "tip": "For the 'evening' occasion we recommend ..."
  },
  "products": [
    {"name": "...", "score": 9.4,
     "reasons": ["Finish matches skin (matte)",
                 "Matches preference: vegan", "..."]}
  ]
}
```

Before being returned, the recommendation is saved to the `recommendations` table, building up a history. The frontend then displays the style and the product cards, where each card shows the match score and reasons, making the recommendation understandable to the user.

---

## 4. Technologies

- **Clojure** — functional programming language on the JVM; used for the entire backend.
- **Leiningen** — build tool and dependency manager for Clojure projects.
- **next.jdbc** and **honeysql** — database access and programmatic SQL query building.
- **SQLite** — lightweight, file-based database.
- **clj-http** and **cheshire** — HTTP client for importing products and working with JSON.
- **Ring** and **Compojure** — libraries for the HTTP server and route definitions (REST API).
- **ring-json** and **ring-cors** — automatic JSON conversion and cross-origin access for the frontend.
- **React** (Create React App) — library for the user interface.
- **Makeup API** — public source of makeup product data, with a local fallback catalog.

---

## 5. Usage

### Prerequisites

1. Java (JDK 17)
2. [Leiningen](https://leiningen.org/)
3. [Node.js](https://nodejs.org/)

### Run the backend

```bash
cd backend
lein run
```

The backend starts on `http://localhost:3001`. On first run it creates the database and fills the product catalog.

### Run the frontend

```bash
cd frontend
npm install
npm start
```

The app opens on `http://localhost:3000`.

### REST API

```
GET  /health      health check -> {"status":"ok"}
GET  /products    full product catalog
GET  /history     last 10 recommendations
POST /recommend   main endpoint, returns a recommendation
```

### Example request

```bash
curl -X POST http://localhost:3001/recommend \
  -H "Content-Type: application/json" \
  -d "{\"skin-type\":\"oily\",\"undertone\":\"warm\",\"skin-tone\":\"fair\",\"occasion\":\"evening\",\"preferences\":[\"vegan\",\"matte\"]}"
```

---

## 6. Project Structure

```
makeupadvisor/
├── backend/                 Clojure REST API + SQLite
│   ├── src/.../engine.clj    (rules + scoring)
│   ├── src/.../core.clj      orchestration
│   ├── src/.../db.clj        database (next.jdbc + honeysql)
│   ├── src/.../import.clj    catalog import + local seed
│   ├── src/.../web.clj       REST API (Ring + Compojure)
│   ├── resources/seed/products.edn   fallback catalog
│   └── project.clj           Leiningen configuration
└── frontend/                React application (form + results)
    └── src/                  MakeupForm, ResultPanel, ProductCard, api.js
