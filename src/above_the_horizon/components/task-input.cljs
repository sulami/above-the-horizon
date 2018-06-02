(ns above-the-horizon.components.task-input
  (:require [reagent.core :as r]
            [above-the-horizon.components.button :refer [button]]
            [above-the-horizon.style :as style]))

(def ReactNative (js/require "react-native"))

(def text-input (r/adapt-react-class (.-TextInput ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))

(defn task-input
  [name-atom auto-focus]
  [view {:style style/textbox-container-style}
   [text-input
    {:style style/task-title-textbox-style
     :maxLength 255
     :placeholder "Task Name"
     :returnKeyType "done"
     :enablesReturnKeyAutomatically true
     :autoFocus auto-focus
     :on-change-text #(reset! name-atom %)}
    @name-atom]
   (when (not (empty? @name-atom))
     [button "X"
      style/textbox-empty-button-style
      #(reset! name-atom "")])])
