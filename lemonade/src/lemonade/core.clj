(ns lemonade.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [hiccup.util :refer [raw-string]]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [html5]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [garden.core :refer [css]])
  (:gen-class))

(def posts
  [{:id 1 :title "Oh Hello there Clojure!" :content "This is my first post using Clojure! It's just supposed to be a test while I write some code to get this blog up and running. As I'm still getting the hang of the language the plan is to build with just the basics to get the blog up and running. Once the MVP is in production we will begin work on the next iteration using more advanced features of Clojure and ClojureScript. I hope you enjoy the strugglesome journey as much as I do!" :tags ["Clojure" "Functional"]}
   {:id 2 :title "Functional first!" :content "Clojure was the one that showed me that immutable data is a thing and OOP Java is not everything. Cause Java was my first, my native, my be all and end all. I was a Java developer for 10 years before I discovered Clojure. And it was a revelation! I learned that functional programming is not just a buzzword, it's a way of thinking about problems that leads to simpler, more maintainable code. Clojure's emphasis on immutability and pure functions has changed the way I approach software development." :tags ["Functional"]}
   {:id 3 :title "ClojureScript is the future!" :content "ClojureScript is the best thing since sliced bread! It allows you to write Clojure code that compiles to JavaScript, which means you can use all the power of Clojure in your web applications. The REPL-driven development experience is fantastic, and the ability to use Clojure's powerful data structures and functions in the browser is a game changer." :tags ["Parenting" "SLAA"]}])

(def styles
  (css 
    [:root {"--black" "#1c1c1c"
            "--blue" "#3c69e7"
            "--poppy" "#df2935"
            "--sunglow" "#fdca40"
            "--platinum" "#e6e8e6"
            "--englishViolet" "#413c58" 
            "--mintGreen" "#98ff98" 
            "--fieryRed" "#ff4500" 
            "--transition" "all 0.3s ease"}]
    
    [:body {:font-family "Lucida Console, 'Courier New', serif"
            :line-height "1.6"
            :color "poppy"
            :max-width "min(800px, 95%)"
            :margin "0 auto"
            :padding "20px"
            :background-color "var(--platinum)"}]
    
    [:.header {:text-align "center"
               :margin-bottom "3rem"
               :padding "3rem 1rem"
               :background "linear-gradient(45deg, var(--poppy), var(--englishViolet))"
               :border-radius "12px"
               :box-shadow "0 4px 12px rgba(0,0,0,0.1)"
               :color "platinum"
               :margin-top "1rem"}]
    
    [:.site-title {:color "black"
                   :font-size "clamp(2.2rem, 5vw, 3.2rem)"
                   :font-weight "800"
                   :margin "0"
                   :letter-spacing "-0.025em"
                   :text-shadow "0 2px 4px rgba(0,0,0,0.4)"}]
    
    [:.subtitle {:color "black"
                 :font-size "clamp(1.1rem, 3vw, 1.3rem)"
                 :margin "0.5rem 0 0 0"
                 :font-weight "500"
                 :max-width "600px"
                 :margin-left "auto"
                 :margin-right "auto"}]
    
    [:.code-highlight {:background "var(--mintGreen)"
                        :color "var(--black)"
                        :padding "0.5rem 1rem"
                        :border-radius "6px"
                        :font-family "'Courier New', monospace"}]
    
    [:.post-list {:display "grid"
                  :gap "1.8rem"
                  :grid-template-columns "repeat(auto-fill, minmax(300px, 1fr))"
                  :margin-top "2rem"}]
    
    [:.post-card {:background "var(--platinum)"
                  :border-radius "12px"
                  :padding "1.8rem"
                  :box-shadow "0 4px 10px rgba(0,0,0,0.05)"
                  :transition "var(--transition)"
                  :border-left "4px solid var(--sunglow)"}
     [:&:hover {:transform "translateY(-6px)"
                :box-shadow "0 8px 20px rgba(0,0,0,0.1)"}]]
    
    [:.post-title {:margin "0 0 0.8rem 0"
                   :font-size "1.6rem"
                   :font-weight "700"
                   :color "var(--blue)"
                   :border-bottom "2px solid var(--sunglow)"
                   :padding-bottom "0.4rem"
                   :display "inline-block"}
     [:a {:color "var(--blue)"
          :text-decoration "none"
          :transition "var(--transition)"}
      [:&:hover {:color "var(--fieryRed)"
                 :text-decoration "none"
                 :border-bottom-color "var(--fieryRed)"}]]]
    
    [:.post-preview {:color "#444"
                     :margin "0"
                     :line-height "1.7"}]
    
    [:.post-content {:background "white"
                     :border-radius "12px"
                     :padding "2.5rem"
                     :box-shadow "0 6px 18px rgba(0,0,0,0.05)"
                     :margin-bottom "2.5rem"
                     :border-left "5px solid var(--blue)"}]
    
    [:.back-link {:display "inline-block"
                  :background "var(--poppy)"
                  :color "var(--black)"
                  :text-decoration "none"
                  :font-weight "600"
                  :padding "0.9rem 1.8rem"
                  :border-radius "8px"
                  :transition "var(--transition)"
                  :box-shadow "0 4px 8px rgba(253, 202, 64, 0.3)"
                  :font-size "1.1rem"}
     [:&:hover {:background-color "var(--blue)"
                :color "sunglow"
                :transform "translateY(-2px)"
                :box-shadow "0 6px 12px rgba(55, 114, 255, 0.4)"}]]
    
    [:.post-full-title {:color "var(--blue)"
                        :font-size "clamp(1.8rem, 5vw, 2.8rem)"
                        :margin "0 0 1.8rem 0"
                        :line-height "1.2"
                        :padding-bottom "0.6rem"
                        :border-bottom "3px solid var(--sunglow)"
                        :display "inline-block"}]
    
    [:.post-full-content {:font-size "1.15rem"
                          :line-height "1.8"
                          :color "#333"}]
    
    [:.not-found {:text-align "center"
                  :padding "4rem 2rem"}]
    
    [:.skip-link {:position "absolute"
                  :top "-100px"
                  :left "0"
                  :background "var(--black)"
                  :color "white"
                  :padding "1rem"
                  :z-index "1000"
                  :transition "top 0.3s ease"}
     [:&:focus {:top "0"}]]
    
    [:.tag-list {:display "flex"
                 :gap "0.4rem"
                 :margin "1.2rem 0"}]
    
    [:.tag {:background "var(--sunglow)"
            :color "var(--black)"
            :font-size "0.85rem"
            :padding "0.3rem 0.9rem"
            :border-radius "100px"
            :font-weight "600"}]
    
    [".post-card, .post-content" {:transition "transform 0.3s ease, box-shadow 0.3s ease"}]
    
    ["@media (prefers-reduced-motion: reduce)" 
     [:* {:transition "none !important"
          :animation "none !important"
          :transform "none !important"}]]
    
    ["@media (max-width: 600px)" 
     [:.post-list {:grid-template-columns "1fr"}]
     [:.header {:padding "2rem 1rem"}]
     [:.post-content {:padding "1.8rem"}]
     [:.back-link {:display "block" 
                   :text-align "center"
                   :margin "0 auto"
                   :width "max-content"}]
     [:.site-title {:font-size "2.4rem"}]
     [:.subtitle {:font-size "1.1rem"}]]))

