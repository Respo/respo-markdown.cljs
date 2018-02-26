
(ns respo-md.comp.container
  (:require-macros [respo.macros :refer [defcomp <> div span textarea]])
  (:require [hsl.core :refer [hsl]]
            [respo-ui.core :as ui]
            [respo.core :refer [create-comp]]
            [respo.comp.space :refer [=<]]
            [respo-md.comp.md :refer [comp-md comp-md-block]]))

(def initial-state {:draft ""})

(def style-container {:align-items :stretch})

(def style-text {})

(defcomp
 comp-container
 (store highlighter)
 (let [states (:states store), state (or (:data states) initial-state)]
   (div
    {:style (merge ui/global ui/fullscreen ui/row style-container)}
    (textarea
     {:placeholder "Some markdown content",
      :value (:draft state),
      :style (merge ui/textarea ui/flex style-text),
      :on {:input (fn [e dispatch! mutate!]
             (println "Editing:" state (:value e))
             (mutate! (assoc state :draft (:value e))))}})
    (comp-md-block (:draft state) {:highlight highlighter}))))
