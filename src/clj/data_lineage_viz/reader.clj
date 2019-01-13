(ns data-lineage-viz.reader
  (:require [clojure.edn :as edn]
            [data-lineage-viz.validator :as validator]))

(defn read-spec [spec-resource]
  (edn/read-string (slurp spec-resource)))

(defn read-specs [spec-resources]
  (map read-spec spec-resources))

(defn merge-specs [a b]
  (merge-with into a b))

(defn build-spec [spec-resources]
  (reduce merge-specs {} (read-specs spec-resources)))

