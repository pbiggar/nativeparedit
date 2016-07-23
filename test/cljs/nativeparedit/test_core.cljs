(ns nativeparedit.test-core
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [nativeparedit.core :as np]))


(deftest passing-test
  (is (= (+ 1 1) 2)))

(deftest has-failing-test
  (is (= (+ 1 1) 2))
  (is (= (+ 4 5) 6)))

(deftest errors-testing
  (is
   (throw (js/Error. "bug!"))))




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



;;;;;;;;;;;;;;;;;;;;
;;; test setup
;;;;;;;;;;;;;;;;;;;;

(def ^:dynamic test-callback nil)

(defmethod cljs.test/report [:cljs.test/default :end-run-tests] [m]
  (when test-callback (test-callback m)))

;; todo store it in the test env
(def results (atom (list)))
(def specs (atom (list)))
(def suite (atom nil))

;describe("asdas", -> it("asdasdasd", -> expect(1).toEqual(1)))
  ; (let [actual (:actual m)
  ;       expected (:expected m)
  ;       result (-> expected js/expect (.toEqual actual))])

(defn add-result [type result]
  (println type)
  (println result))

(defn add-spec [spec]
  (let [spec (js/it (-> m :name str) #(add-results % @results))]
    (swap! conj specs spec))
  (println val))

(defn add-suite [suite]
  ()
  (let [s (js/describe (-> :ns m str) (fn [] nil))]
    (js/console.log s)
    (reset! suite s))
  )

(defmethod cljs.test/report [:cljs.test/default :fail] [m]
  (add-result :fail m))

(defmethod cljs.test/report [:cljs.test/default :pass] [m]
  (add-result :pass m))

(defmethod cljs.test/report [:cljs.test/default :error] [m]
  (add-result :error m))

(defmethod cljs.test/report [:cljs.test/default :end-test-ns] [m]
  (add-suite m))

(defmethod cljs.test/report [:cljs.test/default :end-test-var] [m]
  (add-spec m))


; (defmethod cljs.test/report [:cljs.test/default :fail] [m]
;   (cljs.test/inc-report-counter! :fail)
;   (println "\n  FAIL in" (cljs.test/testing-vars-str m))
;   (when (seq (:testing-contexts (cljs.test/get-current-env)))
;     (println (cljs.test/testing-contexts-str)))
;   (when-let [message (:message m)]
;     (println message))
;   (println "    expected:" (pr-str (:expected m)))
;   (println "      actual:" (pr-str (:actual m)))
;   (println))

(defn run_tests [callback]
  (binding [test-callback callback]
           (run-tests)))
