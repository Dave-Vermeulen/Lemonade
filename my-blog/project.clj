(defproject cryogen "0.1.0"
            :description "Simple static site generator"
            :url "https://github.com/cryogen-project/cryogen"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.11.3"]
                           [ring/ring-devel "1.12.1"]
                           [compojure "1.7.1"]
                           [ring-server "0.5.0"]
                           [cryogen-flexmark "0.1.5"]
                           [cryogen-core "0.4.6"]
                           [ch.qos.logback/logback-classic "1.2.3"]
                           [org.slf4j/slf4j-api "1.7.30"]
                           [com.github.jai-imageio/jai-imageio-jpeg2000 "1.3.0"] ; JPEG2000 support
                           [org.xerial/sqlite-jdbc "3.34.0"]] ; SQLite JDBC driver
           
            :plugins [[lein-ring "0.12.5"]]
            :main cryogen.core
            :ring {:init cryogen.server/init
                   :handler cryogen.server/handler}
            :aliases {"serve"      ["run" "-m" "cryogen.server"]
                      "serve:fast" ["run" "-m" "cryogen.server" "fast"]})
