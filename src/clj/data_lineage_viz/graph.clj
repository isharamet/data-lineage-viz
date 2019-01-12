(ns data-lineage-viz.graph
  (:require [loom.graph :as g]
            [loom.attr :as gattr]
            [loom.alg :as galg]
            [loom.io :as gio]            
            [clojure.data.codec.base64 :as b64]))

(defn extract-tf-edges [tf]
  (let [[_ attrs] tf
        input (:input attrs)
        output (:output attrs)]
    (for [i input
          o output]
      [i o])))

(defn extract-edges [data-spec]
  (mapcat extract-tf-edges (:data-transformations data-spec)))

(defn extract-nodes-attrs [data-spec]
  (map (fn [[k v]] [k (select-keys v [:label])]) (:data data-spec)))

(defn extract-tf-edges-attrs [tf]
  (let [[_ attrs] tf
        input (:input attrs)
        output (:output attrs)]
    (for [i input
          o output]
      [[i o] (select-keys attrs [:label])])))

(defn extract-edges-attrs [data-spec]
  (mapcat extract-tf-edges-attrs (:data-transformations data-spec)))

(defn add-attrs [graph target attrs]
  (reduce (fn [acc [k v]] (gattr/add-attr acc target k v))
          graph
          attrs))

(defn add-targets-attrs [graph targets-attrs]
  (reduce (fn [acc [t attrs]] (add-attrs acc t attrs))
          graph
          targets-attrs))

(defn add-attr-to-all-nodes [graph k v]
  (gattr/add-attr-to-nodes graph k v (g/nodes graph)))

(defn traceable-nodes [graph node]
  (set (concat
        (galg/pre-traverse (g/transpose graph) node)
        (galg/pre-traverse graph node))))

(defn filter-graph [graph node]
  (let [nodes (traceable-nodes graph node)
        decorated-graph (gattr/add-attr graph node :style "filled")]
    (g/subgraph decorated-graph nodes)))

(defn build-graph [data-spec]
  (-> (apply g/digraph (extract-edges data-spec))
      (add-targets-attrs (extract-nodes-attrs data-spec))
      (add-targets-attrs (extract-edges-attrs data-spec))
      (add-attr-to-all-nodes :shape "polygon")))


(defn render-to-encoded-string [graph]
  (let [graph-bytes (gio/render-to-bytes graph :fmt :svg)
        encoded-bytes (b64/encode graph-bytes)]
    (apply str (map char encoded-bytes))))

;;------

;; (def spec (reader/build-spec reader/spec-files))

;; (def my-graph (build-graph spec))

;; (gio/view my-graph :fmt :svg)

;;------















