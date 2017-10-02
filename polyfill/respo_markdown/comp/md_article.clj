
(ns respo-markdown.comp.md-article)

(defmacro h3 [props & children]
  `(respo.core/create-element :h3 ~props ~@children))
