(ns above-the-horizon.db
  (:require [clojure.spec.alpha :as s]))

;; spec of app-db
;; (s/def ::tasks )
(s/def ::app-db
  (s/keys :req-un [::tasks]))

;; initial state of app-db
(def app-db
  {:tasks [{:name "Bring out the trash"}
           {:name "Empty the dishwasher"}]})
