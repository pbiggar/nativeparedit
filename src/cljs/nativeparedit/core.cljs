(ns nativeparedit.core
  (:require [cljs.nodejs :as nodejs]))

(nodejs/enable-util-print!)


(def commands (.-commands js/atom))

(defn active-editor []
  (-> js/atom .workspace .getActiveEditor))

(defn open-round []
  (insert)
  nil)

(defn close-round []
  nil)

(defn close-round-and-newline []
  nil)


(defn open-square []
  nil)

(defn close-square []
  nil)

(def dq-test
  [["\"asd | asdas\"" "\"asd \"| asdas\""]
   ["\"|\"" "\"\"|"]
   ["\"open string with no close|" "\"open string with no close\"|"]
   ["(a b c |d e)" "(a b c \"|\" d e)"]
   ["(a b c| d e)" "(a b c \"|\" d e)"]
   ["|\"\"" "\"|\""]])

(defn in-string? [cursor]
  (let [scope (.getScopeDescriptor ed)]
    (.log js/console scope)
    false))

(defn surrounding-chars [cursor]
  "ad")

(defn doublequote [_]
  (.log js/console "doublequote")
  (let [ed (getActiveEditor)
        cursor (.getLastCursor ed)
        str? (inString? cursor)
        [prev next] (surroundingChars cursor)]

    (cond ed
      (= next "\"") (moveCursorForward ed 1)
      str? (do (insert ed "\"") (moveCursorForward ed 1))
      (= next " " (do insert ed " \"\"") (moveCursorForward ed 2))
      :else (do (insert ed "\"\" ") (moveCursorForward ed 1)))))

(defn newline []
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
  (.log js/console "Activating paredit")
  (.add commands "atom-workspace" "nativeparedit:doublequote", doublequote))

(defn deactivate [state]
  (.log js/console "Deactivating from paredit"))


(set! js/module.exports
  (js-obj "activate" activate
          "deactivate" deactivate
          "run_tests" #(nativeparedit.test-core/run-tests)))

;; noop - needed for :nodejs CLJS build
(set! *main-cli-fn* (constantly nil))
