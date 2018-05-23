---
title: PMD Documentation Index
keywords: java
permalink: index.html
toc: false
summary: >
    Welcome to the documentation index for PMD and CPD! This page exposes
    the contents of the documentation site thematically, which you can further
    scope down using the blue filter buttons. To navigate the site, you may also use
    the search bar in the top right, or the sidebar on the left.
last_updated: August 2017
author: Jeff Jensen <jjensen@apache.org>, Andreas Dangel <andreas.dangel@adangel.org>,
        Clément Fournier <clement.fournier76@gmail.com>
---



<br/>


{% unless site.output == "pdf" %}
<script src="js/jquery.shuffle.min.js"></script>
<script src="js/jquery.ba-throttle-debounce.min.js"></script>
{% endunless %}



<div class="filter-options" id='grid-rule' >
      <button class="btn btn-primary" data-group="all">All</button>
      <button class="btn btn-primary" data-group="getting_started">Getting Started</button>
      <button class="btn btn-primary" data-group="userdocs">User documentation</button>
      <button class="btn btn-primary" data-group="extending">Extending PMD</button>
      <button class="btn btn-primary" data-group="contributing">Contributing</button>
</div>



<div class="container-fluid" >
<div id="grid" class="row">

<!--  TODO the "getting started" panel is not that useful. It would be better to make a page series. -->
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
       datagroups='["userdocs", "extending", "contributing"]'
       title="Writing rules"
       description="These pages document the process of writing and testing custom rules and metrics for PMD."
    %}

    {% include custom/shuffle_panel.html
       tags="userdocs"
       except_tags="extending,tools"
       datagroups='["userdocs"]'
       image="fa-cog"
       title="Usage and configuration"
       description="Learn how to build effective and versatile rulesets."
    %}


    {% include custom/shuffle_panel.html
       tags="devdocs"
       except_tags="extending"
       datagroups='["contributing"]'
       image="fa-github"
       title="Contributing"
       description="If you'd like to help us build PMD, these topics may interest you. See you around!"
    %}



    {% include custom/shuffle_panel.html
       tags="tools"
       datagroups='["userdocs"]'
       title="Tools and integrations"
       description="These pages describe solutions that integrate PMD within your build process."
    %}

    {% include custom/shuffle_panel.html
       tags="devdocs,extending"
       datagroups='["contributing","extending"]'
       title="Major contributions"
       description=""
    %}


<!-- sizer -->
<div class="col-xs-6 col-sm-4 col-md-1 shuffle_sizer"></div>

</div>
</div>

<!-- {% include image.html file="pmd-logo-big.png" alt="PMD Logo" %} -->

{% unless site.output == "pdf" %}

{% include initialize_shuffle.html %}

{% endunless %}



{% include links.html %}
