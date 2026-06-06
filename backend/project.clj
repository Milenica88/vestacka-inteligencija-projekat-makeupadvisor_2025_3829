(defproject vestacka-inteligencija-projekat-makeupadvisor_2025_3829 "0.2.0-SNAPSHOT"
  :description "Makeup Advisor"
  :url "https://github.com/Milenica88/vestacka-inteligencija-projekat-makeupadvisor_2025_3829"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.5"]
                 [com.github.seancorfield/next.jdbc "1.3.1108"]
                 [org.xerial/sqlite-jdbc "3.51.1.0"]
                 [com.github.seancorfield/honeysql "2.7.1364"]
                 [clj-http "3.13.0"]
                 [cheshire "5.13.0"]
                 [compojure "1.7.1"]
                 [ring/ring-core "1.12.2"]
                 [ring/ring-jetty-adapter "1.12.2"]
                 [ring/ring-json "0.5.1"]
                 [ring-cors "0.1.13"]]
  :main ^:skip-aot vestacka-inteligencija-projekat-makeupadvisor_2025_3829.web
  :target-path "target/%s"
  :repl-options {:init-ns vestacka-inteligencija-projekat-makeupadvisor_2025_3829.core}
  :profiles
  {:dev {:dependencies [[midje "1.10.10"]]
         :plugins [[lein-midje "3.2.1"]]}})