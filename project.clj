(defproject native-paredit "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [org.clojure/core.async "0.2.374"]]

  :source-paths ["src/cljs"]
  :profiles {:dev {:source-paths ["src/dev"]
                   :dependencies [[thheller/shadow-build "1.0.207"]
                                  [thheller/shadow-devtools "0.1.35"]]}})
