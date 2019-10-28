
(ns respo-md.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo-ui.core :as ui]
            [respo.comp.space :refer [=<]]
            [respo-md.comp.md :refer [comp-md comp-md-block]]
            [respo.core :refer [defcomp mutation-> <> div span textarea input a]]))

(def initial-state {:draft "", :text ""})

(defcomp
 comp-container
 (store highlighter)
 (let [states (:states store), state (or (:data states) initial-state)]
   (div
    {:style (merge ui/global {:width "80%", :margin "0 auto"})}
    (div {} (a {:href "https://github.com/Respo/respo-markdown"} (<> "respo-markdown")))
    (div
     {}
     (comp-md-block
      "Respo Markdown component renders Markdown text to virtual DOM in Respo. Require the code with\n\n```clojure\n(require '[respo-md.comp.md :refer [comp-md comp-md-block]]\n\n(comp-md \"content\")\n\n(comp-md-block \"content\\nnew line\" {})\n```\n"
      {}))
    (div
     {:style {}}
     (div {} (comp-md "This is an example for using `comp-md`:"))
     (div
      {}
      (input
       {:style (merge ui/input {:width "100%"}),
        :value (:text state),
        :placeholder "text inline",
        :on-input (mutation-> (assoc state :text (:value %e)))}))
     (div {} (comp-md (:text state))))
    (=< nil 40)
    (div
     {:style {}}
     (div {} (comp-md "Example For using `comp-md-block`:"))
     (div
      {}
      (textarea
       {:placeholder "multi-line content",
        :value (:draft state),
        :style (merge ui/textarea {:height 240, :width "100%"}),
        :on {:input (fn [e dispatch! mutate!]
               (println "Editing:" state (:value e))
               (mutate! (assoc state :draft (:value e))))}}))
     (div
      {:style (merge ui/flex {:padding 8})}
      (comp-md-block
       (:draft state)
       {:highlight highlighter,
        :css ".md-p code {\n  background-color: #edf;\n  padding: 0 8px;\n}",
        :class-name "demo"}))))))
