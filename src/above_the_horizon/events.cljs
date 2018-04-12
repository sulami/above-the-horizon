(ns above-the-horizon.events
  (:require
   [re-frame.core :refer [reg-event-db after]]
   [schema.core :as s :include-macros true]
   [above-the-horizon.db :as db :refer [app-db]]
   [above-the-horizon.realm :as realm]
   [above-the-horizon.schema :as schema :refer [app-db-schema]]))

;; -- Interceptors ------------------------------------------------------------
;;
;; See https://github.com/Day8/re-frame/blob/master/docs/Interceptors.md
;;
(def validate-schema
  "Throw an exception if db doesn't conform to its schema."
  (if goog.DEBUG
    (after (partial s/validate schema/app-db-schema))
    []))

;; -- Handlers --------------------------------------------------------------

(reg-event-db
 :initialize-db
 validate-schema
 (fn [_ _]
   app-db))

(reg-event-db
 :save-task
 validate-schema
 (fn [db [_ task]]
   (realm/save-task task)
   (->> (realm/with-action realm/query "Task")
        (assoc db :tasks))))

(reg-event-db
 :complete-task
 validate-schema
 (fn [db [_ uid]]
   (realm/delete-task uid)
   (let [new-tasks (remove #(= uid (:uid %)) (:tasks db))]
     (assoc db :tasks new-tasks))))
