(ns above-the-horizon.schema)

(def task-schema
  {:name "Task"
   :primaryKey "uid"
   :properties {:uid "string"
                :name "string"}})

(def complete-schema
  (clj->js {:schema [task-schema]
            :schemaVersion 2}))
