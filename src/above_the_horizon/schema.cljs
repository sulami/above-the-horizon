(ns above-the-horizon.schema
  (:require [schema.core :as s :include-macros true]
            [cljs-time.core :as time]))

(def schema-mapping
  "Mapping of realm types to scheam types"
  {"string" s/Str})

(defn realm->schema
  [props]
  "Convert Realm properties to a schema schema"
  (let [convert-type (fn [[k v]] [k s/Str])]
    (->> props
         (map convert-type)
         (into {}))))

(def task-schema
  {:name "Task"
   :primaryKey "uid"
   :properties {:uid "string"
                :name "string"}})

(def Task
  (realm->schema (:properties task-schema)))

(def complete-schema
  (clj->js {:schema [task-schema]
            :schemaVersion 2}))
