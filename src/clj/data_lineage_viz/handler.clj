(ns data-lineage-viz.handler
  (:require [reitit.ring :as reitit-ring]
            [data-lineage-viz.middleware :refer [middleware]]
            [hiccup.page :refer [include-js include-css html5]]
            [config.core :refer [env]]
            [muuntaja.core :as m]
            [data-lineage-viz.reader :as reader]
            [data-lineage-viz.graph :as graph]))

(def data-spec (reader/build-spec (env :data-spec-resources)))

(def lineage-graph (graph/build-graph data-spec))

(defn group-nodes [group nodes]
  (into {}
        (filter
         (fn [[node-key node]]
           (= (:data-type node) group))
         nodes)))

(def menu-items
  (let [groups (:data-types data-spec)
        nodes (:data data-spec)]
    (map (fn [[group-key group]]
           {group-key
            (assoc group :items (group-nodes group-key nodes))})
         groups)))

(def mount-target
  [:div#app
   [:h2 "Loading..."]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css
    "https://fonts.googleapis.com/css?family=Montserrat"
    "https://unpkg.com/purecss@1.0.0/build/pure-min.css"
    (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
   (head)
   [:body {:class "body-container"}
    mount-target
    (include-js "/js/app.js")]))

(defn index-handler
  [_request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (loading-page)})

(defn menu-items-handler
  [_request]
  {:status 200
   :body menu-items})

(defn graph-handler
  [_request]
  {:status 200
   :body {:graph (graph/render-to-encoded-string lineage-graph)}})

(defn subgraph-handler
  [_request]
  (let [node (keyword (:node (:path-params _request)))]
    {:status 200
     :body  {:graph (graph/render-to-encoded-string
                     (graph/filter-graph lineage-graph node))}}))

(def app
  (reitit-ring/ring-handler
   (reitit-ring/router
    [["/" {:get {:handler index-handler}}]
     ["/menu-items" {:get {:handler menu-items-handler}}]
     ["/graph"
      ["" {:get {:handler graph-handler}}]
      ["/:node" {:get {:handler subgraph-handler
                       :parameters {:path {:node string?}}}}]]]
    {:data {:muuntaja m/instance
            :middleware middleware}})
   (reitit-ring/routes
    (reitit-ring/create-resource-handler {:path "/" :root "/public"})
    (reitit-ring/create-default-handler))))
