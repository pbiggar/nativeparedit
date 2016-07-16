(ns nativeparedit.test-core
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [nativeparedit.core :as np]))


(deftest asdasd
  (is (= (+ 1 1) 2)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; tests
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn split-test-string [s]
  (let [col (.indexOf s "|")
        new-str (-> s (str/split "|") str/join)]
    [new-str col]))

(defn editor-for-test [test]
  (let [[string col] (split-test-string test)
        ed (TextEditor.)]
    (.setText ed string) ; todo add a bunch of different wrapping texts
    (.setCursor ed [0, pos])
    ed))

(defn run-test []
  (doseq [[initial expected] np/dq-test
          [expected-result expected-col] (np/splitTestString expected)
          ed (editor-for-test initial)]

    (binding [active-editor (fn [] ed)]
             (np/doublequote)
             (is (= expected-string (.getText ed)))
             (is (= expected-col (-> ed .getCursorBufferPosition .col))))))
