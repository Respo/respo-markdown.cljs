
(ns respo-markdown.comp.md-article
  (:require [hsl.core :refer [hsl]]
            [clojure.string :as string]
            [respo-ui.style :as ui]
            [respo.alias :refer [create-comp create-element div pre code p h1 h2 img a]]
            [respo.comp.space :refer [comp-space]]
            [respo.comp.text :refer [comp-text comp-code]]
            [respo-markdown.util.core :refer [split-block split-line]]
            [respo-markdown.util.string :refer [br]]))

(defn comp-image [chunk]
  (let [useful (subs chunk 2 (- (count chunk) 1)), [content url] (string/split useful "](")]
    (img {:attrs {:src url, :alt content}})))

(defn h3 [props & children] (create-element :h3 props children))

(defn comp-link [chunk]
  (let [useful (subs chunk 1 (- (count chunk) 1)), [content url] (string/split useful "](")]
    (a {:attrs {:href url, :inner-text content, :target "_blank"}})))

(defn blockquote [props & children] (create-element :blockquote props children))

(defn render-inline [text]
  (->> (split-line text)
       (map-indexed
        (fn [idx chunk]
          [idx
           (let [[mode content] chunk]
             (case mode
               :code (comp-code content nil)
               :url (a {:attrs {:href content, :inner-text content, :target "_blank"}})
               :link (comp-link content)
               :image (comp-image content)
               :text (comp-text content nil)
               (comp-text (str "Unknown:" content) nil)))]))))

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
         :else (div {} (render-inline line)))))))

(def comp-text-block
  (create-comp
   :text-block
   (fn [lines]
     (fn [state mutate!]
       (div
        {:attrs {:class-name "md-paragraph"}}
        (->> lines (map-indexed (fn [idx line] [idx (comp-line line)]))))))))

(def style-container {:padding 8})

(def comp-code-block
  (create-comp
   :code-block
   (fn [lines options]
     (fn [state mutate!]
       (let [lang (first lines)
             content (string/join br (rest lines))
             highlight-fn (:highlight options)]
         (pre
          {:attrs {:class-name "md-code-block"}}
          (code
           {:attrs (if (and (not (string/blank? lang)) (fn? highlight-fn))
              {:innerHTML (highlight-fn content lang)}
              {:inner-text content})})))))))

(def comp-md-article
  (create-comp
   :md-article
   (fn [text options]
     (fn [state mutate!]
       (let [blocks (split-block text)]
         (div
          {:style (merge ui/flex style-container), :attrs {:class-name "md-article"}}
          (->> blocks
               (map-indexed
                (fn [idx block]
                  [idx
                   (let [[mode lines] block]
                     (comp-text (pr-str mode) nil)
                     (case mode
                       :text (comp-text-block lines)
                       :code (comp-code-block lines options)
                       (comp-text "Unknown content.")))])))))))))
