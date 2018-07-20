(ns above-the-horizon.components.task-cell
  (:require [reagent.core :as r]
            [re-frame.core :refer [dispatch]]
            [schema.core :as s :include-macros true]
            [cljs-time.core :as time]
            [above-the-horizon.components.button :refer [button]]
            [above-the-horizon.schema :refer [Task]]
            [above-the-horizon.style :as style]
            [above-the-horizon.time :refer [format-time]]))

(def ReactNative (js/require "react-native"))

(def text (r/adapt-react-class (.-Text ReactNative)))
(def touchable-opacity (r/adapt-react-class (.-TouchableOpacity ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))

(s/defn ^:always-validate task-cell
  [navigate
   task :- Task]
  [view {:style style/task-cell-container-style :key (:uid task)}
   [button
    "O"
    style/task-cell-checkbox-style
    #(dispatch [:complete-task (:uid task)])]
   [touchable-opacity {:key (:uid task)
                         :style style/task-cell-right-touch-style
                         :on-press #(navigate "NewTask" {:task task})}
    [view {:style style/task-cell-right-container-style}
     [text {:style style/task-cell-title-style} (:name task)]
     [text {:style (if (and (-> task :due-date nil? not)
                            (time/after? (time/now) (:due-date task)))
                     style/task-cell-due-date-overdue-style
                     style/task-cell-due-date-style)}
      (if (:due-date task)
        (format-time (:due-date task))
        "")]]]])
