(ns nativeparedit.test-core
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
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

;; creating
(defn create-results [results]
  (doall (for [{:keys [result type]} results]
           (let [actual (:actual result)
                 expected (:expected result)
                 spec (-> js/jasmine .getEnv .-currentSpec)]
            ;  (println (str "result: " result))
            ;  (println (str "type: " type))
            ;  (println (str "actual " actual))
            ;  (println (str "expected" expected))
             (condp = type
                    :error (let [e result]
                             (set! js/ads (clj->js e))
                             (set! js/ads2 actual)
                             (.fail spec {:message (.-message actual)
                                          :fileName (:file e)
                                          :line (:line e)
                                          :a (comment -> actual/rawStack)}))
                    :pass (-> expected js/expect (.toEqual expected))
                    :fail (-> (str actual) js/expect (.toEqual (str expected))))))))

(defn create-all-specs []
  (doseq [s @specs]
    (js/it (->> s :spec meta :name (str "should pass: ")) #(create-results (:results s)))))

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
