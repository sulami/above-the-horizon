(ns above-the-horizon.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [cljs-react-navigation.reagent :refer [stack-navigator stack-screen]]
            [above-the-horizon.events]
            [above-the-horizon.subs]))

(def ReactNative (js/require "react-native"))
(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))

(def ReactNavigation (js/require "react-navigation"))

;; (defn alert [title]
;;       (.alert (.-Alert ReactNative) title))

(def button-style
  {:background-color "#fff" :padding 12 :border-radius 5})

(def button-text-style
  {:font-size 16})

(defn make-button [display-text action]
  [touchable-highlight {:style button-style
                        :on-press action
                        :key display-text}
   [text {:style button-text-style} display-text]])

(defn make-task-button [navigate task]
  (make-button (:name task) #(navigate "NewTask" {:task task})))

(defn today-view []
  (fn [{:keys [navigation]}]
    (let [{navigate :navigate} navigation
          tasks (subscribe [:get-tasks])]
      [view
       (map (partial make-task-button navigate) @tasks)
       (make-button "New Task" #(navigate "NewTask" {}))])))

(defn new-task-view [props]
  (fn []
    (let* [task (-> props :navigation :state :params :task)
           task-name (if task (task :name) "New Task")]
      [view {:style {:margin 10}}
       [text {:style {:font-size 20 :text-align "center"}} task-name]])))

(def stack-router
  {:Today {:screen (stack-screen today-view {:title "Today"})}
   :NewTask {:screen (stack-screen new-task-view {:title "New Task"})}})

(def stack-nav
  (stack-navigator stack-router))

(defn app-root []
  [:> stack-nav {}])

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "AboveTheHorizon" #(r/reactify-component app-root)))
