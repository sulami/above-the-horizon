(ns above-the-horizon.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [cljs-react-navigation.reagent :refer [stack-navigator stack-screen]]
            [schema.core :as s :include-macros true]
            [above-the-horizon.db :refer [Task]]
            [above-the-horizon.events]
            [above-the-horizon.realm :as realm]
            [above-the-horizon.style :as style]
            [above-the-horizon.subs]))

(def ReactNative (js/require "react-native"))
(def app-registry (.-AppRegistry ReactNative))
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

(defn make-button [display-text button-style action]
  [touchable-highlight {:style (:button button-style)
                        :on-press action
                        :key display-text}
   [text
    {:style (:text button-style)}
    display-text]])

(s/defn ^:always-validate make-task-button
  [navigate
   task :- Task]
  [view {:style {:flex-direction "row"} :key (:uid task)}
   (make-button
    "O"
    {:button {:margin-left 20 :padding 12} :text {:font-size 16}}
    #(dispatch [:complete-task (:uid task)]))
   (make-button
    (:name task)
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
       [view {:style style/action-bar-style}
        (make-button "+" style/new-task-button-style #(navigate "NewTask"))]])))

(defn task-view [props]
  (fn []
    (let* [go-back (-> props :navigation :goBack)
           task (-> props :navigation :state :params :task)
           is-new-task (nil? task)
           task-name (if is-new-task "" (:name task))]
      [safe-area-view {:style style/view-style}
       [text-input
        {:style style/textbox-style
         :maxLength 255
         :placeholder "Task Name"
         :returnKeyType "done"
         :enablesReturnKeyAutomatically true
         :autoFocus is-new-task}
        task-name]
       [view {:style style/action-bar-style}
        (make-button "Cancel" style/cancel-button-style #(go-back))
        (make-button "Save" style/save-button-style (fn []
                                                      (dispatch [:save-task {:uid task}])
                                                      (go-back)))]])))

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
