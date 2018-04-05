(ns above-the-horizon.style)

;; Colours
(def cancel-colour "#c66")
(def save-colour "#6c6")
(def gray-accent-colour "#ccc")

;; Styles
(def view-style
  {:background-color "#fff"
   :justify-content "space-between"
   :height "100%"})


(def action-bar-style
  {:border-color gray-accent-colour
   :border-top-width 1
   :flex-direction "row"
   :justify-content "space-around"
   :width "100%"})

(def task-button-style
  {:button {:border-radius 5
            :padding 12}
   :text {:font-size 16}})

(def new-task-button-style
  {:button {:align-self "flex-end"
            :background-color "#fff"
            :border-color gray-accent-colour
            :height 50
            :margin-right 30
            :width 50}
   :text {:color gray-accent-colour
          :font-size 40
          :line-height 44
          :text-align "center"}})

(def textbox-style
  {:background-color "#fff"
   :border-color gray-accent-colour
   :border-radius 10
   :border-width 1
   :margin 20
   :padding 12})

(defn extend-button-style
  "Extends a button style"
  [base extras]
  {:button (into (base :button) (extras :button))
   :text (into (base :text) (extras :text))})

(def action-button-style
  {:button {:padding 20}
   :text {:font-size 22}})

(def cancel-button-style
  (extend-button-style
   action-button-style
   {:text {:color cancel-colour}}))

(def save-button-style
  (extend-button-style
   action-button-style
   {:text {:color save-colour}}))
