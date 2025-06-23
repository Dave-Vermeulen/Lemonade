(ns lemonade.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [hiccup.util :refer [raw-string]]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [html5]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [garden.core :refer [css]]
            [clojure.string :as str])
  (:gen-class))

(def posts
  [{:id 1 
    :title "Oh Hello There Clojure!" 
    :content "This is my first post using Clojure! It's just supposed to be a test while I write some code to get this blog up and running. As I'm still getting the hang of the language the plan is to build with just the basics to get the blog up and running. Once the MVP is in production we will begin work on the next iteration using more advanced features of Clojure and ClojureScript. I hope you enjoy the strugglesome journey as much as I do!" 
    :author "Dawud Vermeulen"
    :date "2025-06-20"
    :tags ["Clojure" "Functional" "getting-started" "lisp"]
    :published true
    :slug "hello-clojure"
    :reading-time 2}
   {:id 2 
   :title "Functional First!" 
   :content "Clojure was the one that showed me that immutable data is a thing and OOP Java is not everything. Cause Java was my first, my native, my be all and end all. I was a Java developer for 10 years before I discovered Clojure. And it was a revelation! I learned that functional programming is not just a buzzword, it's a way of thinking about problems that leads to simpler, more maintainable code. Clojure's emphasis on immutability and pure functions has changed the way I approach software development."
   :author "Dawud Vermeulen"
   :date "2025-06-21" 
   :tags ["Functional" "Clojure" "Java" "OOP" "Functional Programming"]
   :published true
   :slug "functional-first"
   :reading-time 2}
   {:id 3 
   :title "The 5 Year Old Servant!" 
   :content "Since her birth we have followed the Tibetan technique for parenting. From the time she was born till the day she turned 5 we treated as one would treat royalty. We waited on her every breathe. Now she is taught to wait on us, to serve her family, to serve others. The transition to this phase of her development, the phase that teaches her humility through service to others, the phase that helps her combat the innate human condition of selfishness." 
   :author "Dawud Vermeulen"
   :date "2025-06-22"
   :tags ["Parenting" "SLAA"]
   :published true
   :slug "5-year-old-servant"
   :reading-time 3}])

