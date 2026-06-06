(ns vestacka-inteligencija-projekat-makeupadvisor_2025_3829.web

  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.util.response :refer [response]]
            [ring.middleware.cors :refer [wrap-cors]]
            [vestacka-inteligencija-projekat-makeupadvisor_2025_3829.core :as core]
            [vestacka-inteligencija-projekat-makeupadvisor_2025_3829.db :as db]
            [vestacka-inteligencija-projekat-makeupadvisor_2025_3829.import :as import]))

(defroutes app-routes
           (GET  "/health"    []           (response {:status "ok"}))
           (GET  "/products"  []           (response (db/all-products)))
           (GET  "/history"   []           (response (db/recent-recommendations 10)))
           (GET "/products/:type" [type] (response (db/products-by-type type)))
           (POST "/recommend" {body :body} (response (core/recommend body)))
           (route/not-found {:error "Not Found"}))

(def app
  (-> app-routes
      (wrap-json-body {:keywords? true})
      wrap-json-response
      (wrap-cors :access-control-allow-origin [#"http://localhost:3000"]
                 :access-control-allow-methods [:get :post :options])))

(defn -main [& _args]
  (import/ensure-catalog!)
  (println "MakeupAdvisor backend listening on http://localhost:3001")
  (run-jetty app {:port 3001 :join? false}))