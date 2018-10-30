
(ns respo-md.comp.md
  (:require-macros [respo-md.comp.md :refer [h3]])
  (:require [hsl.core :refer [hsl]]
            [clojure.string :as string]
            [respo-ui.core :as ui]
            [respo.core :refer [create-comp create-element]]
            [respo.comp.space :refer [=<]]
            [respo-md.util.core :refer [split-block split-line]]
            [respo.core :refer [defcomp list-> div pre code span p h1 h2 img a <> style]]
            [respo.util.list :refer [map-with-idx]]))

(defn blockquote [props & children] (create-element :blockquote props children))

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

(defcomp comp-md (text) (list-> :span {} (render-inline text)))

(defcomp
 comp-text-block
 (lines)
 (list-> :div {:class-name "md-p"} (->> lines (map-with-idx (fn [line] (comp-line line))))))

(defcomp
 comp-md-block
 (text options)
 (let [blocks (split-block text), css (:css options), class-name (:class-name options)]
   (list->
    :div
    {:class-name (if (nil? class-name) "md-block" (str "md-block " class-name)),
     :style (:style options)}
    (let [css (:css options)
          p-elements (->> blocks
                          (map-with-idx
                           (fn [block]
                             (let [[mode lines] block]
                               (<> (pr-str mode))
                               (case mode
                                 :text (comp-text-block lines)
                                 :code (comp-code-block lines options)
                                 (<> "Unknown content."))))))]
      (if (nil? css)
        p-elements
        (cons [-1 (style {:inner-text css, :scoped true})] p-elements))))))

(defn li [props & children] (create-element :li props children))
