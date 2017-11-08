
(ns respo-markdown.comp.md-article
  (:require-macros [respo.macros :refer [defcomp list-> div pre code span p h1 h2 img a <>]]
                   [respo-markdown.comp.md-article :refer [h3]])
  (:require [hsl.core :refer [hsl]]
            [clojure.string :as string]
            [respo-ui.style :as ui]
            [respo.core :refer [create-comp create-element]]
            [respo.comp.space :refer [=<]]
            [respo-markdown.util.core :refer [split-block split-line]]))

(defn comp-image [chunk]
  (let [useful (subs chunk 2 (- (count chunk) 1)), [content url] (string/split useful "](")]
    (img {:src url, :alt content})))

(defn comp-link [chunk]
  (let [useful (subs chunk 1 (- (count chunk) 1)), [content url] (string/split useful "](")]
    (if (and (string/starts-with? content "`") (string/ends-with? content "`"))
      (a
       {:href url, :target "_blank"}
       (code {:inner-text (subs content 1 (dec (count content)))}))
      (a {:href url, :inner-text content, :target "_blank"}))))

(defn render-inline [text]
  (->> (split-line text)
       (map-indexed
        (fn [idx chunk]
          [idx
           (let [[mode content] chunk]
             (case mode
               :code (<> code content nil)
               :url (a {:href content, :inner-text content, :target "_blank"})
               :link (comp-link content)
               :image (comp-image content)
               :text (<> span content nil)
               (<> span (str "Unknown:" content) nil)))]))))

(defcomp
 comp-line
 (line)
 (cond
   (string/starts-with? line "# ") (list-> :h1 {} (render-inline (subs line 2)))
   (string/starts-with? line "## ") (list-> :h2 {} (render-inline (subs line 3)))
   (string/starts-with? line "### ") (list-> :h3 {} (render-inline (subs line 4)))
   (string/starts-with? line "> ") (list-> :blockquote {} (render-inline (subs line 2)))
   (string/starts-with? line "* ") (list-> :li {} (render-inline (subs line 2)))
   :else (list-> :div {} (render-inline line))))

(defcomp
 comp-text-block
 (lines)
 (list->
  :div
  {:class-name "md-paragraph"}
  (->> lines (map-indexed (fn [idx line] [idx (comp-line line)])))))

(def style-container {:padding 8})

(defcomp
 comp-code-block
 (lines options)
 (let [lang (first lines)
       content (string/join "\n" (rest lines))
       highlight-fn (:highlight options)]
   (pre
    {:class-name "md-code-block"}
    (code
     (if (and (not (string/blank? lang)) (fn? highlight-fn))
       {:innerHTML (highlight-fn content lang)}
       {:inner-text content})))))

(defcomp
 comp-md-article
 (text options)
 (let [blocks (split-block text)]
   (list->
    :div
    {:class-name "md-article", :style (merge ui/flex style-container)}
    (->> blocks
         (map-indexed
          (fn [idx block]
            [idx
             (let [[mode lines] block]
               (<> (pr-str mode))
               (case mode
                 :text (comp-text-block lines)
                 :code (comp-code-block lines options)
                 (<> "Unknown content.")))]))))))

(defn blockquote [props & children] (create-element :blockquote props children))

(defn li [props & children] (create-element :li props children))
