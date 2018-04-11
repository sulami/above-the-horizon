(ns above-the-horizon.schema
  (:require [schema.core :as s :include-macros true]
            [goog.date :refer [UtcDateTime]]
            [cljs-time.core :as time]))

(def schema-mapping
  "Mapping of realm types to scheam types."
  {"date" UtcDateTime
   "string" s/Str})

(defn convert-type
  [[k v]]
  "Convert a Realm type to a schema type. Deals with Realm's potential
  additional parameters."
  (let* [realm-data-type (if (map? v) (:type v) v)
         schema-data-type (get schema-mapping realm-data-type)
         is-required (-> v :optional nil?)]
    (if is-required
      [k schema-data-type]
      [(s/optional-key k) (s/maybe schema-data-type)])))

(defn realm->schema
  [realm-schema]
  "Convert Realm properties to a schema schema."
  (->> realm-schema
       :properties
       (map convert-type)
       (into {})))

(def task-schema
  {:name "Task"
   :primaryKey "uid"
   :properties {:uid "string"
                :name "string"
                :due-date {:type "date" :optional true}}})

(def complete-schema
  (clj->js {:schema [task-schema]
            :schemaVersion 4}))

(def Task
  (realm->schema task-schema))
