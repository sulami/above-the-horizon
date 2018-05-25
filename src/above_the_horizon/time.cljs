(ns above-the-horizon.time
  (:require [cljs-time.coerce :refer [from-date to-date]]
            [cljs-time.format :refer [formatters unparse]]))

(defn format-time
  [dt]
  "Format a cljs datetime."
  (unparse (formatters :mysql) dt))

(defn format-js-time
  [dt]
  "Format a JS Date."
  (-> dt from-date format-time))

(defn on-js-time
  [f dt]
  "Run cljs.time operations on a JS Date."
  (-> dt from-date f to-date))
