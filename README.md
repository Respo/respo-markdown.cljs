
Respo Markdown
----

Render Markdown subset to Respo DSL.

Demo http://repo.respo.site/markdown/

Supported features:

* Code block
* Headers, h1, h2, h3
* Quoteblock
* Unordered list
* Inline code
* Url
* Inline link
* Image link

### Usage

```clojure
[respo/markdown "0.1.8"]
```

```clojure
(respo-markdown.comp.md-article/comp-md-article "a\n" {})
; returns DSL
```

For options `{}`, `highlight.js` is suggested:

```clojure
{:highlight (fn [code lang]
   (let [result (.highlight js/hljs lang code)]
     (comment .log js/console "Result" result code lang js/hljs)
     (.-value result)))}
```

Write your own CSS to style the HTML:

```css
.md-article {}

.md-paragraph {
  margin: 16px 0;
}

.md-code-block {
  color: white;
  background-color: hsl(300, 80%, 20%);
  padding: 8px;
  display: block;
}
```

### Develop

Workflow https://github.com/mvc-works/coworkflow

### License

MIT
