
(ns respo-markdown.util.core
  (:require [clojure.string :as string]
            [cljs.reader :refer [read-string]]
            [respo-markdown.util.string :refer [br]]))

(defn split-block
  ([text] (split-block (string/split text br) [] [] :empty))
  ([lines acc buffer mode]
   (if (empty? lines)
     (if (empty? buffer) acc (conj acc [mode buffer]))
     (let [cursor (first lines), left (rest lines)]
       (case mode
         :empty
           (cond
             (= cursor "") (recur left acc [] :empty)
             (string/starts-with? cursor "```") (recur left acc [(subs cursor 3)] :code)
             :else (recur left acc [cursor] :text))
         :text
           (cond
             (= cursor "") (recur left (conj acc [:text buffer]) [] :empty)
             (string/starts-with? cursor "```")
               (recur left (conj acc [:text buffer]) [(subs cursor 3)] :code)
             :else (recur left acc (conj buffer cursor) :text))
         :code
           (if (string/starts-with? cursor "```")
             (recur left (conj acc [mode buffer]) [] :empty)
             (recur left acc (conj buffer cursor) :code))
         (throw (js/Error. (str "Strange splitting mode: " mode))))))))
