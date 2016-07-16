(defproject nativeparedit "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.7.228"]]

  :clean-targets ["target"
                  "plugin/lib"]
  :plugins [[lein-cljsbuild "1.1.3"]]
  :cljsbuild
  {:builds
   [{:id :plugin
     :source-paths ["src/cljs" "test/cljs"]
     :compiler {:optimizations :simple
                :target :nodejs
                :hashbang false
                :pretty-print true
                :parellel-build true
                :language-in :ecmascript5
                :language-out :ecmascript5
                ;                :output-dir "plugin/lib"
                :output-to "plugin/lib/nativeparedit.js"}}
    ]}
  ; :profiles {:dev {:source-paths ["src/dev"]
  ;                  :dependencies [[thheller/shadow-build "1.0.207"]
  ;                                 [thheller/shadow-devtools "0.1.35"]]}}
  )
