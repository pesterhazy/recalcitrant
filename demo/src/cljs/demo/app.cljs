(ns demo.app
  (:require [reagent.core :as r]
            [recalcitrant.core :as rc]))

(defonce !state (r/atom nil))
(defonce !n (r/cursor !state [:n]))
(defonce !hidden? (r/cursor !state [:hidden?]))

(defn counter-ui* [{:keys [n hidden?]}]
  [:div.rounded-circle.indicator {:class (if hidden? :hide :show)}
   [:div {:style {:font-size 40}} n]])

(defn counter-ui []
  (-> (rc/component "lala")
      (rc/render counter-ui*)
      (rc/logging)
      rc/finalize))

(defn root []
  [:div
   [:div.control
    [:div.btn-toolbar
     [:button.btn.btn-primary {:on-click (fn [] (swap! !n #(inc (or % 0))))}
      "inc"]
     [:button.btn.btn-secondary {:type "button" :on-click (fn [] (swap! !hidden? not))}
      "toggle"]]]
   [counter-ui {:n (or @!n 0)
                :hidden? @!hidden?}]])

(defn init []
  (r/render-component (fn [] [rc/error-boundary [root]])
                      (.getElementById js/document "container")))
