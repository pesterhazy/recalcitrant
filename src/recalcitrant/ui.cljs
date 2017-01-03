(ns recalcitrant.ui
  (:require [reagent.core :as r]
            [recalcitrant.core :as rc]))

(defn input [{:keys [state] :as props}]
  [:input (-> props
              (dissoc :state)
              (assoc :on-change #(reset! state (->> % .-target .-value))
                     :value @state))])

(defn checkbox [{:keys [state] :as props}]
  [:input (-> props
              (dissoc :state)
              (assoc :type :checkbox
                     :on-change #(swap! state not)
                     :checked @state))])

(defn delay-input
  "Like input but adds calls on-expired, passed as a prop, after delay-ms"
  []
  (let [!d (atom nil)]
    (-> (rc/component "delay-input")
        (rc/render (fn [{:keys [state]}]
                     [input {:state (r/wrap @state
                                            (fn [v]
                                              (.start @!d)
                                              (reset! state v)))
                             :class "form-control"}]))
        (rc/new-props (fn [{:keys [on-expired delay-ms state]}]
                        (some-> @!d .dispose)
                        (reset! !d (goog.async.Delay. #(on-expired @state) delay-ms))))
        rc/finalize)))
