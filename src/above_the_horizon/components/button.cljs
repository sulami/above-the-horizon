(ns above-the-horizon.components.button
  (:require [reagent.core :as r]))

(def ReactNative (js/require "react-native"))

(def text (r/adapt-react-class (.-Text ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableOpacity ReactNative)))

(defn button-component
  [display-text button-style action]
  [touchable-highlight {:style (:button button-style)
                        :on-press action
                        :key display-text}
   [text
    {:style (:text button-style)}
    display-text]])
