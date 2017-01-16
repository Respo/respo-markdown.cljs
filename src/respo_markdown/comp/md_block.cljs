
(ns respo-markdown.comp.md-block
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo.alias :refer [create-comp div span textarea]]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.text :refer [comp-text]]
            [respo-markdown.util.core :refer [split-block]]))

(def comp-md-block
  (create-comp
   :md-block
   (fn [text options] (fn [state mutate!] (div {} (comp-text (split-block text) nil))))))
