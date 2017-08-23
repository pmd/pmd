---
title: Writing Documentation
last_update: August 2017
permalink: pmd_devdocs_writing_documentation.html
keywords: documentation, jekyll, markdown
---

PMD's documentation uses [Jekyll](https://jekyllrb.com/) with
the [I'd rather be writing Jekyll Theme](http://idratherbewriting.com/documentation-theme-jekyll/index.html).

Here are some quick tips.

## Format

The pages are in general in [Github Flavored Markdown](https://kramdown.gettalong.org/parser/gfm.html).

## Structure

All documentation is stored in the folder `docs/`. This is the folder, that github and the travis-ci scripts
use to render the site.

New pages are stored in the different subfolders under `pages`. The folder structure resembles the sidebar structure.
Since all pages use a simple *permalink*, in the rendered html pages, all pages are flattened in one directory.
This makes it easy to view the documentation also offline.

## Building

There are two ways, to execute jekyll:

1.  Using [bundler](http://bundler.io/). This will install all the needed ruby packages locally and execute jekyll:

        # this is required only once, to download and install the dependencies
        bundle install
        # this builds the documentation under _site
        bundle exec jekyll build
        # this runs a local webserver as http://localhost:4005
        bundle exec jekyll serve

2.  Using [docker](https://www.docker.com/). This will create a local docker image, into which all needed ruby
    packages and jekyll is installed.

        # this is required only once to create a local docker image named "pmd-doc"
        docker build --no-cache -t pmd-doc .
        # this builds the documentation under _site
        docker run --rm=true -v "$PWD:/src" pmd-doc build -H 0.0.0.0
        # this runs a local webserver as http://localhost:4005
        docker run --rm=true -v "$PWD:/src" -p 4005:4005 pmd-doc serve -H 0.0.0.0

The built site is stored locally in the (git ignored) directory `_site`. You can
point your browser to `_site/index.html` to see the pmd documentation.

Alternatively, you can start the local webserver, that will serve the documentation.
Just go to http://localhost:4005.
If a page is modified, the documentation will automatically be rendered again and
all you need to do, is refreshing the page in your browser.

See also the script [pmd-jekyll.sh](https://gist.github.com/oowekyala/ee6f8801138861072c59ce683bdf737b).
It starts the jekyll server in the background and doesn't block the current shell.

## The sidebar

The sidebar is stored as a YAML document under `_data/sidebars/pmd_sidebar.yml`.

Make sure to add an entry there, whenever you create a new page.


## The frontmatter

Each page in jekyll begins with a YAML section at the beginning. This section
is separated by 3 dashes (`---`). Example:

    ---
    title: Writing Documentation
    last_update: August 2017
    permalink: pmd_devdocs_writing_documentation.html
    ---

    Some Text

    # Some header

There are a couple of possible fields. Most important and always
required are **title** and **permalink**.

By default, a page **toc** (table of contents) is automatically generated.
You can prevent this with "toc: false".

You can add **keywords**, that will be used for the on-site search: "keywords: documentation, jekyll, markdown"

It's useful to maintain a **last_update** field. This will be added at the bottom of the
page.

A **summary** can also be provided. It will be added in a box before the content.

For a more exhaustive list, see [Pages - Frontmatter](http://idratherbewriting.com/documentation-theme-jekyll/mydoc_pages.html#frontmatter).


## Alerts and Callouts

See [Alerts](http://idratherbewriting.com/documentation-theme-jekyll/mydoc_alerts.html).

For example, a info-box can be created like this:

    {%raw%}{% include note.html content="This is a note." %}{%endraw%}

It renders as:

{% include note.html content="This is a note." %}

Other available types are:

*   note.html
*   tip.html
*   warning.html
*   important.html


A callout is created like this:

    {%raw%}{% include callout.html content="This is a callout of type default.<br/><br/>There are the following types available: danger, default, primary, success, info, and warning." type="default" %}{%endraw%}

It renders as:

{% include callout.html content="This is a callout of type default.<br/><br/>There are the following types available: danger, default, primary, success, info, and warning." type="default" %}

## Code samples with syntax highlighting

This is as easy as:

    ``` java
    public class Foo {
        public void bar() { System.out.println("x"); }
    }
    ```

This looks as follows:

``` java
public class Foo {
    public void bar() { System.out.println("x"); }
}
```
