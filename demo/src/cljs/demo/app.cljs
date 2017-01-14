(ns demo.app
  (:require [reagent.core :as r]
            [recalcitrant.core :as rc]))

(defonce !state (r/atom nil))
(defonce !n (r/cursor !state [:n]))
(defonce !hidden? (r/cursor !state [:hidden?]))

(defn counter-ui* [{:keys [n]}]
  [:div {:style {:display :flex
                 :align-items :center
                 :justify-content :center
                 :background-color "#fef0fe"
                 :border-radius 10
                 :width 80
                 :height 80}}
   [:div {:style {:font-size 40}} n]])

(defn counter-ui []
  (-> (rc/component "lala")
      (rc/render counter-ui*)
      (rc/logging)
      rc/finalize))

(defn root []
  [:div
   [:div.control
    [:button {:on-click (fn [] (swap! !n #(inc (or % 0))))}
     "inc"]
    [:button {:on-click (fn [] (swap! !hidden? not))}
     "toggle"]]
   (when @!hidden?
     [counter-ui {:n (or @!n 0)}])])

(defn init []
  (r/render-component (fn [] [rc/error-boundary [root]])
                      (.getElementById js/document "container")))
