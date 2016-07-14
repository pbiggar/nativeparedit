(ns nativeparedit.core
  (:require [cljs.nodejs :as node]))

;; reference to atom shell API
(def ashell (node/require "atom"))

;; js/atom is not the same as require 'atom'.
(def commands (.-commands js/atom))




(defn open-round []
  nil)

(defn close-round []
  nil)

(defn close-round-and-newline []
  nil)

(defn open-square []
  nil)

(defn close-square []
  nil)

(defn doublequote []
  (.log js/console "doublequote")
  nil)

(defn newline []
  nil)

(defn forward-delete []
  nil)

(defn backward-delete []
  nil)

(defn kill []
  nil)

(defn kill-word []
  nil)

(defn backwards-kill-word []
  nil)

(defn forward []
  nil)

(defn backward []
  nil)

(defn wrap-round []
  nil)

(defn splice-sexp []
  nil)

(defn splice-sexp-killing-backwards []
  nil)

(defn splice-sexp-killing-forward []
  nil)

(defn forward-slurp-sexp []
  nil)

(defn forward-barf-sexp []
  nil)

(defn backward-slurp-sexp []
  nil)

(defn backward-barf-sexp []
  nil)

(defn split-sexp []
  nil)

(defn join-sexp []
  nil)





;; live-reload
;; calls stop before hotswapping code
;; then start after all code is loaded
;; the return value of stop will be the argument to start
(defn stop []
  nil)

(defn start [state]
  nil)


(defn activate [state]
  (.add commands "atom-workspace" "nativeparedit:doublequote", doublequote)
  (.log js/console "Hello World from {{raw-name}}"))

(defn deactivate [state]
  (.log js/console "Deactivating from {{raw-name}}"))
