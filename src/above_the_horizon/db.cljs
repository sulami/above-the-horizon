(ns above-the-horizon.db
  (:require [clojure.spec.alpha :as sp]
            [schema.core :as s :include-macros true]))

(def realm (js/require "realm"))

(defn jsx->clj
  [x]
  (if (map? x)
    x
    (into {} (for [k (.keys js/Object x)]
               [(keyword k)
                (let [v (aget x k)]
                  (if (instance? realm.List v)
                    (map jsx->clj (array-seq v))
                    v))]))))

(defn query
  [r table]
  (some->> (.objects r table)
           array-seq
           (mapv jsx->clj)))

(defn do-stuff []
  (let* [schema [{:name "Task", :properties {:name "string"}}]
         r (new realm (clj->js {:schema schema}))
         old-tasks (.objects r "Task")
         new-task (clj->js {:name "Fly away"})]
    (.write r (fn []
                (.delete r old-tasks)
                (.create r "Task" (clj->js {:name "something esle"}))
                (.create r "Task" new-task)))
    (let [got-task (query r "Task")]
      (prn got-task)
      (.close r)
      got-task)))

;; spec of app-db
;; (sp/def ::tasks )
(sp/def ::app-db
  (sp/keys :req-un [::tasks]))

;; Task schema
(def Task
  "A schema for tasks"
  {:name s/Str})

;; initial state of app-db
(def app-db
  {:tasks [{:name "Bring out the trash"}
           {:name "Empty the dishwasher"}]})
