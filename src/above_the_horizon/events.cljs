(ns above-the-horizon.events
  (:require
   [re-frame.core :refer [reg-event-db after]]
   [clojure.spec.alpha :as s]
   [above-the-horizon.db :as db :refer [app-db]]
   [above-the-horizon.realm :as realm]))

;; -- Interceptors ------------------------------------------------------------
;;
;; See https://github.com/Day8/re-frame/blob/master/docs/Interceptors.md
;;
(defn check-and-throw
  "Throw an exception if db doesn't have a valid spec."
  [spec db [event]]
  (when-not (s/valid? spec db)
    (let [explain-data (s/explain-data spec db)]
      (throw (ex-info (str "Spec check after " event " failed: " explain-data) explain-data)))))

(def validate-spec
  (if goog.DEBUG
    (after (partial check-and-throw ::db/app-db))
    []))

;; -- Handlers --------------------------------------------------------------

(reg-event-db
 :initialize-db
 validate-spec
 (fn [_ _]
   app-db))

(reg-event-db
 :save-task
 validate-spec
 (fn [db [_ task]]
   (realm/create-task "")
   (->> (realm/with-action realm/query "Task")
        (assoc db :tasks))))

(reg-event-db
 :complete-task
 validate-spec
 (fn [db [_ uid]]
   (realm/delete-task uid)
   (let [new-tasks (remove #(= uid (:uid %)) (:tasks db))]
     (assoc db :tasks new-tasks))))
