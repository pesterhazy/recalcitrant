(ns recalcitrant.core
  (:require [reagent.core :as r]
            [reagent.impl.component :as comp]
            [reagent.impl.util :as util]
            [reagent.debug :refer-macros [dev?]]
            [goog.object :as gobj]))

(defn component
  "Component constructor

  Provides a fluent-style API:

  (-> (rc/component \"my-component\")
      (rc/render (fn [{:keys [name]}] [:div \"Hello\" name]))
      (rc/finalize))"
  [display-name]
  {:display-name display-name})

(defn render [spec f]
  (-> spec
      (assoc :reagent-render f)))

(defn new-props
  "Initialization mixin

  Adds a callback that will be called on component creation as well as whenever
  a new set of props is received. "
  [spec init]
  (-> spec
      (update :component-will-mount
              (fn [next]
                (fn [this]
                  (init (r/props this))
                  (when next
                    (next this)))))
      (update :component-will-receive-props
              (fn [next]
                (fn [this [_ new-props :as v]]
                  (when (not= new-props (r/props this))
                    (init new-props))
                  (when next
                    (next this v)))))))

(def lifecycle-methods
  [
   ;; :get-initial-state
   :component-will-receive-props
   ;; :should-component-update
   :component-will-mount
   :component-did-mount
   :component-will-update
   :component-did-update
   :component-will-unmount
   :reagent-render])

(defn logging
  "Mixin that logs out component lifecycle events"
  [spec]
  (reduce (fn [m ky] (update m
                             ky
                             (fn [next]
                               (fn [& args]
                                 (js/console.info (name ky))
                                 (when next
                                   (apply next args))))))
          spec
          lifecycle-methods))

(defn finalize
  [spec]
  (r/create-class spec))

;; ---

(def error-boundary
  "Wrapper component for recovering from exceptions in downstream
  render fns. Creates an error boundary that prevents exceptions from corrupting
  the React component hierarchy.

  Use this component to wrap a single reagent (root) component. Any exception
  thrown in downstream render fns will be caught and logged. The component's
  child and its children will not be rendered.

  This is useful in a reloading-based development workflow.

  Example usage:

  (ns my-ns
    (:require [recalcitrant.core :refer [error-boundary]]))

  (defn root []
    (assert false \"Oops\"))

  ;; note the way we are invoking the component
  (r/render [(fn [] [error-boundary [root]])]
                      (.. js/document (querySelector \"#container\")))

  Note that this relies on the undocumented unstable_handleError API introduced
  in React 15.

  This componenet may have performance implications, so it is recommended to
  enable it only during development."
  (if (dev?)
    (r/adapt-react-class (comp/create-class
                          {:getInitialState
                           (fn []
                             #js {:error false})

                           :unstable_handleError
                           (fn [e]
                             (this-as this
                               (if (ex-data e)
                                 (js/console.error (pr-str e))
                                 (js/console.error e))
                               (.setState this #js {:error true})))

                           :render
                           (fn []
                             (this-as this
                               (let [children ((-> util/react
                                                   (gobj/get "Children")
                                                   (gobj/get "toArray"))
                                               (-> this
                                                   (gobj/get "props")
                                                   (gobj/get "children")))]
                                 (when (not= 1 (count children))
                                   (js/console.warn "Component error-boundary requires a single child component. Additional children are ignored."))
                                 (if (-> this
                                         (gobj/get "state")
                                         (gobj/get "error"))
                                   (do
                                     (js/console.warn "An error occurred downstream (see errors above). The element subtree will not be rendered.")
                                     nil)
                                   (first children)))))}))
    (r/adapt-react-class (comp/create-class
                          {:render
                           (fn []
                             (this-as this
                               (let [children ((-> util/react
                                                   (gobj/get "Children")
                                                   (gobj/get "toArray"))
                                               (-> this
                                                   (gobj/get "props")
                                                   (gobj/get "children")))]
                                 (first children))))}))))
