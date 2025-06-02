(defproject blog-backend "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
               [org.postgresql/postgresql "42.5.4"]
               [org.clojure/java.jdbc "0.7.12"]
               [ring/ring-core "1.9.6"]
               [ring/ring-json "0.5.1"]
               [ring/ring-jetty-adapter "1.9.6"]
               [compojure "1.6.2"]
               [hiccup "2.0.0-alpha2"]
               [ring-cors "0.1.13"]
               [buddy/buddy-auth "3.0.323"]
               [buddy/buddy-hashers "1.8.1"]]
  :main ^:skip-aot blog-backend.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
