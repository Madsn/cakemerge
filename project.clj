(defproject
  mergecake
  "0.1.1-SNAPSHOT"
  :repl-options
  {:init-ns mergecake.repl}
  :dependencies
  [[ring-server "0.3.1"]
   [com.h2database/h2 "1.3.175"]
   [com.taoensso/timbre "3.0.0"]
   [environ "0.4.0"]
   [markdown-clj "0.9.41"]
   [korma "0.3.0-RC6"]
   [http-kit "2.1.13"]
   [selmer "0.6.1"]
   [clojure-csv/clojure-csv "2.0.1"]
   [com.taoensso/tower "2.0.1"]
   [org.clojure/clojure "1.5.1"]
   [log4j
    "1.2.17"
    :exclusions
    [javax.mail/mail
     javax.jms/jms
     com.sun.jdmk/jmxtools
     com.sun.jmx/jmxri]]
   [lib-noir "0.8.1"]
   [compojure "1.1.6"]
   [com.draines/postal "1.11.1"]]
  :ring
  {:handler mergecake.handler/app,
   :init mergecake.handler/init,
   :destroy mergecake.handler/destroy}
  :profiles
  {:uberjar {:aot :all},
   :production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}},
   :dev
   {:dependencies [[ring-mock "0.1.5"] [ring/ring-devel "1.2.1"]],
    :env {:dev true}}}
  :url
  "None"
  :main
  mergecake.core
  :plugins
  [[lein-ring "0.12.5"] [lein-environ "0.4.0"]]
  :description
  "Cakemerge - avoid conflicting cakedays"
  :min-lein-version "2.0.0")