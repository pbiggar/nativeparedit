(ns util.inspect)

(defmacro with-ns
  "Evaluates body in another namespace.  ns is either a namespace
  object or a symbol.  This makes it possible to define functions in
  namespaces other than the current one."
  [ns & body]
  `(do
     (create-ns ~ns)
     (binding [*ns* (the-ns ~ns)]
       (refer 'clojure.core)
       ~@(map (fn [form] `(eval '~form)) body))))

(defmacro inspect
  "prints the expression '<name> is <value>', and returns the value.
    Increments a metric if called in a production environment."
  [value]
  `(do
    (when (= nil js/saved)
      (set! js/saved (make-array))
      (set! js/saved_clj (make-array)))
    (let [value# (quote ~value)
          result# ~value]

      (.push js/saved_clj result#)
      (.push js/saved (clj->js result#))

      (println (str value# " "
                    "is "
                    (with-out-str (cljs.pprint/pprint result#))
                    "\n"))
      result#)))
