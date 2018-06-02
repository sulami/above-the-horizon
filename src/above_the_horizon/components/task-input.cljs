(ns above-the-horizon.components.task-input
  (:require [reagent.core :as r]
            [above-the-horizon.style :as style]))

(def ReactNative (js/require "react-native"))

(def text-input (r/adapt-react-class (.-TextInput ReactNative)))

(defn task-input-component
  [name-atom]
  [text-input
   {:style style/textbox-style
    :maxLength 255
    :placeholder "Task Name"
    :returnKeyType "done"
    :enablesReturnKeyAutomatically true
    :autoFocus is-new-task
    :on-change-text #(reset! name-atom %)}
   @name-atom])
