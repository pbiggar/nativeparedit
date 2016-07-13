(ns native-paredit.core
  (:require [cljs.nodejs :as node]))

;; reference to atom shell API
(def ashell (node/require "atom"))

;; js/atom is not the same as require 'atom'.
(def commands (.-commands js/atom))

;; get atom.CompositeDisposable so we can work with it
(def composite-disposable (.-CompositeDisposable ashell))

;; Atom for holding all disposables objects
(def disposables (atom []))

;; Initialise new composite-disposable so we can add stuff to it later
(def subscriptions (new composite-disposable))
(swap! disposables conj subscriptions)

(defn toggle []
    (.log js/console "native-paredit got toggled!"))

;; Dispose all disposables
(defn deactivate []
    (.log js/console "Deactivating native-paredit...")
    (doseq [disposable @disposables]
      (.dispose disposable)))

(defn serialize []
  nil)

(defn activate [state]
  (.log js/console "Hello World from native-paredit")
  (.add subscriptions
        (.add commands "atom-workspace" "native-paredit:toggle" toggle)))

;; live-reload
;; calls stop before hotswapping code
;; then start after all code is loaded
;; the return value of stop will be the argument to start
(defn stop []
  (let [state (serialize)]
    (deactivate)
    state))

(defn start [state]
  (activate state))
