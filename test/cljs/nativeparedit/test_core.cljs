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



;; creating
(defn create-results [results]
  (println "creating results")
  (doall (for [{:keys [result type]} results]
           (do
            (println "result: ")
            (println result)
            (println type)
            (condp = type
                   :error (let [spec (-> js/jasmine .getEnv .-currentSpec) ]
                            (.fail spec {:message "asdas" :stack []}))
                   :pass []
                   :fail (let [actual (str (:actual result))
                               expected (str (:expected result))]
                           (-> actual js/expect (.toEqual expected))))))))

(defn create-all-specs [spec]
  (doseq [s @specs]
    (println "creating spec")
    (js/it (-> s :var str) #(create-results (:results s)))))

(defn create-suite! [suite]
  (js/describe (-> :ns suite str) create-all-specs))



(defmethod cljs.test/report [:cljs.test/default :fail] [m]
  (println "fail")
  (add-result! :fail m))

(defmethod cljs.test/report [:cljs.test/default :pass] [m]
  (println "pass")
  (add-result! :pass m))

(defmethod cljs.test/report [:cljs.test/default :error] [m]
  (println "error")
  (add-result! :error m))

(defmethod cljs.test/report [:cljs.test/default :end-test-var] [m]
  (println (str "adding spec" m))
  (add-spec! (:var m))
  (println "clearing test data")
  (clear-test-data!))

(defmethod cljs.test/report [:cljs.test/default :end-test-ns] [m]
  (println "creating suite")
  (create-suite! m)
  (println "clearing suite data")
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
