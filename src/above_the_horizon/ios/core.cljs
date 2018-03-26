(ns above-the-horizon.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [cljs-react-navigation.reagent :refer [stack-navigator stack-screen]]
            [above-the-horizon.events]
            [above-the-horizon.subs]))

(def ReactNative (js/require "react-native"))
(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableOpacity ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))

(def ReactNaviagtion (js/require "react-navigation"))
(def safe-area-view (r/adapt-react-class (.-SafeAreaView ReactNaviagtion)))

(def view-style
  {:background-color "#fff"
   :height "100%"})

(def task-button-style
  {:button {:background-color "#fff"
            :border-radius 5
            :padding 12}
   :text {:font-size 16}})

(def new-task-button-style
  {:button {:background-color "#fff"
            :border-radius 100
            :border-color "#ccc"
            :border-width 1
            :height 50
            :width 50}
   :text {:color "#ccc"
          :font-size 40
          :line-height 44
          :text-align "center"}})

(defn make-button [display-text button-style action]
  [touchable-highlight {:style (button-style :button)
                        :on-press action
                        :key display-text}
   [text {:style (button-style :text)} display-text]])

(defn make-task-button [navigate task]
  (make-button (:name task) task-button-style #(navigate "NewTask" {:task task})))

(defn today-view [props]
  (fn []
    (let [navigate (-> props :navigation :navigate)
          get-param (-> props :navigation :getParam)
          tasks (subscribe [:get-tasks])]
      [safe-area-view {:style view-style}
       (make-button "getProp" task-button-style #(alert (str props)))
       (map (partial make-task-button navigate) @tasks)
       (make-button "+" new-task-button-style #(navigate "NewTask"))])))

(defn task-view [props]
  (fn []
    (let* [task (-> props :navigation :state :params :task)
           task-name (if task (task :name) "New Task")]
      [safe-area-view
       [text {:style {:font-size 20 :text-align "center"}} task-name]])))

(def stack-router
  {:Today {:screen (stack-screen today-view {:title "Today"})}
   :NewTask {:screen (stack-screen task-view {:title "Task"})}})

(def stack-nav
  (stack-navigator stack-router {:headerMode "none"}))

(defn app-root []
  [:> stack-nav {}])

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "AboveTheHorizon" #(r/reactify-component app-root)))
