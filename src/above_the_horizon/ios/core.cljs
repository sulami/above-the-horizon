(ns above-the-horizon.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [cljs-react-navigation.reagent :refer [stack-navigator stack-screen]]
            [above-the-horizon.events]
            [above-the-horizon.subs]))

(def ReactNative (js/require "react-native"))
(def ReactNavigation (js/require "react-navigation"))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
;; (def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))

;; (def logo-img (js/require "./images/cljs.png"))

;; (defn alert [title]
;;       (.alert (.-Alert ReactNative) title))

(defn basic-view []
  (fn []
    [view {:style {:margin 40}}
     [text {:style {:font-size 30}} "hullo"]]))

(defn basic-view-two []
  (fn [{:keys [navigation]}]
    (let [{navigate :navigate} navigation]
      [view {:style {:margin 40}}
       [touchable-highlight {:style {:background-color "#999" :padding 12 :border-radius 5}
                             :on-press #(navigate "HomeTwo" {})}
        [text {:style {:font-size 30}} "hullo"]]])))

(def stack-router
  {:Home {:screen (stack-screen basic-view-two {:title "hullo"})}
   :HomeTwo {:screen (stack-screen basic-view {:title "hullo-two"})}})

(def stack-nav
  (stack-navigator stack-router))

(defn app-root []
  [:> stack-nav {}])

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "AboveTheHorizon" #(r/reactify-component app-root)))
