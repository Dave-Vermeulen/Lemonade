(defproject lemonade "0.1.0-SNAPSHOT"
  :description "a bare bones version of my first clojure blog"
  :url "https://lemonade-opal.vercel.app/"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                [ring/ring-core "1.9.6"]
                [ring/ring-jetty-adapter "1.9.6"]
                [compojure "1.7.0"]
                [hiccup "2.0.0-alpha2"]
                [ring/ring-defaults "0.3.4"]
                [garden "1.3.10"]]
  :main ^:skip-aot lemonade.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
             :dev {:dependencies [[ring/ring-mock "0.4.0"]]}})
