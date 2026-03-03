(defproject vestacka-inteligencija-projekat-makeupadvisor_2025_3829 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.4"]
                 [com.github.seancorfield/next.jdbc "1.3.1086"]
                 [org.xerial/sqlite-jdbc "3.51.1.0"]
                 [com.github.seancorfield/honeysql "2.7.1364"]]
  :repl-options {:init-ns vestacka-inteligencija-projekat-makeupadvisor-2025-3829.core}
  :profiles
  {:dev {:dependencies [[midje "1.10.10"]]
         :plugins [[lein-midje "3.2.1"]]}})