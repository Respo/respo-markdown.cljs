
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

(defn split-line
  ([line] (split-line [] line "" :text))
  ([acc line buffer mode]
   (if (= "" line)
     (if (= "" buffer) acc (conj acc [mode buffer]))
     (let [cursor (first line), left (subs line 1)]
       (case mode
         :text
           (case cursor
             "`"
               (recur (conj (if (some? buffer) (conj acc [:text buffer]) acc)) left "" :code)
             "h"
               (if (or (= "http://" (subs line 0 7)) (= "https://" (subs line 0 8)))
                 (let [pieces (string/split line " ")]
                   (recur
                    (conj
                     (if (= "" buffer) acc (conj acc [:text buffer]))
                     [:url (first pieces)])
                    (str " " (string/join " " (rest pieces)))
                    ""
                    :text))
                 (recur acc left (str buffer "h") :text))
             "["
               (let [pattern (re-pattern "^\\[[^\\]]+\\]\\([^\\)]+\\)")
                     guess (re-find pattern line)]
                 (if (some? guess)
                   (recur
                    (conj (if (= "" buffer) acc (conj acc [:text buffer])) [:link guess])
                    (string/replace line pattern "")
                    ""
                    :text)
                   (recur acc left (str buffer "[") :text)))
             "!"
               (let [pattern (re-pattern "^\\!\\[[^\\]]*\\]\\([^\\)]+\\)")
                     guess (re-find pattern line)]
                 (if (some? guess)
                   (recur
                    (conj (if (= "" buffer) acc (conj acc [:text buffer])) [:image guess])
                    (string/replace line pattern "")
                    ""
                    :text)
                   (recur acc left (str buffer "[") :text)))
             (recur acc left (str buffer cursor) :text))
         :code
           (if (= cursor "`")
             (recur (conj acc [:code buffer]) left "" :text)
             (recur acc left (str buffer cursor) :code))
         (throw (js/Error. (str "Unknown mode:" mode))))))))
