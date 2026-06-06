(ns vestacka-inteligencija-projekat-makeupadvisor_2025_3829.core

  (:require [vestacka-inteligencija-projekat-makeupadvisor_2025_3829.engine :as engine]
            [vestacka-inteligencija-projekat-makeupadvisor_2025_3829.db :as db]))


(def recommend-foundation engine/recommend-foundation)
(def recommend-lipstick   engine/recommend-lipstick)

(defn recommend
  [request]
  (let [products (db/all-products)
        result   (engine/advise request products)]
    (db/save-recommendation! request result)
    result))