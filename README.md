# recalcitrant

recalcitrant is a toolbox for building simple components in Reagent

Note that this is pre-alpha code at best. Feedback is welcome!

## Motivation

Overall Reagent is a pleasure to use. But some tasks can seem unnecessarily complex:

- error handling
- building stateful components
- accessing props in lifecycle methods

This library aims to provide abstractions and Higher Level Components to make
common Reagent tasks simpler.

## Error Handling

Like React, Reagent does not
[handle exceptions](https://github.com/reagent-project/reagent/issues/272) or
errors in render functions well. If an unhandled exception is thrown inside a
render function, the component tree gets corrupted.

To prevent this problem, wrap your entire component tree in an `error-boundary`
component:

```clojure
(ns my-ns
  (:require [recalcitrant.tools :refer [error-boundary]]))

(defn root []
  (assert false "Oops"))

(r/render-component (fn [] [error-boundary [root]])
                    (.. js/document (querySelector \"#container\")))
```

The component will catch the exception and print it to the console. The tree
containing the exception will not be rendered. If the tree is rerendered after
fixing the offending code, Reagent recovers and renders the tree again.

Note that this solution is based on an unstable React API so there is no
guarantee that error boundaries will continue to work in future versions of React.

## Buidling Components with Lifecyle Methods

Building components with Lifecycle methods in Reagent can be tedious.
Recalcitrant offers an alternativ way to constructs components by decorating a
render method with lifecyce methods.

```clojure
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
```

This builds up a component in steps:

- start with an empty but named component spec (mandatory)
- add a render function (mandatory)
- decorate the component with a "new-props" method.
- finalize the pipeline, returning a Reagent component

Decorators are composable. When building up a pipeline, order matters. The following decorators are availabile:

- *new-props*: Adds a callback that is called when new props are received. This
  happens both when the component is mounted initially and when the props change
  for an existing components.

  Use *new-props* as an opportunity to cause side-effects (AJAX requests, DOM
  maniuplation) that depend on the value of the props.

  Note that that this is different from :component-did-mount lifecycle methods
  or initialization code in Form-2 components, where only the initial value of
  the props is ever taken into account.

- *logging*: Prints log messages to the console whenever any lifecycle methods
  is called. This is useful to see when and why components are re-rendered.

## Demo

A sample app demonstrating the features is included in the repository. To try
it:

```shell
cd demo
boot dev
# open http://localhost:3000 in your browser
```

## Author

(c) 2017 Paulus Esterhazy
