(ns vestacka-inteligencija-projekat-makeupadvisor_2025_3829.import
  :require [clj-http.client :as http]
   [next.jdbc :as jdbc]
   [honeysql.core :as hsql]
   [honeysql.helpers :as h]
  [vestacka-inteligencija-projekat-makeupadvisor_2025_3829.db :refer [ds]]
  )

(def makeup-url
  "http://makeup-api.herokuapp.com/api/v1/products.json")

(defn fetch-products []
  (-> (http/get makeup-url {:as :json})
      :body))
