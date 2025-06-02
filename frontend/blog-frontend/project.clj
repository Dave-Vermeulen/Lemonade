(defproject blog-frontend "0.1.0-SNAPSHOT"
  :description "Retro Clojure Blog Frontend"
  :url "http://localhost:3449"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.9.1"

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.11.60"]
                 [org.clojure/core.async "0.4.500"]
                 [reagent "1.1.0"]
                 [cljs-http "0.1.46"]
                 [garden "1.3.10"]]

  :plugins [[lein-figwheel "0.5.20"]
            [lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]
            [lein-npm "0.6.2"]]

  :npm {:dependencies [[react "17.0.2"]
                       [react-dom "17.0.2"]
                       ["@cljs-oss/module-deps" "1.1.0"]]}

  :source-paths ["src" "dev"]

  :cljsbuild {
    :builds [
      {
        :id "dev"
        :source-paths ["src" "dev"]
        :figwheel {
          :on-jsload "blog-frontend.core/on-js-reload"
          :open-urls ["http://localhost:3449/index.html"]
        }
        :compiler {
          :main blog-frontend.core
          :asset-path "js/compiled/out"
          :output-to "resources/public/js/compiled/blog_frontend.js"
          :output-dir "resources/public/js/compiled/out"
          :source-map-timestamp true
          :preloads [devtools.preload]
          :install-deps true
          :npm-deps true
          :infer-externs true
          :closure-warnings {:non-standard-jsdoc :off}
        }
      }
    ]
  }

  :figwheel {
    :css-dirs ["resources/public/css"]
  }

  :profiles {
    :dev {
      :dependencies [
        [binaryage/devtools "1.0.0"]
        [figwheel-sidecar "0.5.20"]
      ]
      :source-paths ["src" "dev"]
      :clean-targets ^{:protect false} [
        "resources/public/js/compiled"
        :target-path
      ]
    }
  }
)