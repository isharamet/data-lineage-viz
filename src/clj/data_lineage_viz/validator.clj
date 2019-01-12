(ns data-lineage-viz.validator
  (:require [clojure.spec.alpha :as s]))

(s/def :data/label string?)
(s/def :data/data-type keyword?)

(s/def :data-type/label string?)

(s/def :data-transformation/label string?)
(s/def :data-transformation/input (s/coll-of keyword?))
(s/def :data-transformation/output (s/coll-of keyword?))

(s/def ::data
  (s/map-of keyword? (s/keys :req-un [:data/label
                                      :data/data-type])))

(s/def ::data-types
  (s/map-of keyword? (s/keys :req-un [:data-type/label])))

(s/def ::data-transformations
  (s/map-of keyword? (s/keys :req-un [:data-transformation/label
                                      :data-transformation/input
                                      :data-transformation/output])))
(s/def ::data-spec
  (s/keys :req-un [(or ::data ::data-types ::data-transformations)]))

(defn valid? [data-spec]
  (let [validation-result (s/valid? ::data-spec data-spec)]
    (when-not validation-result (s/explain ::data-spec data-spec))
    validation-result))
