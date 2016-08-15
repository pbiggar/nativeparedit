(ns nativeparedit.core
  (:refer-clojure :exclude [newline])
  (:require [cljs.nodejs :as nodejs]))

(nodejs/enable-util-print!)

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




(def open-round-test
  [["(a b |c d)" "(a b (|) c d)"]
   ["(foo \"bar |baz\" quux)" "(foo \"bar (|baz\" quux)"]])

(defn open-round []
  nil)



(def close-round-test
  [["(a b |c )" "(a b c)|"]
   ["; Hello,| world!" "; Hello,)| world!]"]])

(defn close-round []
  nil)


(def close-round-and-newline-test
  [["(defun f (x| ))" "(defun f (x)\n  |)"]
   ["; (Foo.|" "; (Foo.)|"]])

(defn close-round-and-newline []
  nil)


(def open-square-test
  [["(a b |c d)" "(a b [|] c d)"]
   ["(foo \"bar |baz\" quux)" "(foo \"bar [|baz\" quux)"]])

(defn open-square []
  nil)

["(define-key keymap [frob| ] ’frobnicate)" "(define-key keymap [frob]| ’frobnicate)"]
["; [Bar.|" "; [Bar.]|"]

(defn close-square []
  nil)



["(frob grovel |full lexical)" "(frob grovel \"|\" full lexical)"]
["(frob grovel \"|\" full lexical)" "(frob grovel \"\"| full lexical)"]
["(foo \"bar |baz\" quux)" "(foo \"bar \\\"|baz\" quux)"]
["(frob grovel)   ; full |lexical" "(frob grovel)   ; full \"|lexical"]


(def doublequote-test
  [["\"open string with no close|" "\"open string with no close\"|"]
   ["\"asd | asdas\"" "\"asd \"| asdas\""]
   ["\"|\"" "\"\"|"]
   ["(a b c |d e)" "(a b c \"|\" d e)"]
   ["(a b c| d e)" "(a b c \"|\" d e)"]
   ["|\"\"" "\"|\""]])

(defn doublequote [_]
  (let [ed (active-editor)
        cursor (.getLastCursor ed)
        str? (in-string? ed cursor)
        next (get-next-char ed cursor)]
    (cond
      (= next "\"") (.moveRight ed 1)
      str? (insert-text! ed cursor "\"")
      (= next " ") (do (insert-text! ed cursor " \"\"")
                       (.moveLeft ed 1))
      :else (do (insert-text! ed cursor "\"\" ")
                (.moveLeft ed 2) ))))

(defn blackslash [_])

(defn semicolon [_])

(defn meta-doublequote [_])

(defn comment-dwim [_])

["(let ((n frobbotz)) |(display (+ n 1)\nport))"
 "(let ((n frobbotz))\n|(display (+ n 1)\nport))"]

(defn newline []
  nil)

["(quu|x \"zot]\")" "(quu| \"zot\")"]
["(quux |\"zot]\")" "(quux \"|zot\")"]
["(quux \"|zot\")" "(quux \"|ot\")"]
["(foo (|) bar)" "(foo | bar)"]
["|(foo bar)" "(|foo bar)"]

(defn forward-delete []
  nil)

["(\"zot\" q|uux)" "(\"zot\" |uux)"]
["(\"zot\"| quux)" "(\"zot|\" quux)"]
["(\"zot|\" quux)" "(\"zo|\" quux)"]
["(foo () bar)" "(foo bar)"]
["(foo bar)" "(foo bar)"]

(defn backward-delete []
  nil)

["(foo bar)| ; Useless comment!" "(foo bar)|"]
["(|foo bar) ; Useless comment!" "(|) ; Useless comment!"]
["|(foo bar) ; Useless line!" "|"]
["(foo \"|bar baz\"\n    quux)" "(foo \"|\"\n    quux)"]

(defn kill []
  nil)


["|(foo bar) ; baz" "| bar) ; baz"]
["| bar) ; baz" "(|) ; baz"]
["(|) ; baz" "() ; |"]
[";;; |Frobnicate\n(defun frobnicate ...)" ";;; |\n(defun frobnicate ...)"]
[";;; |\n(defun frobnicate ...)" ";;;(| frobnicate ...)"]

(defn forward-kill-word []
  nil)


["(foo bar) ; baz\n(quux)|" "(foo bar) ; baz\n(|)"]
["(foo bar) ; baz\n(|)" "(foo bar) ; |\n()"]
["(foo bar) ; |\n()" "(foo |) ;\n()"]
["(foo |) ;\n()" "(|) ;\n()"]
(defn backward-kill-word []
  nil)

["(foo |(bar baz) quux)" "(foo (bar baz)| quux)"]
["(foo (bar baz)|)" "(foo (bar baz))|"]
(defn forward []
  nil)

["(foo (bar baz)| quux)" "(foo |(bar baz) quux)"]
["(|(foo) bar)" "|((foo) bar)"]
(defn backward []
  nil)

(defn backward-up [])

(defn backward-down [])

(defn forward-down [])

(defn forward-up [])

["(foo |bar baz)" "(foo (|bar) baz)"]
(defn wrap-round []
  nil)

["(foo (bar| baz) quux)" "(foo bar| baz quux)"]
(defn splice-sexp []
  nil)

["(foo (let ((x 5)) |(sqrt n)) bar)" "(foo |(sqrt n) bar)"]
(defn splice-sexp-killing-backward []
  nil)

["(a (b c| d e) f)" "(a b c| f)"]
(defn splice-sexp-killing-forward []
  nil)

(defn raise-sexp [])

(defn convolute-sexp [])

["(foo (bar |baz) quux zot)" "(foo (bar |baz quux) zot)"]
["(a b ((c| d)) e f)" "(a b ((c| d) e) f)"]
["(a b ((c| d) e) f)" "(a b ((c| d e)) f)"]
(defn forward-slurp-sexp []
  nil)

["(foo (bar |baz quux) zot)" "(foo (bar |baz) quux zot)"]
(defn forward-barf-sexp []
  nil)

["(foo bar (baz| quux) zot)" "(foo (bar baz| quux) zot)"]
["(a b ((c| d)) e f)" "(a (b (c| d)) e f)"]
["(a (b (c| d)) e f)" "(a ((b c| d)) e f)"]
(defn backward-slurp-sexp []
  nil)

["(foo (bar baz| quux) zot)" "(foo bar (baz| quux) zot)"]
(defn backward-barf-sexp []
  nil)

["(hello| world)" "(hello)| (world)"]
["\"Hello, |world!\"" "\"Hello, \" |\"world!\""]
(defn split-sexp []
  nil)

["(hello)| (world)" "(hello| world)"]
["\"Hello, \" \"world!\"" "\"Hello, world!\""]
["hello-\n|  world" "hello-world"]

(defn join-sexps []
  nil)

(defn recenter-on-sexp [])

(defn reindent-defun [])



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
