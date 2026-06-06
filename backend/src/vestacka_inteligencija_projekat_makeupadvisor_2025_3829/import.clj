(ns vestacka-inteligencija-projekat-makeupadvisor_2025_3829.import
  (:require [clj-http.client :as http]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [vestacka-inteligencija-projekat-makeupadvisor_2025_3829.db :as db]))

(def makeup-url
  "https://makeup-api.herokuapp.com/api/v1/products.json")

(defn- parse-price [v]
  (cond
    (number? v) (double v)
    (string? v) (try (Double/parseDouble v) (catch Exception _ nil))
    :else nil))

(defn- normalize
  "Reduces a product from the Makeup API format to the internal shape."
  [p]
  {:id           (:id p)
   :brand        (:brand p)
   :name         (:name p)
   :product_type (:product_type p)
   :price        (parse-price (:price p))
   :rating       (:rating p)
   :image_link   (:image_link p)
   :description  (:description p)
   :tag_list     (vec (:tag_list p))
   :colors       (mapv :hex_value (:product_colors p))})

(defn fetch-from-api
  "Returns a list of normalized products from the API, or throws."
  []
  (->> (http/get makeup-url {:as :json
                             :socket-timeout 30000
                             :connection-timeout 30000})
       :body
       (map normalize)))

(defn load-seed
  "Loads the local fallback catalog (already in the internal shape)."
  []
  (-> (io/resource "seed/products.edn")
      slurp
      edn/read-string))

(defn ensure-catalog!
  "Makes sure the DB is initialized and the catalog is not empty.
   Returns the number of loaded products (or nil if already populated)."
  []
  (db/init!)
  (when (zero? (db/count-products))
    (let [products (try
                     (let [api (fetch-from-api)]
                       (if (seq api)
                         (do (println "Catalog imported from the Makeup API.") api)
                         (do (println "API empty – using local seed.") (load-seed))))
                     (catch Exception e
                       (println "API unavailable (" (.getMessage e) ") – using local seed.")
                       (load-seed)))]
      (doseq [p products]
        (db/insert-product! p))
      (println "Loaded products:" (count products))
      (count products))))