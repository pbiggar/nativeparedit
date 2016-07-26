(ns nativeparedit.test-core
  (:require [cljs.test :refer-macros [deftest is testing run-tests] :as test]
            [nativeparedit.core :as np]))


(deftest passing-test
  (is (= (+ 1 1) 2)))

(deftest has-failing-test
  (is (= (+ 1 1) 3))
  (is (= (+ 1 1) 2))
  (is (= (+ 4 5) 6)))

(deftest errors-testing
  (is
   (throw (js/Error. "bug!"))))
;



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


(defn clear-test-data! []
  (swap! results (fn [_] (list))))

(defn clear-suite-data! []
  (swap! specs (fn [_] (list))))

(defn add-result! [type result]
  (swap! results conj {:type type :result result}))

(defn add-spec! [spec]
  (swap! specs conj {:spec spec :results @results}))

;; TODO: spec.endedAt


(set! js/saved_clj (clj->js []))
(set! js/saved (clj->js []))

(defn save [val]
  (.push js/saved_clj val)
  (.push js/saved (clj->js val))
  val)


;; creating
(defn create-results [results]
  (let [spec (-> js/jasmine .getEnv .-currentSpec)]
    (doseq [{:keys [result type]} results]
      (let [actual (:actual result)
            expected (:expected result)
            result (condp = type
                          :error (js/jasmine.ExpectationResult.
                                  (clj->js {:passed false
                                            :fileName (:file result)
                                            :message (.-message actual)
                                            :trace {:stack (.-stack actual)}}))
                          :fail (js/jasmine.ExpectationResult.
                                 (clj->js {:passed false
                                           :line (:line result)
                                           :expected (str expected)
                                           :actual (str actual)
                                           :trace {:stack nil}
                                           :message (str "Expected " expected ", but got " actual)}))
                          :pass (js/jasmine.ExpectationResult.
                                 (clj->js {:passed true
                                           :expected (str expected)
                                           :actual (str actual)
                                           :trace {:stack nil}
                                           :message "Passed"}))
                          )]
        (-> spec .-results_ (.addResult result))))))

(defn create-all-specs []
  (doseq [s @specs]
    (js/it (->> s :spec meta :name (str " should pass: ")) #(create-results (:results s)))))

(defn create-suite! [suite]
  (js/describe (-> :ns suite str) create-all-specs))



(defmethod cljs.test/report [:cljs.test/default :fail] [m]
  (println "fail")
  (println m)
  (add-result! :fail m))

(defmethod cljs.test/report [:cljs.test/default :pass] [m]
  (add-result! :pass m))

(defmethod cljs.test/report [:cljs.test/default :error] [m]
  (add-result! :error m))

(defmethod cljs.test/report [:cljs.test/default :end-test-var] [m]
  (add-spec! (:var m))
  (clear-test-data!))

(defmethod cljs.test/report [:cljs.test/default :end-test-ns] [m]
  (create-suite! m)
  (clear-suite-data!))


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
