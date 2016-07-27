(ns atom-reporter
  (:require [cljs.test :as test]))


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
