(ns above-the-horizon.style)

(def view-style
  {:background-color "#fff"
   :justify-content "space-between"
   :height "100%"})

(def task-button-style
  {:button {:border-radius 5
            :padding 12}
   :text {:font-size 16}})

(def new-task-button-style
  {:button {:align-self "flex-end"
            :background-color "#fff"
            :border-color "#ccc"
            :border-radius 100
            :border-width 1
            :height 50
            :margin-right 30
            :width 50}
   :text {:color "#ccc"
          :font-size 40
          :line-height 44
          :text-align "center"}})
