---
title: Writing documentation
tags: [devdocs]
last_update: August 2017
permalink: pmd_devdocs_writing_documentation.html
keywords: documentation, jekyll, markdown
author: Andreas Dangel <andreas.dangel@adangel.org>
---

PMD's documentation uses [Jekyll](https://jekyllrb.com/) with
the [I'd rather be writing Jekyll Theme](http://idratherbewriting.com/documentation-theme-jekyll/index.html).

Here are some quick tips.

## Format

The pages are in general in [Github Flavored Markdown](https://kramdown.gettalong.org/parser/gfm.html).

## Structure

The documentation sources can be found in two places based on how they are generated:
- the ones that are manually written (like the one you are reading);
- and the ones that are generated automatically from the category files. All the rule documentation
pages are generated that way.

### Handwritten documentation

All handwritten documentation is stored in the subfolders under `docs/pages`. The folder structure resembles the sidebar structure.
Since all pages use a simple *permalink*, in the rendered html pages, all pages are flattened in one directory.
This makes it easy to view the documentation also offline.

### Rule documentation

The categories for a language `%lang%` are located in
`pmd-%lang%/src/main/resources/category/%lang% `. So for Java the categories
can be found under [pmd-java/src/main/resources/category/java](https://github.com/pmd/pmd/tree/master/pmd-java/src/main/resources/category/java).
The XML category files in this directory are transformed during build into markdown pages
describing the rules they contain. These pages are placed under `docs/` like the handwritten
documentation, and are then rendered with Jekyll like the rest of them. The rule documentation
generator is the separate submodule `pmd-doc`.

Modifying the documentation of a rule should thus not be done on the markdown page,
but directly on the XML `rule` tag corresponding to the rule, in the relevant
category file.

The XML documentation of rules can contain GitHub flavoured markdown.
Just wrap the markdown inside CDATA section in the xml. CDATA sections preserve
all formatting inside the delimiters, and allow to write code samples without
 escaping special xml characters. For example:
```
<rule ...>
 <description>
 <![CDATA[
   Full description, can contain markup

   And paragraphs
 ]]>
 </description>
 ...
</rule>
```

## Custom Liquid Tags

We have some additional custom liquid tags that help in writing the documentation.

Here's a short overview:

| Liquid | Rendered as |
|:-------|:------------|
| `{% raw %}{% rule "java/codestyle/LinguisticNaming" %}{% endraw %}`               | {% rule "java/codestyle/LinguisticNaming" %} |
| `{% raw %}{% jdoc core::Rule %}{% endraw %}`                                      | {% jdoc core::Rule %} |
| `{% raw %}{% jdoc !q!core::Rule %}{% endraw %}`                                   | {% jdoc !q!core::Rule %} |
| `{% raw %}{% jdoc core::Rule#setName(java.lang.String) %}{% endraw %}`            | {% jdoc core::Rule#setName(java.lang.String) %} |
| `{% raw %}{% jdoc !c!core::Rule#setName(java.lang.String) %}{% endraw %}`         | {% jdoc !c!core::Rule#setName(java.lang.String) %} |
| `{% raw %}{% jdoc !a!core::Rule#setName(java.lang.String) %}{% endraw %}`         | {% jdoc !a!core::Rule#setName(java.lang.String) %} |
| `{% raw %}{% jdoc !ac!core::Rule#setName(java.lang.String) %}{% endraw %}`        | {% jdoc !ac!core::Rule#setName(java.lang.String) %} |
| `{% raw %}{% jdoc core::properties.PropertyDescriptor %}{% endraw %}`             | {% jdoc core::properties.PropertyDescriptor %} |
| `{% raw %}{% jdoc_nspace :jast java::lang.java.ast %}{% jdoc jast::ASTAnyTypeDeclaration %}{% endraw %}`       | {% jdoc_nspace :jast java::lang.java.ast %}{% jdoc jast::ASTAnyTypeDeclaration %} |
| `{% raw %}{% jdoc_nspace :jast java::lang.java.ast %}{% jdoc_package :jast %}{% endraw %}`                     | {% jdoc_nspace :jast java::lang.java.ast %}{% jdoc_package :jast %} |
| `{% raw %}{% jdoc_nspace :PrD core::properties.PropertyDescriptor %}{% jdoc !ac!:PrD#uiOrder() %}{% endraw %}` | {% jdoc_nspace :PrD core::properties.PropertyDescriptor %}{% jdoc !ac!:PrD#uiOrder() %} |
| `{% raw %}{% jdoc_old core::Rule %}{% endraw %}`                                  | {% jdoc_old core::Rule %}

For the javadoc tags, the standard PMD maven modules are already defined as namespaces, e.g. `core`, `java`, `apex`, ....

For the implementation of these tags, see the [_plugins](https://github.com/pmd/pmd/tree/master/docs/_plugins) folder.


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

## Checking for dead links

`mvn verify -pl pmd-doc`. This only checks links within the site. HTTP links can be checked
by specifying `-Dpmd.doc.checkExternalLinks=true` on the command line.
