
(ns respo-markdown.util.core
  (:require [clojure.string :as string] [cljs.reader :refer [read-string]]))

(defn split-block
  ([text] (split-block (string/split text (read-string "\"\\n\"")) [] []))
  ([text acc buffer] (if (empty? text) acc nil)))
