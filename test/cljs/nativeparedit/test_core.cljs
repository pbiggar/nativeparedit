(ns nativeparedit.test-core
  (:require [cljs.test :refer-macros [deftest is testing run-tests] :as test]
            [nativeparedit.core :as np]))

(set! js/saved_clj (clj->js []))
(set! js/saved (clj->js []))

(defn save [val]
  (.push js/saved_clj val)
  (.push js/saved (clj->js val))
  val)

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

;; todo store it in the test env
(def results (atom (list)))
(def specs (atom (list)))


(defn clear-results! []
  (swap! results (fn [_] (list))))

(defn clear-specs! []
  (swap! specs (fn [_] (list))))

(defn add-result! [result]
  (swap! results conj
         (js/jasmine.ExpectationResult. (clj->js result))))

(defn add-spec! [spec]
  (swap! specs conj {:spec spec :results @results})
  (clear-results!))


;;; Take cljs.test structures and get them into jasmine.
;;; Creates a suite (using `describe`) for each namespace,
;;; and a spec (using `it`) for each test var.
;;;
;;; TODO: namespaces can nest, so we can handle `testing` this way too.
;;;
;; You would think that we could just create the data structures in some form
;;; jasmine expects and pass them to it. Actually, the jasmine code is super
;;; convaluated, and you end up just reimplementing it. Whereas calling `describe`
;;; and `it` directly works quite well.


(defn create-results [results]
  (let [spec (-> js/jasmine .getEnv .-currentSpec)]
    (doseq [result results]
      (-> spec .-results_ (.addResult result)))))

(defn create-all-specs []
  (doseq [s @specs]
    (js/it (->> s :spec meta :name (str " should pass: ")) #(create-results (:results s)))))

(defn create-suite! [suite]
  (js/describe (-> :ns suite str) create-all-specs)
  (clear-specs!))


(defmethod cljs.test/report [:cljs.test/default :fail] [m]
  (add-result! {:passed false
                :line (:line m)
                :expected (-> m :expected str)
                :actual (-> m :actual str)
                :trace {:stack nil}
                :message (str "Expected " (:expected m) ", but got " (:actual m))}))

(defmethod cljs.test/report [:cljs.test/default :pass] [m]
  (add-result! {:passed true
                :expected (-> m :expected str)
                :actual (-> m :actual str)
                :trace {:stack nil}
                :message "Passed"}))

(defmethod cljs.test/report [:cljs.test/default :error] [m]
  (add-result! {:passed false
                :fileName (:file m)
                :message (-> m :actual .-message)
                :trace {:stack (-> m :actual .-stack)}}))

(defmethod cljs.test/report [:cljs.test/default :end-test-var] [m]
  (add-spec! (:var m)))

(defmethod cljs.test/report [:cljs.test/default :end-test-ns] [m]
  (create-suite! m))


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
