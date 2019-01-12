(ns data-lineage-viz.middleware
  (:require [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [reitit.ring.middleware.muuntaja :as muuntaja]))

(def middleware
  [#(wrap-defaults % site-defaults)
   muuntaja/format-middleware])
