(ns above-the-horizon.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [cljs-react-navigation.reagent :refer [stack-navigator stack-screen]]
            [cljs-time.core :as time]
            [cljs-time.coerce :refer [to-date]]
            [cljs-time.format :as tformat]
            [schema.core :as s :include-macros true]
            [above-the-horizon.events]
            [above-the-horizon.realm :as realm]
            [above-the-horizon.schema :refer [Task]]
            [above-the-horizon.style :as style]
            [above-the-horizon.subs]))

(def ReactNative (js/require "react-native"))
(def app-registry (.-AppRegistry ReactNative))
(def date-picker (r/adapt-react-class (.-DatePickerIOS ReactNative)))
(def scroll-view (r/adapt-react-class (.-ScrollView ReactNative)))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def text-input (r/adapt-react-class (.-TextInput ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableOpacity ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))

(def ReactNaviagtion (js/require "react-navigation"))
(def safe-area-view (r/adapt-react-class (.-SafeAreaView ReactNaviagtion)))

(defn alert [message]
  "Trigger an alert with `message`"
  (.alert (.-Alert ReactNative) message))

(defn button-component
  [display-text button-style action]
  [touchable-highlight {:style (:button button-style)
                        :on-press action
                        :key display-text}
   [text
    {:style (:text button-style)}
    display-text]])

(s/defn ^:always-validate task-cell-component
  [navigate
   task :- Task]
  [view {:style style/task-cell-container-style :key (:uid task)}
   [button-component
    "O"
    style/task-cell-checkbox-style
    #(dispatch [:complete-task (:uid task)])]
   [touchable-highlight {:key (:uid task)
                         :style style/task-cell-right-touch-style
                         :on-press #(navigate "NewTask" {:task task})}
    [view {:style style/task-cell-right-container-style}
     [text {:style style/task-cell-title-style} (:name task)]
     [text {:style (if (and (-> task :due-date nil? not)
                            (time/after? (time/now) (:due-date task)))
                     style/task-cell-due-date-overdue-style
                     style/task-cell-due-date-style)}
      (if (:due-date task)
        (tformat/unparse (tformat/formatters :mysql) (:due-date task))
        "No due date")]]]])

(defn format-time
  [dt]
  "Format a cljs datetime."
  (tformat/unparse (tformat/formatters :mysql) dt))

(defn format-js-time
  [dt]
  "Format a JS Date."
  (-> dt cljs-time.coerce/from-date format-time))

(defn today-view [props]
  (fn []
    (let [navigate (-> props :navigation :navigate)
          get-param (-> props :navigation :getParam)
          tasks (subscribe [:get-tasks])]
      [safe-area-view {:style style/view-style}
       [scroll-view
        (map (partial task-cell-component navigate) @tasks)]
       [view {:style style/action-bar-style}
        [button-component "+" style/new-task-button-style #(navigate "NewTask")]]])))

(defn date-picker-component
  [date-picker-atom]
  (let [today (cljs-time.coerce/to-date (time/now))
        tomorrow (cljs-time.coerce/to-date (time/plus (time/now) (time/days 1)))]
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

(defn task-view [props]
  (let* [go-back (-> props :navigation :goBack)
         task (-> props :navigation :state :params :task)
         is-new-task (nil? task)
         task-uid (if is-new-task nil (:uid task))
         name-value (r/atom (if is-new-task "" (:name task)))
         due-date-value (r/atom (cljs-time.coerce/to-date
                                 (or (:due-date task) (time/now))))]
    (fn []
      [safe-area-view {:style style/view-style}
       [text-input
        {:style style/textbox-style
         :maxLength 255
         :placeholder "Task Name"
         :returnKeyType "done"
         :enablesReturnKeyAutomatically true
         :autoFocus is-new-task
         :on-change-text #(reset! name-value %)}
        @name-value]
       [date-picker-component due-date-value]
       [view {:style style/action-bar-style}
        [button-component "Cancel" style/cancel-button-style #(go-back)]
        [button-component "Save" style/save-button-style (fn []
                                                      (dispatch [:save-task {:uid task-uid
                                                                             :name @name-value
                                                                             :due-date @due-date-value}])
                                                      (go-back))]]])))

(def stack-router
  {:Today {:screen (stack-screen today-view)}
   :NewTask {:screen (stack-screen task-view)}})

(def stack-nav
  (stack-navigator stack-router {:headerMode "none"}))

(defn app-root []
  [:> stack-nav {}])

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent
   app-registry
   "AboveTheHorizon"
   #(r/reactify-component app-root)))
