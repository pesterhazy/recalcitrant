(ns demo.app
  (:require [reagent.core :as reagent :refer [atom]]
            [recalcitrant.core :as rc]))

(defn root []
  (-> (rc/component "lala")
      (rc/render (fn [] [:div "Hello world"]))
      rc/finalize))

(defn init []
  (reagent/render-component (fn [] [rc/error-boundary [root]])
                            (.getElementById js/document "container")))
