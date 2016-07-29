(ns nativeparedit.core
  (:require [cljs.nodejs :as nodejs]))

(nodejs/enable-util-print!)


(defn save [val]
  (println (str "saving: " val))
  (.push js/saved_clj val)
  (.push js/saved (clj->js val))
  val)

(def ^:dynamic active-editor ; for testing
  (fn []
    (-> js/atom .workspace .getActiveEditor)))

(defn in-string? [ed cursor]
  (let [scope (-> cursor .getScopeDescriptor .getScopesArray last)]
    (= "string.quoted.double.clojure" scope)))

(defn get-next-char [ed c]
  (let [p (.getBufferPosition c)]
    ;; todo use positionforcharacterindex
    (.getTextInBufferRange ed (clj->js [p [(.-row p) (inc (.-column p))]]))))

(defn insert-text! [ed c text]
  (-> ed .getBuffer (.insert (.getBufferPosition c) text)))


(defn open-round []
  nil)

(defn close-round []
  nil)

(defn close-round-and-newline []
  nil)

(defn open-square []
  nil)

(defn close-square []
  nil)





(def doublequote-test
  [["\"open string with no close|" "\"open string with no close\"|"]
   ["\"asd | asdas\"" "\"asd \"| asdas\""]
   ["\"|\"" "\"\"|"]
   ["(a b c |d e)" "(a b c \"|\" d e)"]
   ["(a b c| d e)" "(a b c \"|\" d e)"]
   ["|\"\"" "\"|\""]])

(defn doublequote [_]
  (let [ed (active-editor)
        cursor (save (.getLastCursor ed))
        str? (save (in-string? ed cursor))
        next (save (get-next-char ed cursor))]
    (cond
      (= next "\"") (.moveRight ed 1)
      str? (insert-text! ed cursor "\"")
      (= next " ") (do (insert-text! ed cursor " \"\"")
                       (.moveLeft ed 1))
      :else (do (insert-text! ed cursor "\"\" ")
                (.moveLeft ed 2) ))))

(defn -newline []
  nil)

(defn forward-delete []
  nil)

(defn backward-delete []
  nil)

(defn kill []
  nil)

(defn kill-word []
  nil)

(defn backwards-kill-word []
  nil)

(defn forward []
  nil)

(defn backward []
  nil)

(defn wrap-round []
  nil)

(defn splice-sexp []
  nil)

(defn splice-sexp-killing-backwards []
  nil)

(defn splice-sexp-killing-forward []
  nil)

(defn forward-slurp-sexp []
  nil)

(defn forward-barf-sexp []
  nil)

(defn backward-slurp-sexp []
  nil)

(defn backward-barf-sexp []
  nil)

(defn split-sexp []
  nil)

(defn join-sexp []
  nil)


(defn activate [state]
  (println "Activating paredit")
  (.add js/atom.commands "atom-workspace" "nativeparedit:doublequote", doublequote))

(defn deactivate [state]
  (println "Deactivating from paredit"))


(set! js/module.exports
  (js-obj "activate" activate
          "deactivate" deactivate
          "run_tests" #(nativeparedit.test-core/run-tests)))

(defn -main [& args]
  (println "running main()"))

;; noop - needed for :nodejs CLJS build
(set! *main-cli-fn* (constantly 0))