(defn layout [title body]
  (html5 {:lang "en"}
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:meta {:name "description" :content "A vibrant Clojure blog exploring functional programming"}]
    [:link {:href "https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" 
            :rel "stylesheet"
            :crossorigin "anonymous"}]
    [:link {:rel "preconnect" :href "https://fonts.gstatic.com"}]
    [:title (str title " | Lemonade Blog 🍋")]
    [:style {:type "text/css"} (raw-string styles)]]
   [:body
    [:a.skip-link {:href "#main"} "Skip to main content"]
    body]))

(defn home-page []
  (layout "My 🍋 Blog"
    [:div
     [:header.header
      [:h1.site-title "Lemonade 🍋"]
      [:p.subtitle "Refreshing takes on life and it's code."]]
     
     [:main#main.post-list
      (for [post posts]
        [:article.post-card
         [:div.tag-list
          (for [tag (:tags post)]
            [:span.tag tag])]
         [:h2.post-title 
          [:a {:href (str "/post/" (:id post))} (:title post)]]
         [:p.post-preview 
          (let [content (:content post)
                preview (if (> (count content) 100)
                          (str (subs content 0 100) "...")
                          content)]
            preview)]])]]))

(defn post-page [id]
  (let [post (first (filter #(= (:id %) (Integer/parseInt id)) posts))]
    (if post
      (layout (:title post)
        [:div
         [:main#main
          [:article.post-content
           [:div.tag-list
            (for [tag (:tags post)]
              [:span.tag tag])]
           [:h1.post-full-title (:title post)]
           [:div.post-full-content (:content post)]]
         [:div {:style "text-align: center; margin-top: 2.5rem;"}
          [:a.back-link {:href "/"} "← Back to all posts"]]]])
      (layout "Not Found" 
        [:main#main
         [:div.post-content.not-found
          [:h1 "Post not found"]
          [:p "The post you're looking for doesn't exist."]
          [:div {:style "margin-top: 2.5rem;"}
           [:a.back-link {:href "/"} "← Back to all posts"]]]]))))

(defn not-found-page []
  (layout "Page Not Found"
    [:main#main
     [:div.post-content.not-found
      [:h1 "404 - Page Not Found"]
      [:p "The page you requested doesn't exist."]
      [:div {:style "margin-top: 2.5rem;"}
       [:a.back-link {:href "/"} "← Back to home"]]]]))

(defroutes app-routes
  (GET "/" [] (home-page))
  (GET "/post/:id" [id] (post-page id))
  (route/not-found (not-found-page)))

(def app
  (wrap-defaults app-routes site-defaults))

(defn -main
  "the main man"
  []
  (println "Starting server on http://localhost:3000")
  (run-jetty app {:port 3000 :join? false}))