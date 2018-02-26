
(ns respo-md.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo-ui.core :as ui]
            [respo.core :refer [create-comp]]
            [respo.comp.space :refer [=<]]
            [respo-md.comp.md :refer [comp-md comp-md-block]]
            [respo.macros :refer [defcomp mutation-> <> div span textarea input]]))

(def initial-state {:draft "", :text ""})

(defcomp
 comp-container
 (store highlighter)
 (let [states (:states store), state (or (:data states) initial-state)]
   (div
    {:style (merge ui/global)}
    (div
     {:style {}}
     (input
      {:style (merge ui/input {:width 600}),
       :value (:text state),
       :on-input (mutation-> (assoc state :text (:value %e)))})
     (div {} (comp-md (:text state))))
    (div
     {:style ui/row}
     (textarea
      {:placeholder "Some markdown content",
       :value (:draft state),
       :style (merge ui/textarea ui/flex {:height 240}),
       :on {:input (fn [e dispatch! mutate!]
              (println "Editing:" state (:value e))
              (mutate! (assoc state :draft (:value e))))}})
     (div
      {:style (merge ui/flex {:padding 8})}
      (comp-md-block (:draft state) {:highlight highlighter}))))))
