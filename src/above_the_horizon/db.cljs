(ns above-the-horizon.db
  (:require [clojure.spec.alpha :as s]
            [above-the-horizon.realm :as ar]))

;; spec of app-db
;; (s/def ::tasks )
(s/def ::app-db
  (s/keys :req-un [::tasks]))

;; initial state of app-db
(def app-db
  {:tasks (ar/with-action ar/query "Task")})
