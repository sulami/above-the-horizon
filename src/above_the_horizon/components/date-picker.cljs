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
(def touchable-opacity (r/adapt-react-class (.-TouchableOpacity ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))

(def title-view-style
  {:flex 0
   :flex-direction "row"
   :justify-content "space-between"
   :width "100%"
   :height 40
   :padding-horizontal 10
   :border-color "#eee"
   :border-width 1})

(def title-style
  {:font-size 16
   :height 40
   :line-height 40
   :text-align "center"})

(def view-style
  {:flex 1
   :justify-content "center"
   :height 30
   :width "100%"
   :flex-direction "row"
   :flex-wrap "wrap"})

(def button-style
  {:button {:width "33%"
            :border-color "#eee"
            :border-width 0.5
            :height 40}
   :text {:font-size 15
          :line-height 40
          :text-align "center"}})

(defn date-picker-component
  [date-atom]
  (r/with-let [picker-expanded (r/atom false)
               date-picker-position (r/atom (or @date-atom (to-date (time/now))))]
    (add-watch date-atom :date-sync
               (fn [key atom old-state new-state]
                 (if (nil? new-state)
                   (do
                     (reset! picker-expanded false)
                     (reset! date-picker-position (to-date (time/now))))
                  (reset! date-picker-position new-state))))
    (let [today (to-date (time/now))
          tomorrow (to-date (-> 1 time/days time/from-now))]
      [view
       [touchable-opacity {:style title-view-style
                           :on-press (fn []
                                       (when (and (nil? @date-atom)
                                                  (not @picker-expanded))
                                         (reset! date-atom today))
                                       (swap! picker-expanded not))}
        [text {:style title-style} "Due date:"]
        [text {:style title-style} (if (nil? @date-atom) "No due date" (format-js-time @date-atom))]]
       [view {:style {:display (if @picker-expanded "flex" "none")}}
        [date-picker
         {:date @date-picker-position
          :on-date-change #(reset! date-atom %)}]
        [view {:style view-style}
         [button-component "Clear" button-style #(reset! date-atom nil)]
         [button-component "Today" button-style #(reset! date-atom today)]
         [button-component "Tomorrow" button-style #(reset! date-atom tomorrow)]
         [button-component "+ 1 day" button-style #(reset! date-atom nil)]
         [button-component "+ 1 week" button-style #(reset! date-atom today)]
         [button-component "+ 1 month" button-style #(reset! date-atom tomorrow)]]]])))
