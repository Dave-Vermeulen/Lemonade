# Lemonade Blog 🍋

[![MIT License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Clojure](https://img.shields.io/badge/made_with-clojure-4d0083.svg)](https://clojure.org/)
[![Cryogen](https://img.shields.io/badge/powered_by-cryogen-34a853.svg)](https://cryogenweb.org/)

A modern static blog built with Clojure and Cryogen, featuring a custom-designed interface with bold colors and professional styling. Perfect for developers who want a minimalist yet expressive platform.

   🔫  📒  💻
"This is my rifle. There are many like it, but this one is mine."


![Blog Preview](public/images/blog-preview.png) <!-- Add actual screenshot -->

## Features ✨

- **Clojure-powered** static site generation
- **Nucleus theme** with custom modifications
- Bold color scheme: `#ed254e`, `#f9dc5c`, `#f4fffd`, `#011936`, `#465362`
- Responsive design with smooth animations
- Markdown-powered blog posts
- Syntax highlighting for code blocks
- SEO-friendly structure
- Easy GitHub Pages/Netlify deployment

## Installation ⚙️

### Prerequisites
- [Leiningen](https://leiningen.org/) (Clojure build tool)
- Java JDK 8+

### Quick Start
```bash
git clone https://github.com/your-username/lemonade-blog.git
cd lemonade-blog

### Install dependencies
```bash
lein deps

#### Build and serve locally
```bash
lein run
lein run serve  ;;or just use python3


## Configuration 🔧

### Core Settings (`content/config.edn`)
```clojure
{:site-title "Lemonade"
 :author "Dawūd Vermeulen"
 :theme "nucleus"
 :css ["reset.css" 
       "typography.css" 
       "menu.css" 
       "buttons.css" 
       "style.css" 
       "css/custom.css"]
 :pages [{:title "Home" :uri "/"}
         {:title "Portfolio" :uri "https://dawudvermeulen.vercel.app/"}
         {:title "Archives" :uri "/archives.html"}]}

### Color Scheme Implementation

Custom CSS variables in content/css/custom.css:

```clojure
    :root {
  --primary-red: #ed254e;
  --sunshine: #f9dc5c;
  --mint-cream: #f4fffd;
  --midnight: #011936;
  --slate: #465362;
}

## Customization 🎨

### Changing the Theme

1. Add theme files to themes/your-theme-name/
2. Update config.edn:
```clojure
{:theme "your-theme-name"}

### Modifying Styles

- content/css/custom.css - Main custom styles
- themes/nucleus/css/ - Theme base styles (override in custom.css)

### Adding Posts

- Create Markdown files in content/md/ following format

```bash
{:title "Post Title"
 :date "YYYY-MM-DD"
 :tags ["tag1" "tag2"]}

Your content in **Markdown**...

### Updating Navigation

- Modify the :pages vector in config.edn:

```clojure
:pages [
  {:title "New Link" :uri "/your-page.html"}
  {:title "External" :uri "https://example.com" :target "_blank"}
]


## Deployment 🚀

### GitHub Pages

1. Push public/ directory to gh-pages branch
2. Enable GitHub Pages in repo settings

### Vercel

1. Connect your repository
2. Set build command: lein run
3. Set publish directory: public

## License 📄

This project is licensed under the MIT License - see the LICENSE file for details.

## Troubleshooting 🔍

##### Common Issues:
- CSS not loading: Verify paths in config.edn match theme structure

- 404 Errors: Ensure all theme files exist in themes/nucleus/

- Syntax Highlighting: Confirm highlight.pack.js exists in themes/nucleus/js/
