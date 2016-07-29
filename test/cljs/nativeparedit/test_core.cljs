(ns nativeparedit.test-core
  (:require-macros [jasmine.macros :refer [describe it expect]])
  (:require [cljs.test :refer-macros [deftest is testing] :as test]
            [nativeparedit.core :as np]
            [clojure.string :as str]))

(set! js/saved_clj (clj->js []))
(set! js/saved (clj->js []))

(defn save [val]
  (.push js/saved_clj val)
  (.push js/saved (clj->js val))
  (println (str "saving: " val))
  val)


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
  (let [[string col] (save (split-test-string initial))
        ed (. js/atom.workspace getActiveTextEditor)]
    (.setText ed (wrap string)) ; todo add a bunch of different wrapping texts
    (.setCursorBufferPosition ed #js[0, (save (offset col))])
    ed))

(defn run-test [data f]
  (doseq [[initial expected] data]
    (it (str "should match for " initial " -> " expected)
        (let [ed (editor-for-test initial)]
          (binding [np/active-editor (fn [] ed)]
                   (f)
                   (let [actual-text (-> ed .getText unwrap)
                         actual-col (-> ed .getCursorBufferPosition .-column unoffset)]
                     (expect (= (build-test-string actual-text actual-col) expected))))))))

(defn run_tests []
  (describe "doublequote"
            (js/beforeEach
             (fn []
               (js/waitsForPromise
                (fn []
                  (js/atom.packages.activatePackage "language-clojure")))
               (js/waitsForPromise
                (fn []
                  (js/atom.workspace.open "a.clj")))))
            (it "should activate"
                (expect (= true (js/atom.packages.isPackageLoaded "language-clojure")))
                (expect (= true (js/atom.packages.isPackageActive "language-clojure"))))
            (run-test np/doublequote-test np/doublequote)))
