(ns above-the-horizon.components.date-picker
  (:require [reagent.core :as r]
            [cljs-time.core :as time]
            [cljs-time.coerce :refer [to-date]]
            [above-the-horizon.components.button :refer [button]]
            [above-the-horizon.style :as style]
            [above-the-horizon.time :refer [format-js-time on-js-time]]))

(def ReactNative (js/require "react-native"))

(def date-picker-ios (r/adapt-react-class (.-DatePickerIOS ReactNative)))
(def layout-animation (r/adapt-react-class (.-LayoutAnimation ReactNative)))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def touchable-opacity (r/adapt-react-class (.-TouchableOpacity ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))

(defn date-picker
  [date-atom]
  (r/with-let [picker-expanded (r/atom false)
               date-picker-position (r/atom (or @date-atom
                                                (to-date (time/now))))]

    ; Keep the two atoms in sync, more or less. Collapse the picker when
    ; clearing the date.
    (add-watch
     date-atom
     :date-sync
     (fn [key atom old-state new-state]
       (if (nil? new-state)
         (do (layout-animation.name.easeInEaseOut)
             (reset! picker-expanded false)
             (reset! date-picker-position (to-date (time/now))))
         (reset! date-picker-position new-state))))

    (let [today (to-date (time/now))
          tomorrow (-> 1 time/days time/from-now to-date)]
      [view

       ; This is the title row.
       [touchable-opacity
        {:style style/date-picker-title-bar-style
         :on-press (fn []
                     (when (and (nil? @date-atom)
                                (not @picker-expanded))
                       (reset! date-atom today))
                     (layout-animation.name.easeInEaseOut)
                     (swap! picker-expanded not))}
        [text {:style style/date-picker-title-style} "Due date:"]
        [text {:style style/date-picker-title-style}
         (if (nil? @date-atom)
           "No due date"
           (format-js-time @date-atom))]]

       ; This is the collapsable bottom section with the picker.
       [view {:style {:overflow "hidden"
                      :height (if @picker-expanded "auto" 0)}}
        [date-picker-ios
         {:date @date-picker-position
          :on-date-change #(reset! date-atom %)}]
        [view {:style style/date-picker-collapsible-view-style}
         [button "Clear" style/date-picker-button-style #(reset! date-atom nil)]
         [button "Today" style/date-picker-button-style #(reset! date-atom today)]
         [button "Tomorrow" style/date-picker-button-style #(reset! date-atom tomorrow)]
         [button "+ 1 day" style/date-picker-button-style #(swap! date-atom (fn [old]
                                                                                        (on-js-time (fn [dt]
                                                                                                      (time/plus dt (time/days 1)))
                                                                                                    old)))]
         [button "+ 1 week" style/date-picker-button-style #(swap! date-atom (fn [old]
                                                                                         (on-js-time (fn [dt]
                                                                                                       (time/plus dt (time/weeks 1)))
                                                                                                     old)))]
         [button "+ 1 month" style/date-picker-button-style #(swap! date-atom (fn [old]
                                                                                          (on-js-time (fn [dt]
                                                                                                        (time/plus dt (time/months 1)))
                                                                                                      old)))]]]])))
