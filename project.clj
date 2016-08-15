(defproject nativeparedit "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.89"]]

  :npm {:dependencies [[source-map-support "*"]]
        :root :root}

  :clean-targets ["target" "out"
                  "plugin/lib/nativeparedit.js"
                  "node_modules"]

  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-npm "0.6.1"]]

  :cljsbuild
  {:builds
   [{:id :plugin
     :source-paths ["src/cljs" "test/cljs" "test/clj"]
     :compiler {:optimizations :none
                :target :nodejs
                :warnings {:single-segment-namespace false}
                :pretty-print true
                :main nativeparedit.test-core
                :cache-analysis true
                :source-map true
                :parallel-build true
                :output-dir "out"
                :output-to "out/main.js"}}
    ]})
