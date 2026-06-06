(ns vestacka-inteligencija-projekat-makeupadvisor_2025_3829.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [honey.sql :as sql]
            [honey.sql.helpers :as h]
            [cheshire.core :as json])
  (:import (java.time Instant)))

(def db-spec
  {:dbtype "sqlite"
   :dbname "makeup.db"})

(def ds (jdbc/get-datasource db-spec))

(def ^:private opts {:builder-fn rs/as-unqualified-lower-maps})

(defn init! []
  (jdbc/execute! ds ["CREATE TABLE IF NOT EXISTS products (
                        id           INTEGER PRIMARY KEY AUTOINCREMENT,
                        external_id  TEXT,
                        brand        TEXT,
                        name         TEXT NOT NULL,
                        product_type TEXT,
                        price        REAL,
                        rating       REAL,
                        image_link   TEXT,
                        description  TEXT,
                        tag_list     TEXT,
                        colors       TEXT)"])
  (jdbc/execute! ds ["CREATE TABLE IF NOT EXISTS recommendations (
                        id          INTEGER PRIMARY KEY AUTOINCREMENT,
                        created_at  TEXT,
                        skin_type   TEXT,
                        undertone   TEXT,
                        skin_tone   TEXT,
                        occasion    TEXT,
                        preferences TEXT,
                        result      TEXT)"]))

(defn count-products []
  (:n (jdbc/execute-one! ds ["SELECT COUNT(*) AS n FROM products"] opts)))

(defn insert-product! [p]
  (let [q (-> (h/insert-into :products)
              (h/columns :external_id :brand :name :product_type :price
                         :rating :image_link :description :tag_list :colors)
              (h/values [[(some-> (:id p) str)
                          (:brand p)
                          (:name p)
                          (:product_type p)
                          (:price p)
                          (:rating p)
                          (:image_link p)
                          (:description p)
                          (json/generate-string (or (:tag_list p) []))
                          (json/generate-string (or (:colors p) []))]]))]
    (jdbc/execute-one! ds (sql/format q))))

(defn- parse-product [row]
  (-> row
      (update :tag_list #(when % (json/parse-string % true)))
      (update :colors   #(when % (json/parse-string % true)))))

(defn all-products []
  (->> (jdbc/execute! ds (sql/format (-> (h/select :*) (h/from :products))) opts)
       (map parse-product)))

(defn products-by-type [product-type]
  (->> (jdbc/execute! ds (sql/format (-> (h/select :*)
                                         (h/from :products)
                                         (h/where [:= :product_type product-type])))
                      opts)
       (map parse-product)))

(defn save-recommendation! [request result]
  (let [q (-> (h/insert-into :recommendations)
              (h/columns :created_at :skin_type :undertone :skin_tone
                         :occasion :preferences :result)
              (h/values [[(str (Instant/now))
                          (:skin-type request)
                          (:undertone request)
                          (:skin-tone request)
                          (:occasion request)
                          (json/generate-string (or (:preferences request) []))
                          (json/generate-string result)]]))]
    (jdbc/execute-one! ds (sql/format q))))

(defn recent-recommendations [n]
  (->> (jdbc/execute! ds (sql/format (-> (h/select :*)
                                         (h/from :recommendations)
                                         (h/order-by [:id :desc])
                                         (h/limit n)))
                      opts)
       (map (fn [r]
              (-> r
                  (update :preferences #(when % (json/parse-string % true)))
                  (update :result      #(when % (json/parse-string % true))))))))
