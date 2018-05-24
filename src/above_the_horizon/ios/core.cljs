(ns above-the-horizon.ios.core
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [cljs-react-navigation.reagent :refer [stack-navigator stack-screen]]
            [cljs-time.core :as time]
            [cljs-time.coerce :refer [to-date]]
            [above-the-horizon.components.button :refer [button-component]]
            [above-the-horizon.components.date-picker :refer [date-picker-component]]
            [above-the-horizon.components.task-cell :refer [task-cell-component]]
            [above-the-horizon.events]
            [above-the-horizon.realm :as realm]
            [above-the-horizon.style :as style]
            [above-the-horizon.subs]))

(def ReactNative (js/require "react-native"))

(def app-registry (.-AppRegistry ReactNative))
(def scroll-view (r/adapt-react-class (.-ScrollView ReactNative)))
(def text-input (r/adapt-react-class (.-TextInput ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))

(def ReactNaviagtion (js/require "react-navigation"))
(def safe-area-view (r/adapt-react-class (.-SafeAreaView ReactNaviagtion)))

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

(defn task-view [props]
  (let* [go-back (-> props :navigation :goBack)
         task (-> props :navigation :state :params :task)
         is-new-task (nil? task)
         task-uid (if is-new-task nil (:uid task))
         name-value (r/atom (if is-new-task "" (:name task)))
         due-date-value (r/atom (to-date
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
