(ns data-lineage-viz.prod
  (:require [data-lineage-viz.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
