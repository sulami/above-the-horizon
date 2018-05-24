(ns above-the-horizon.components.date-picker
  (:require [reagent.core :as r]
            [cljs-time.core :as time]
            [cljs-time.coerce :refer [to-date]]
            [above-the-horizon.components.button :refer [button-component]]
            [above-the-horizon.style :as style]
            [above-the-horizon.time :refer [format-js-time]]))

(def ReactNative (js/require "react-native"))

(def date-picker (r/adapt-react-class (.-DatePickerIOS ReactNative)))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))

(defn date-picker-component
  [date-picker-atom]
  (let [today (to-date (time/now))
        tomorrow (to-date (-> 1 time/days time/from-now))]
    [view
     [text
      {:style style/task-cell-title-style}
      (str "Due date: " (format-js-time @date-picker-atom))]
     [date-picker
      {:date @date-picker-atom
       :on-date-change #(reset! date-picker-atom %)}]
     [view {:style {:flex 1 :justify-content "space-between" :height 30 :width "100%" :flex-direction "row"}}
      [button-component
       "Clear"
       {:button {:width "33%" :height 30} :text {:font-size 15 :text-align "center"}}
       #(reset! date-picker-atom nil)]  ;; FIXME this kills the picker, we need a seperate atom for that.
      [button-component
       "Today"
       {:button {:width "33%" :height 30} :text {:font-size 15 :text-align "center"}}
       #(reset! date-picker-atom today)]
      [button-component
       "Tomorrow"
       {:button {:width "33%" :height 30} :text {:font-size 15 :text-align "center"}}
       #(reset! date-picker-atom tomorrow)]]]))