(defn get-post-by-id [id]
  "find post by id"
  (->> posts
       (filter #(= (:id %) (Integer/parseInt (str id))))
       first))
(defn get-post-by-slug [slug]
  "find post by slug"
  (->> posts
       (filter #(= (:slug %) slug))
       first))
(defn get-published-posts []
  "get published posts, sorted by date, latest first"
  (->> posts
       (filter :published)
       (sort-by :date)
       reverse))
(defn get-posts-by-tag [tag]
  "get posts by tag"
  (->> posts
       (filter #(some #{tag} (:tags %)))
       (filter :published)))
(defn format-date [date]
  "convert date string to readable format"
  ;; use library for date to change format
  date)
(defn truncate-content [content max-length]
  "preview for post content"
  (if (> (count content) max-length)
    (let [truncated (subs content 0 max-length)
          last-space (.lastIndexOf truncated " ")]
      (if (> last-space 0)
        (str (subs truncated 0 last-space) "...")
        (str truncated "...")))
   content))
(defn estimate-reading-time [content]
  "estimate reading time based on post length"
  (let [words (count (str/split content #"\s+"))
        wpm 200]
    (max 1 (Math/round (/ words wpm)))))

(def styles
  (css 
    [:root {:--black "#1c1c1c"
            :--blue "#3c69e7" 
            :--blue-dark "#1e3ba3"        
            :--blue-light "#6b8aff"       
            :--poppy "#df2935"
            :--poppy-dark "#a01e2a"       
            :--sunglow "#fdca40"
            :--sunglow-dark "#c49a00"     
            :--platinum "#e6e8e6"
            :--white "#ffffff"            
            :--englishViolet "#413c58"
            :--mintGreen "#98ff98"
            :--fieryRed "#ff4500"
            :--transition "all 0.3s ease"}]
    
    [:body {:font-family "Lucida Console, 'Courier New', serif"
            :line-height "1.6"
            :color "var(--poppy)"
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
               :margin-top "1rem"}]
    
    [:.site-title {:color "var(--black)"
                   :font-size "clamp(2.2rem, 5vw, 3.2rem)"
                   :font-weight "800"
                   :margin "0"
                   :letter-spacing "-0.025em"
                   :text-shadow "0 2px 4px rgba(0,0,0,0.4)"}]
    
    [:.subtitle {:color "var(--black)"
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
                   :color "var(--blue-dark)"
                   :border-bottom "2px solid var(--sunglow)"
                   :padding-bottom "0.4rem"
                   :display "inline-block"}
     [:a {:color "var(--blue-dark)"
          :text-decoration "none"
          :transition "var(--transition)"}
      [:&:hover {:color "var(--poppy)"
                 :text-decoration "none"
                 :border-bottom-color "var(--poppy)"}]]]
    
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
                  :background "var(--blue-light)"
                  :color "var(--white)"
                  :text-decoration "none"
                  :font-weight "600"
                  :padding "0.9rem 1.8rem"
                  :border-radius "8px"
                  :transition "var(--transition)"
                  :box-shadow "0 4px 8px rgba(253, 202, 64, 0.3)"
                  :font-size "1.1rem"}
     [:&:hover {:background-color "var(--blue-dark)"
                :color "var(--white)"
                :transform "translateY(-2px)"
                :box-shadow "0 6px 12px rgba(55, 114, 255, 0.4)"}]]
    
    [:.post-full-title {:color "var(--blue-dark)"
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
                 :flex-wrap "wrap"
                 :gap "0.4rem"
                 :margin "1.2rem 0"
                 :width "100%"
                 :box-sizing "border-box"}]
    
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

(defn layout 
  ([title body] (layout title body nil))
  ([title body meta-opts]
   (let [description (get meta-opts :description "The organic writings of a Clojure fanboy, exploring the dunya and its code. The blog is about being a father to a young girl, about South Africa, Running road and trail, learning to code, being married to a beautiful model from KwaZulu Natal. The blog is about the life Allah has given me, and all I do to please God.")
         keywords (get meta-opts :keywords "clojure, functional programming, blog, fatherhood, south africa, running, coding, girls, children, kids, wife, cape town, normally")
         author (get meta-opts :author "Normally Blog")
         page-type (get meta-opts :type "website")]

     (html5 {:lang "en"}
       [:head
        [:meta {:charset "utf-8"}]
        [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
        [:meta {:name "description" :content description}]
        [:meta {:name "keywords" :content keywords}]
        [:meta {:name "author" :content author}]
        [:meta {:property "og:title" :content (str title " | Normally 🍋")}]
        [:meta {:property "og:description" :content description}]
        [:meta {:property "og:type" :content page-type}]
        [:meta {:property "og:site_name" :content "Normally 🍋"}]
        [:meta {:property "twitter:card" :content "summary"}]
        [:meta {:name "twitter:title" :content (str title " | Normally 🍋")}]
        [:meta {:name "twitter:description" :content description}]
        [:link {:rel "preconnect" :href "https://fonts.gstatic.com"}]
        [:link {:href "https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap"
                :rel "stylesheet"
                :crossorigin "anonymous"}]
        [:title (str title " | Normally Blog 🍋")]
        [:style {:type "text/css"} (raw-string styles)]]
       [:body
        [:a.skip-link {:href "#main"} "Skip to main content"]
        body]))))
(defn meta-for-post [post]
  {:description (let [content (:content post)
                      preview (if (> (count content) 160)
                                (str (subs content 0 160) "...")
                                content)]
                 preview)
     :keywords (if (:tags post)
                 (str/join "," (:tags post))
                 "clojure, functional programming, blog, fatherhood, south africa, running, husband, wife, kids, children")
     :type "article"
     :author (:author post "Normally Blog")})
  
(defn home-page []
  (layout "Home"
    [:div
     [:header.header
      [:h1.site-title "Normally 🍋"]
      [:p.subtitle "Accounts of a human beings spiritual experience."]]
     
     [:main#main.post-list
      (for [post (get-published-posts)]
        [:article.post-card
         [:div {:style "margin-bottom: 1rem; font-size: 0.9rem; color: #666;"}
          [:span "📅 " (format-date (:date post))]
          [:span {:style "margin-left: 1rem;"} "🕒 " (:reading-time post) " min read"]
          [:span {:style "margin-left: 1rem;"} "✍️ " (:author post)]]
         [:div.tag-list
          (for [tag (:tags post)]
            [:span.tag tag])]
         [:h2.post-title
          [:a {:href (str "/post/" (:id post))} (:title post)]]
         [:p.post-preview
          (truncate-content (:content post) 160)]])]]))


(defn post-page [id]
  (let [post (get-post-by-id id)]
    (if post
      (layout (:title post)
        [:div
         [:main#main
          [:article.post-content
           [:header {:style "margin-bottom: 2rem; padding-bottom: 1rem; border-bottom: 1px solid #eee;"}
            [:div {:style "margin-bottom: 1rem; font-size: 0.95rem; color: #666;"}
             [:span "📅 " (format-date (:date post))]
             [:span {:style "margin-left: 1rem;"} "⏱️ " (:reading-time post) " min read"]
             [:span {:style "margin-left: 1.5rem;"} "✍️ " (:author post)]]
           [:div.tag-list
            (for [tag (:tags post)]
              [:span.tag tag])]
           [:h1.post-full-title (:title post)]]
          [:div.post-full-content (:content post)]]
        [:div {:style "text-align: center; margin-top: 2.5rem;"}
         [:a.back-link {:href "/"} "← Back to all posts"]]]]
       (meta-for-post post))
    (layout "M.I.A."
      [:main#main
       [:div.post-content.not-found
        [:h1 "404 - 🍋 hai VOETSEK wena!!"]
        [:p "The droids you're looking for are not here."]
        [:p "This post does not exist or has been removed."]
        [:p "It might have been a figment of your imagination, or perhaps a glitch"]
        [:p "If you value your personal data, return to the homepage."]
        [:div {:style "margin-top: 2.5rem;"}
         [:a.back-link {:href "/"} "← Back to all posts"]]]]
     {:description "The droids you're looking for are not here. Post not found."}))))

(defn not-found-page []
  (layout "Page Not Found"
    [:main#main
     [:div.post-content.not-found
      [:h1 "404 - 🍋 hai VOETSEK wena!!"]
      [:p "The droids you're looking for are not here."]
        [:p "This post does not exist or has been removed."]
        [:p "It might have been a figment of your imagination, or perhaps a glitch"]
        [:p "If you value your personal data, return to the homepage."]
        [:div {:style "margin-top: 2.5rem;"}
         [:a.back-link {:href "/"} "← Back to home"]]]]
   {:description "The droids you're looking for are not here. Post not found."}))

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