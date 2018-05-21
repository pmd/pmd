---
title: PMD Documentation website
keywords: java
permalink: index.html
toc: false
summary: >
    Welcome to PMD, an extensible cross-language static code analyzer.
    It finds common programming flaws like unused variables, empty catch blocks, unnecessary object creation,
    and so forth. Additionally it includes CPD, the copy-paste-detector. CPD finds duplicated code.
last_updated: August 2017
author: Jeff Jensen <jjensen@apache.org>, Andreas Dangel <andreas.dangel@adangel.org>
---


## Welcome to PMD!

First time user? Then you may be interested in our [quickstart series](TODO)!

<br/>

<div id="grid" class="row">

    {% include custom/shuffle_panel.html
       tag="getting_started"
       description="These pages summarize the gist of PMD usage to get you started quickly:"
       title="Getting started" %}


    {% include custom/shuffle_panel.html
       tag="rule_references"
       title="Rule references"
       description="Pick your language to find out about the rule it supports:"
       image="fa-database"
       titlemaker="page.language_name" %}

    {% include custom/shuffle_panel_filler.html %}

</div>


<!-- {% include image.html file="pmd-logo-big.png" alt="PMD Logo" %} -->

{% include links.html %}
