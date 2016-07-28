(defproject nativeparedit "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.8.51"]]

  :node-dependencies [[source-map-support "0.2.8"]]

  :clean-targets ["target" "out"
                  "plugin/lib/nativeparedit.js"]
  :plugins [[lein-cljsbuild "1.1.1"]]
  :cljsbuild
  {:builds
   [{:id :plugin
     :source-paths ["src/cljs" "test/cljs" "test/clj"]
     :compiler {:optimizations :none
                :target :nodejs
;                :hashbang false
                :pretty-print true
                :main nativeparedit.test-core
;                :cache-analysis true
;                :source-map-support true
;                :parallel-build true
;                :language-in :ecmascript5
;                :language-out :ecmascript5
                :output-dir "out"
                :output-to "out/main.js"}}
    ]}
  ; :profiles {:dev {:source-paths ["src/dev"]
  ;                  :dependencies [[thheller/shadow-build "1.0.207"]
  ;                                 [thheller/shadow-devtools "0.1.35"]]}}
  )
