
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
[respo/markdown "0.1.1"]
```

```clojure
(respo-markdown.comp.md-article/comp-md-article "a\n" {})
; returns DSL
```

### Develop

Workflow https://github.com/mvc-works/stack-workflow

### License

MIT
