(ns demo.app
  (:require [reagent.core :as r]
            [weasel.repl :as repl]
            [recalcitrant.core :as rc]))

(defonce !state (r/atom nil))
(defonce !n (r/cursor !state [:n]))
(defonce !hidden? (r/cursor !state [:hidden?]))
(defonce !remove? (r/cursor !state [:remove?]))

(defn counter-ui* [{:keys [n hidden?]}]
  [:div.bg-info.text-white.rounded-circle.indicator {:class (if hidden? :hide :show)}
   [:div {:style {:font-size 40}} n]])

(defn counter-ui []
  (-> (rc/component "lala")
      (rc/render counter-ui*)
      (rc/logging)
      (rc/new-props (fn [m] (prn m)))
      rc/finalize))

(defn root []
  [:div
   [:div.control
    [:div.btn-toolbar
     [:button.btn.btn-primary {:on-click (fn [] (swap! !n #(inc (or % 0))))}
      "inc"]
     [:button.btn.btn-secondary {:on-click (fn [] (swap! !hidden? not))}
      "toggle hidden?"]
     [:button.btn.btn-secondary {:on-click (fn [] (swap! !remove? not))}
      "toggle remove?"]]]
   (when-not @!remove?
     [counter-ui {:n (or @!n 0)
                  :hidden? @!hidden?}])])

(defn init []
  (enable-console-print!)
  (r/render-component (fn [] [rc/error-boundary [root]])
                      (.getElementById js/document "container")))

(defonce conn
  (when-not (repl/alive?)
    (repl/connect "ws://localhost:9001")))

(if (repl/alive?)
  (println "Loaded example"))
