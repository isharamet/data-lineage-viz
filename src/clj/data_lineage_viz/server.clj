(ns data-lineage-viz.server
    (:require [data-lineage-viz.handler :refer [app]]
              [config.core :refer [env]]
              [ring.adapter.jetty :refer [run-jetty]])
    (:gen-class))

(defn -main [& args]
  (let [port (or (env :port) 3000)]
    (run-jetty app {:port port :join? false})))
