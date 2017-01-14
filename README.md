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

## Creating Components
