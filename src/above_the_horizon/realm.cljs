(ns above-the-horizon.realm
  (:require [cljs-uuid.core :as uuid]
            [schema.core :as s :include-macros true]
            [above-the-horizon.schema :as schema]))

(def realm (js/require "realm"))

;;
;; Utils
;;

(defn jsx->clj
  "Convert JSX data to Clojure data."
  [x]
  (if (map? x)
    x
    (into {} (for [k (.keys js/Object x)]
               [(keyword k)
                (let [v (aget x k)]
                  (if (instance? realm.List v)
                    (map jsx->clj (array-seq v))
                    v))]))))

;;
;; Abstract
;;

(defn action
  "Decorator to run a function in a Realm context, closing the DB afterwards."
  [func]
  (let* [r (new realm schema/complete-schema)
         result (func r)]
    (.close r)
    result))

(defn with-action
  "Run a database operation inside `above-the-horizon.realm/action`."
  [func & args]
  (action #(apply (partial func %) args)))

(defn query
  "Get all rows in a table, converted to Clojure."
  [r table]
  (some->> (.objects r table)
           array-seq
           (mapv jsx->clj)))

(defn insert
  "Insert a row into a table."
  [r table data]
  (.write r #(.create r table (clj->js data) true)))

(defn flush
  "Empty a table."
  [r table]
  (.write r #(->> (.objects r table)
                  (.delete r))))

;;
;; Specific
;;

(s/defn save-task
  "Save an existing task or create a new one."
  ^:always-validate
  [task]
  (->> (assoc task :uid (or (:uid task) (str (uuid/make-random))))
       (with-action insert "Task")))

(defn delete-task
  "Delete a task from Realm."
  [uid]
  (prn (str "Deleting Task " uid))
  (action
   (fn [r]
     (.write
      r
      (fn []
        (->> (-> (.objects r "Task")
                 (.filtered  (str "uid = \"" uid "\"")))
             (.delete r)))))))
