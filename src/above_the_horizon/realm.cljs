(ns above-the-horizon.realm
  (:require [above-the-horizon.schema :as s]))

(def realm (js/require "realm"))

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

(defn action
  "Decorator to run a function in a Realm context, closing the DB afterwards."
  [func]
  (let* [r (new realm s/complete-schema)
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
  (.write r #(.create r table (clj->js data))))

(defn flush
  "Empty a table."
  [r table]
  (.write r #(->> (.objects r table)
                  (.delete r))))
