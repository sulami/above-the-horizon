(ns above-the-horizon.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :get-tasks
  (fn [db _]
    (:tasks db)))
