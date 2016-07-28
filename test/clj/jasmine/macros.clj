(ns jasmine.macros)

(defmacro describe [description & body]
  `(js/describe ~description (fn [] ~@body)))

(defmacro it [description & test-forms]
  `(js/it ~description
          (fn []
            ~@test-forms)))

(defmacro expect [arg]
  (let [[a b & rest] arg
        [c d e] rest
        exp (cond
              (= '= a) (list 'toEqual c)
              :else (list 'throw (list 'js/Error. "TODO")))
        exp (concat (list '. (list 'js/expect b)) exp)]
    `(do (println '~a) (println '~exp) ~exp)))
