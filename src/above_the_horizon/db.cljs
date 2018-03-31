(ns above-the-horizon.db
  (:require [clojure.spec.alpha :as sp]
            [schema.core :as s :include-macros true]
            [above-the-horizon.realm :as ar]))

(defn do-stuff []
  (ar/action (fn [r]
               (.write r (fn []
                           (->> (.objects r "Task")
                                (.delete r))))
               (ar/query r "Task"))))

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
