(ns nativeparedit.test-core
  (:require-macros [jasmine.macros :refer [describe it expect]]
                   [util.inspect :refer [inspect]])
  (:require [cljs.test :refer-macros [deftest is testing] :as test]
            [cljs.nodejs :as nodejs]
            [nativeparedit.core :as np]
            [clojure.string :as str]
            [nativeparedit.test-paredit-el :as el]))


(defn split-test-string [s]
  (let [col (.indexOf s "|")
        new-str (-> s (str/split #"\|") str/join)]
    [new-str col]))

(defn build-test-string [s col]
  (str (.substring s 0 col) "|" (.substring s col)))

(def -prolog "(defn x [] ")
(def -epilog ")")

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
    (.setCursorBufferPosition ed #js[0, (offset col)])
    ed))

(defn run-test [initial expected f]
  (it (str "should match for " initial " -> " expected)
      (let [ed (editor-for-test initial)]
        (binding [np/active-editor (fn [] ed)]
                 (f)
                 (let [actual-text (-> ed .getText unwrap)
                       actual-col (-> ed .getCursorBufferPosition .-column unoffset)]
                   (expect (= (build-test-string actual-text actual-col) expected)))))))

(defn run-paredit-el-tests []
  (doall (for [[[type] sub] (partition 2 (partition-by string? el/tests))]
           (describe (str type " tests")
                     (doseq [[command fn-name tests] sub]
                       (let [fn-name (.substr (str (inspect fn-name)) 8)
                             f (aget nativeparedit.core (inspect (str/replace-all fn-name #"-" "_")))]
                         (inspect f)
                         (describe (str fn-name " (" command ") tests")
                                   (doall (for [[actual expected] (partition 2 tests)]
                                            (run-test actual expected f))))))))))

(defn setup-tests []
  (js/beforeEach
   (fn []
     (js/waitsForPromise
      #(js/atom.packages.activatePackage "language-clojure"))
     (js/waitsForPromise
      #(js/atom.workspace.open "a.clj")))))


(defn run_tests []
  (setup-tests)

  (describe "test suite should work"
            (it "should activate"
                (expect (= true (js/atom.packages.isPackageLoaded "language-clojure")))
                (expect (= true (js/atom.packages.isPackageActive "language-clojure")))))
  (describe "doublequote tests"
            (doseq [[initial expected] np/doublequote-test]
              (run-test initial expected np/doublequote)))

  (describe "lisp-paredit tests"
            (let [fs (nodejs/require "fs")
                  contents (.readFileSync fs "test_lisp_paredit.js" false)]
              ; do node/vm eval
              (inspect (js/eval contents))))

  (describe "paredit-el tests"
            (run-paredit-el-tests)))
