(ns ^:figwheel-no-load data-lineage-viz.dev
  (:require
    [data-lineage-viz.core :as core]
    [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(core/init!)
