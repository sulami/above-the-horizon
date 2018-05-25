(ns above-the-horizon.style)

;; COLOURS

(def background-colour "#fff")
(def cancel-colour "#c66")
(def save-colour "#6c6")
(def gray-accent-colour "#ddd")

;; UTILITY

(defn extend-button-style
  "Extends a button style."
  [base extras]
  {:button (into (:button base) (:button extras))
   :text (into (:text base) (:text extras))})

;; STYLES

;; General layout

(def view-style
  {:background-color background-colour
   :justify-content "space-between"
   :height "100%"})

;; Bottom action bar

(def action-bar-style
  {:border-color gray-accent-colour
   :border-top-width 1
   :flex-direction "row"
   :justify-content "space-around"
   :width "100%"})

;; Task list

(def task-cell-container-style
  "The outer container for the task cell in a list."
  {:align-items "center"
   :flex-direction "row"
   :padding 0})

(def task-cell-checkbox-style
  "The checkbox on the left of the task cell."
  {:button {:padding-horizontal 24
            :padding-vertical 12}
   :text {:font-size 16}})

(def task-cell-right-container-style
  "The right hand side of the task cell, containing the text."
  {:align-items "stretch"
   :flex 1
   :padding 8})

(def task-cell-right-touch-style
  "The right hand touchable highlight in the task cell."
  {:flex 1})

(def task-cell-title-style
  "The title of the task in the task cell."
  {:font-size 16})

(def task-cell-due-date-style
  "The due date in the task cell."
  {:font-size 12})

(def task-cell-due-date-overdue-style
  "The due date in the task cell if it is overdue."
  (into task-cell-due-date-style
        {:color "tomato"}))

(def new-task-button-style
  {:button {:align-self "flex-end"
            :background-color background-colour
            :border-color gray-accent-colour
            :height 50
            :margin-right 30
            :width 50}
   :text {:color gray-accent-colour
          :font-size 40
          :line-height 44
          :text-align "center"}})

;; Task view

(def textbox-style
  {:background-color background-colour
   :border-color gray-accent-colour
   :border-radius 10
   :border-width 1
   :margin 20
   :padding 12})

(def date-picker-title-bar-style
  {:flex 0
   :flex-direction "row"
   :justify-content "space-between"
   :width "100%"
   :height 40
   :padding-horizontal 10
   :border-color gray-accent-colour
   :border-width 1})

(def date-picker-title-style
  {:font-size 16
   :height 40
   :line-height 40
   :text-align "center"})

(def date-picker-collapsible-view-style
  {:justify-content "center"
   :flex-direction "row"
   :flex-wrap "wrap"})

(def date-picker-button-style
  {:button {:border-color gray-accent-colour
            :border-width 0.5
            :height 40
            :width "33.3%"}
   :text {:font-size 15
          :line-height 40
          :text-align "center"}})

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
