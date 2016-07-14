(ns build
  (:require [shadow.cljs.build :as cljs]
            [shadow.cljs.umd :as umd]
            [shadow.devtools.server :as devtools]
            [clojure.java.io :as io]))

(defn- plugin-setup []
  (-> (cljs/init-state)
      (cljs/set-build-options
       {:node-global-prefix "global.nativeparedit"})
      (cljs/find-resources-in-classpath)
      (umd/create-module
       {:open_round 'nativeparedit.core/open-round
        :close_round 'nativeparedit.core/close-round
        :close_round_and_newline 'nativeparedit.core/close-round-and-newline
        :open_square 'nativeparedit.core/open-square
        :close_square 'nativeparedit.core/close-square
        :doublequote 'nativeparedit.core/doublequote
        :newline 'nativeparedit.core/newline
        :forward_delete 'nativeparedit.core/forward-delete
        :backward_delete 'nativeparedit.core/backward-delete
        :kill 'nativeparedit.core/kill
        :kill_word 'nativeparedit.core/kill-word
        :backwards_kill_word 'nativeparedit.core/backwards-kill-word
        :forward 'nativeparedit.core/forward
        :backward 'nativeparedit.core/backward
        :wrap_round 'nativeparedit.core/wrap-round
        :splice_sexp 'nativeparedit.core/splice-sexp
        :splice_sexp_killing_backwards 'nativeparedit.core/splice-sexp-killing-backwards
        :splice_sexp_killing_forward 'nativeparedit.core/splice-sexp-killing-forward
        :forward_slurp_sexp 'nativeparedit.core/forward-slurp-sexp
        :forward_barf_sexp 'nativeparedit.core/forward-barf-sexp
        :backward_slurp_sexp 'nativeparedit.core/backward-slurp-sexp
        :backward_barf_sexp 'nativeparedit.core/backward-barf-sexp
        :split_sexp 'nativeparedit.core/split-sexp
        :join_sexp 'nativeparedit.core/join-sexp
        :activate 'nativeparedit.core/activate
        :deactivate 'nativeparedit.core/deactivate}
       {:output-to "plugin/lib/nativeparedit.js"})))

(defn release []
  (-> (plugin-setup)
      (cljs/compile-modules)
      (cljs/closure-optimize :simple)
      (umd/flush-module))
  :done)

(defn dev []
  (-> (plugin-setup)
      (cljs/watch-and-repeat!
        (fn [state modified]
          (-> state
              (cljs/compile-modules)
              (umd/flush-unoptimized-module))))))

(defn dev-once []
  (-> (plugin-setup)
      (cljs/compile-modules)
      (umd/flush-unoptimized-module))
  :done)

(defn dev-repl []
  (-> (plugin-setup)
      (devtools/start-loop
        {:reload-with-state true
         :console-support true
         :node-eval true}
        (fn [state modified]
          (-> state
              (cljs/compile-modules)
              (umd/flush-unoptimized-module)))))

  :done)
