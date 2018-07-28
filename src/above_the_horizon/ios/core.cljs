(ns above-the-horizon.ios.core
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [cljs-react-navigation.reagent :refer [stack-navigator stack-screen]]
            [cljs-time.core :as time]
            [cljs-time.coerce :refer [to-date]]
            [above-the-horizon.components.button :refer [button]]
            [above-the-horizon.components.date-picker :refer [date-picker]]
            [above-the-horizon.components.task-cell :refer [task-cell]]
            [above-the-horizon.components.task-input :refer [task-input]]
            [above-the-horizon.events]
            [above-the-horizon.realm :as realm]
            [above-the-horizon.style :as style]
            [above-the-horizon.subs]))

(def ReactNative (js/require "react-native"))

(def app-registry (.-AppRegistry ReactNative))
(def flat-list (r/adapt-react-class (.-FlatList ReactNative)))
(def scroll-view (r/adapt-react-class (.-ScrollView ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))

(def ReactNaviagtion (js/require "react-navigation"))
(def safe-area-view (r/adapt-react-class (.-SafeAreaView ReactNaviagtion)))

(defn today-view [props]
  (let [navigate (-> props :navigation :navigate)
        get-param (-> props :navigation :getParam)
        tasks (subscribe [:get-tasks])]
    (fn []
      [safe-area-view {:style style/view-style}
       [scroll-view (map (partial task-cell navigate) @tasks)]
       [view {:style style/action-bar-style}
        [button "Menu" style/action-button-style
         #(navigate "Menu")]
        [button "New Task" style/action-button-style
         #(navigate "NewTask")]]])))

(defn task-view [props]
  (let* [go-back (-> props :navigation :goBack)
         task (-> props :navigation :state :params :task)
         is-new-task (nil? task)
         task-uid (if is-new-task nil (:uid task))
         name-value (r/atom (if is-new-task "" (:name task)))
         due-date-value (r/atom (-> task :due-date to-date))]
    (fn []
      [safe-area-view {:style style/view-style}
       [view {:style style/task-view-container-style}
        [task-input name-value is-new-task]
        [date-picker due-date-value]]
       [view {:style style/action-bar-style}
        [button "Cancel" style/cancel-button-style #(go-back)]
        [button "Save"
         style/save-button-style
         (fn []
           (dispatch
            [:save-task {:uid task-uid
                         :name @name-value
                         :due-date @due-date-value}])
           (go-back))]]])))

(defn menu-view [props]
  (let [navigate (-> props :navigation :navigate)]
    (fn []
      [safe-area-view {:style style/view-style}
       [flat-list {:data [{:key "today" :label "Today" :target "Today"}
                          {:key "all" :label "All Tasks" :target "AllTasks"}
                          {:key "settings" :label "Settings" :target "Settings"}]
                   :render-item (fn [item]
                                  (let* [clj-item (-> item js->clj (get "item"))
                                         label (-> clj-item (get "label"))
                                         target (-> clj-item (get "target"))]
                                    (r/as-element
                                     [button
                                      label
                                      style/menu-button-style
                                      #(navigate target)])))}]])))

(def slide-nav
  (stack-navigator
   {:Today {:screen (stack-screen today-view)}
    :NewTask {:screen (stack-screen task-view)}}
   {:headerMode "none"}))

(def all-nav
  (stack-navigator
   {:Slide {:screen slide-nav}
    :Menu {:screen (stack-screen menu-view)}}
   {:headerMode "none"
    :mode "modal"}))

(defn app-root []
  [:> all-nav {}])

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent
   app-registry
   "AboveTheHorizon"
   #(r/reactify-component app-root)))
