---
title: Main Landing Page
permalink: pmd_projectdocs_committers_main_landing_page.html
last_updated: March 2020
author: Andreas Dangel <andreas.dangel@pmd-code.org>
---

The main homepage of PMD <https://pmd.github.io> is hosted by Github Pages.

The repository is <https://github.com/pmd/pmd.github.io>.

It uses [Jekyll](https://jekyllrb.com/) to generate the static html pages. Jekyll is
executed by github for every push to the repository. Please note, that it takes some time
until Jekyll has been executed and due to caching, the homepage is not updated immediately.
It usually takes 15 minutes.


## Contents

* Main page - aka "Landing page": <https://pmd.github.io>
  * Layout: [_layouts/default.html](https://github.com/pmd/pmd.github.io/blob/master/_layouts/default.html).
    It includes all the sub section, which can be found in the includes directory [_includes/](https://github.com/pmd/pmd.github.io/tree/master/_includes)
  * The latest PMD version is configured in `_config.yml` and the variables `site.pmd.latestVersion` are used
    e.g. in [_includes/home.html](https://github.com/pmd/pmd.github.io/blob/master/_includes/home.html).
* Blog - aka "News": <https://pmd.github.io/news/>
  * This is a section on main page. It shows the 5 latest news. See [_includes/news.html](https://github.com/pmd/pmd.github.io/blob/master/_includes/news.html).
  * There is also a sub page "news" which lists all news.
    * Layout: [_layouts/news.html](https://github.com/pmd/pmd.github.io/blob/master/_layouts/news.html)
    * Page (which is pretty empty): [news.html](https://github.com/pmd/pmd.github.io/blob/master/news.html)
* Documentation for the latest release: <https://pmd.github.io/latest/>
  * The PMD documentation of the latest release is simply copied as static html into the folder [latest/](https://github.com/pmd/pmd.github.io/tree/master/latest).
    This makes the latest release documentation available under the stable URL
    <https://pmd.github.io/latest/>. This URL is also used for the [sitemap.xml](https://github.com/pmd/pmd.github.io/blob/master/sitemap.xml).
* Documentation for previous releases are still being kept under the folders `pmd-<version>/`.


## Building the page locally

Since the repository contains the documentation for many old PMD releases, it is quite big. When executing
Jekyll to generate the site, it copies all the files to the folder `_site/` - and this can take a while.

In order to speed things up locally, consider to add `pmd-*` to the exclude patterns in `_config.yml`. See
also the comments in this file.

Then it is a matter of simply executing `bundle exec jekyll serve`. This will generate the site and host
it on localhost, so you can test the page at <http://127.0.0.1:4000>.


## Updates during a release

When creating a new PMD release, some content of the main page need to be updated as well.
This done as part of the [Release process](pmd_projectdocs_committers_releasing.html), but is
summarized here as well:

* The versions (e.g. `pmd.latestVersion`) needs to be updated in `_config.yml`
  * This is needed to generate the correct links and texts for the latest version on landing page
* The new PMD documentation needs to be copied to `/pmd-<version>/`
* Then this folder needs to copied to `/latest/`, actually replacing the old version.
* A new blog post with release notes is added: `/_posts/YYYY-mm-dd-PMD-<version>.md`
* The sitemap `sitemap.xml` is regenerated

Some of these steps are automated through `do-release.sh` (like blog post), some are manual steps
(updating the version in _config.yml) and other steps are done on the travis-ci-build (like
copying the new documentation).

## Adding a new blog post

Adding a new blog post is as easy as:

* Creating a new file in the folder "_posts": `/_posts/YYYY-mm-dd-<title>.md`
* The file name needs to fit this pattern. The date of the blog post is taken from the file name. The "<title>"
  is used for the url.
* The file is a markdown file starting with a frontmatter for jekyll. Just use this template for the new file:

```
---
layout: post
title: Title
---

Here comes the text
```

Once you commit and push it, Github will run Jekyll and update the page. The Jekyll templates take care that
the new post is recognized and added to the news section and also on the news subpage.
