(ns above-the-horizon.db
  (:require [above-the-horizon.realm :as r]))

;; initial state of app-db
(def app-db
  {:tasks (r/with-action r/query "Task")})
