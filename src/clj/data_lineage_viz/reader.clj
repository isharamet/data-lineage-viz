(ns data-lineage-viz.reader
  (:require [clojure.edn :as edn]
            [data-lineage-viz.validator :as validator]))

(def spec-files
  ["resources/dummy-spec/data-types.edn"
   "resources/dummy-spec/data-a.edn"
   "resources/dummy-spec/transformation-a.edn"
   "resources/dummy-spec/transformation-b.edn"])

(defn read-spec [spec-resource]
  (edn/read-string (slurp spec-resource)))

(defn read-specs [spec-resources]
  (map read-spec spec-resources))

(defn merge-specs [a b]
  (merge-with into a b))

(defn build-spec [spec-resources]
  (reduce merge-specs {} (read-specs spec-files)))

;;(def my-spec (build-spec spec-files))

;;(clojure.pprint/pprint my-spec)

;;(validator/valid? my-spec)

