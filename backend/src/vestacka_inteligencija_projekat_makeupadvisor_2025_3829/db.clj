(ns vestacka-inteligencija-projekat-makeupadvisor_2025_3829.db
  :require [next.jdbc :as jdbc]
  [honeysql.core :as sql]
  [honeysql.helpers :as h])

(def db-spec {:dbtype "sqlite"
              :dbname "resources/makeup.db"})

(def ds (jdbc/get-datasource db-spec))

(defn kreiraj-tabele! []
  (jdbc/execute! ds ["CREATE TABLE IF NOT EXISTS users (
                       id INTEGER PRIMARY KEY AUTOINCREMENT,
                       name TEXT NOT NULL
                     )"]))

(defn ubaci-proizvod!
  [name skintype tone]
  (let [q (-> (h/insert-into :products)
              (h/columns :name :skintype :tone)
              (h/values [[name skintype tone]]))]
    (jdbc/execute! ds (hsql/format q))))


(defn nadji-po-kozi [t]
  (let [q (-> (h/select :*)
              (h/from :products)
              (h/where [:= :skintype t]))]
    (jdbc/execute! ds (hsql/format q))))
