(ns above-the-horizon.schema)

(def task-schema
  {:name "Task"
   :primaryKey "id"
   :properties {:id "int"
                :name "string"}})

(def complete-schema
  (clj->js {:schema [task-schema]
            :schemaVersion 1}))
