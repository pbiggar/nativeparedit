(ns nativeparedit.test-core
  (:require-macros [jasmine.macros :refer [describe it expect]])
  (:require [cljs.test :refer-macros [deftest is testing] :as test]
            [atom-reporter]
            [nativeparedit.core :as np]
            [clojure.string :as str]))

(set! js/saved_clj (clj->js []))
(set! js/saved (clj->js []))

(defn save [val]
  (.push js/saved_clj val)
  (.push js/saved (clj->js val))
  val)


(defn split-test-string [s]
  (let [col (.indexOf s "|")
        new-str (-> s (str/split #"\|") str/join)]
    [new-str col]))

(def -prolog "(defn x [] \n  ")
(def -epilog "\n)")

(defn wrap [text]
  (str -prolog text -epilog))

(defn unwrap [text]
  (.substring text (.-length -prolog) (- (.-length text) (.-length -epilog))))

(defn offset [size]
  (+ size (.-length -prolog)))

(defn unoffset [size]
  (- size (.-length -prolog)))

(defn editor-for-test [initial]
  (let [[string col] (split-test-string initial)
        ed (. js/atom.workspace getActiveTextEditor)]
    (.setText ed (wrap string)) ; todo add a bunch of different wrapping texts
    (.setCursorBufferPosition ed [0, (offset col)])
    ed))

(defn run-test [data f]
  (doseq [[initial expected] data]
    (let [[expected-result expected-col] (split-test-string expected)
          ed (editor-for-test initial)]
      (binding [np/active-editor (fn [] ed)]
               (f)
               (expect (= (-> ed .getText unwrap) expected-result))
               (expect (= (-> ed .getCursorBufferPosition save .-column unoffset) expected-col))))))

(defn run_tests []

  (describe "doublequote"
            (js/beforeEach (fn [] (js/waitsForPromise (fn [] (js/atom.workspace.open "a.clj")))))
            (it "should match expected data"
                (run-test np/dq-test np/doublequote))))
