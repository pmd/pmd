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


{% unless site.output == "pdf" %}
<script src="js/jquery.shuffle.min.js"></script>
<script src="js/jquery.ba-throttle-debounce.min.js"></script>
{% endunless %}

<div class="filter-options">
      <button class="btn btn-primary" data-group="all">All</button>
      <button class="btn btn-primary" data-group="getting_started">Getting Started</button>
      <button class="btn btn-primary" data-group="userdocs">User documentation</button>
      <button class="btn btn-primary" data-group="extending">Extending PMD</button>
      <button class="btn btn-primary" data-group="contributing">Contributing</button>
</div>


<div id="grid" class="row">

    {% include custom/shuffle_panel.html
       tags="getting_started"
       datagroups='["getting_started"]'
       description="These pages summarize the gist of PMD usage to get you started quickly."
       title="Getting started" %}



    {% include custom/shuffle_panel.html
       tags="rule_references"
       datagroups='["userdocs"]'
       title="Rule references"
       description="Pick your language to find out about the rule it supports."
       image="fa-database"
       titlemaker="page.language_name" %}

    {% include custom/shuffle_panel.html
       tags="userdocs,extending"
       datagroups='["userdocs", "extending"]'
       title="Writing rules"
       description="These pages document the process of writing and testing custom rules and metrics for PMD."
    %}


   <!-- {% include custom/shuffle_panel_filler.html %} -->

</div>

<!-- {% include image.html file="pmd-logo-big.png" alt="PMD Logo" %} -->

{% unless site.output == "pdf" %}

{% include initialize_shuffle.html %}<!-- new attempt-->

{% endunless %}



{% include links.html %}
