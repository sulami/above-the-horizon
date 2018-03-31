(ns above-the-horizon.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [cljs-react-navigation.reagent :refer [stack-navigator stack-screen]]
            [schema.core :as s :include-macros true]
            [above-the-horizon.db :refer [Task]]
            [above-the-horizon.events]
            [above-the-horizon.style :as style]
            [above-the-horizon.subs]))

(def ReactNative (js/require "react-native"))
(def app-registry (.-AppRegistry ReactNative))
(def scroll-view (r/adapt-react-class (.-ScrollView ReactNative)))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableOpacity ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))

(def ReactNaviagtion (js/require "react-navigation"))
(def safe-area-view (r/adapt-react-class (.-SafeAreaView ReactNaviagtion)))

(defn alert [message]
  "Trigger an alert with `message`"
  (.alert (.-Alert ReactNative) message))

(defn make-button [display-text button-style action]
  [touchable-highlight {:style (button-style :button)
                        :on-press action
                        :key display-text}
   [text
    {:style (button-style :text)}
    display-text]])

(s/defn ^:always-validate make-task-button
  [navigate
   task :- Task]
  [view {:style {:flex-direction "row"} :key (task :name)}
   (make-button
    "O"
    {:button {:margin-left 20 :padding 12} :text {:font-size 16}}
    #(alert (str "Completed " (task :name))))
   (make-button
    (task :name)
    style/task-button-style
    #(navigate "NewTask" {:task task}))])

(defn today-view [props]
  (fn []
    (let [navigate (-> props :navigation :navigate)
          get-param (-> props :navigation :getParam)
          tasks (subscribe [:get-tasks])]
      [safe-area-view {:style style/view-style}
       [scroll-view
        (map (partial make-task-button navigate) @tasks)]
       (make-button "+" style/new-task-button-style #(navigate "NewTask"))])))

(defn task-view [props]
  (fn []
    (let* [task (-> props :navigation :state :params :task)
           task-name (if task (task :name) "New Task")]
      [safe-area-view
       [text
        {:style {:font-size 20 :text-align "center"}}
        task-name]])))

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
