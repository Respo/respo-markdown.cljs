
(ns respo-markdown.comp.md-article
  (:require [hsl.core :refer [hsl]]
            [clojure.string :as string]
            [respo-ui.style :as ui]
            [respo.alias :refer [create-comp create-element div pre code p h1 h2]]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.text :refer [comp-text]]
            [respo-markdown.util.core :refer [split-block]]
            [respo-markdown.util.string :refer [br]]))

(defn h3 [props & children] (create-element :h3 props children))

(defn blockquote [props & children] (create-element :blockquote props children))

(defn render-inline [text] (comp-text text nil))

(defn li [props & children] (create-element :li props children))

(def comp-line
  (create-comp
   :line
   (fn [line]
     (fn [state mutate!]
       (cond
         (string/starts-with? line "# ") (h1 {} (render-inline (subs line 2)))
         (string/starts-with? line "## ") (h2 {} (render-inline (subs line 3)))
         (string/starts-with? line "### ") (h3 {} (render-inline (subs line 4)))
         (string/starts-with? line "> ") (blockquote {} (render-inline (subs line 2)))
         (string/starts-with? line "* ") (li {} (render-inline (subs line 2)))
         :else (div {} (comp-text line nil)))))))

(def comp-text-block
  (create-comp
   :text-block
   (fn [lines]
     (fn [state mutate!]
       (p {} (->> lines (map-indexed (fn [idx line] [idx (comp-line line)]))))))))

(def style-code
  {:color :white, :background-color (hsl 300 80 20), :padding 8, :display :block})

(def comp-code-block
  (create-comp
   :code-block
   (fn [lines]
     (fn [state mutate!]
       (let [lang (first lines), content (string/join br (rest lines))]
         (pre {:style style-code} (code {:attrs {:inner-text content}})))))))

(def comp-md-article
  (create-comp
   :md-article
   (fn [text options]
     (fn [state mutate!]
       (let [blocks (split-block text)]
         (div
          {}
          (->> blocks
               (map-indexed
                (fn [idx block]
                  [idx
                   (let [[mode lines] block]
                     (comp-text (pr-str mode) nil)
                     (case mode
                       :text (comp-text-block lines)
                       :code (comp-code-block lines)
                       (comp-text "Unknown content.")))])))))))))
