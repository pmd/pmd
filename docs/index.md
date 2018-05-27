---
title: Documentation Index
keywords: java
permalink: index.html
toc: false
summary: >
    Welcome to the documentation site for PMD and CPD! <br/><br/>


last_updated: August 2017
author: Jeff Jensen <jjensen@apache.org>, Andreas Dangel <andreas.dangel@adangel.org>,
        Clément Fournier <clement.fournier76@gmail.com>
---



{% unless site.output == "pdf" %}
<script src="js/jquery.shuffle.min.js"></script>
<script src="js/jquery.ba-throttle-debounce.min.js"></script>

{% include custom/panel_scroll.html %}
{% endunless %}


## Overview

<!--  You can link to an individual panel, the id is determined from the title of the panel -->
<!--  See custom/shuffle_panel.html for the details -->

**PMD** is a static source code analyzer. It finds common programming flaws like
unused variables, empty catch blocks, unnecessary object creation, and
so forth. It's mainly concerned with **Java and Apex**, but **supports six other
languages**.

PMD features many **built-in checks** (in PMD lingo, *rules*), which are documented
for each language in our [Rule references](#shuffle-panel-rule-references). We
also support an extensive API to [**write your own rules**](#shuffle-panel-writing-rules),
which you can do either in Java or as a self-contained XPath query.

PMD is most useful when **integrated into your build process**. It can then be
used as a quality gate, to enforce a coding standard for your codebase. Among other
things, PMD can be run:
* As a [Maven goal](pmd_userdocs_tools_maven.html)
* As an [Ant task](pmd_userdocs_tools_ant.html)
* As a [Gradle task](https://docs.gradle.org/current/userguide/pmd_plugin.html)
* From [command-line](pmd_userdocs_installation.html#running-pmd-via-command-line)

**CPD**, the **copy-paste detector**, is also distributed with PMD. You can also use it
in a variety of ways, which are [documented here](pmd_userdocs_cpd.html).

## Download

The latest release of PMD can be downloaded from our [Github releases page](https://github.com/pmd/pmd/releases/latest).


## Documentation

The rest of this page exposes the contents of the documentation site thematically,
which you can further scope down using the blue filter buttons. To navigate the site,
you may also use the search bar in the top right, or the sidebar on the left.


<br/>




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
       title="Getting started"
       tags="getting_started"
       datagroups='["getting_started"]'
       description="These pages summarize the gist of PMD usage to get you started quickly." %}


    {% include custom/shuffle_panel.html
       title="Rule references"
       tags="rule_references"
       datagroups='["userdocs"]'
       description="Pick your language to find out about the rule it supports."
       image="fa-database"
       titlemaker="page.language_name" %}

    {% include custom/shuffle_panel.html
       title="Writing rules"
       tags="userdocs,extending"
       datagroups='["userdocs", "extending", "contributing"]'
       description="These pages document the process of writing and testing custom rules and metrics for PMD."
    %}

    {% include custom/shuffle_panel.html
       title="Usage and configuration"
       tags="userdocs"
       except_tags="extending,tools"
       datagroups='["userdocs"]'
       image="fa-cog"
       description="Learn how to build effective and versatile rulesets."
    %}


    {% include custom/shuffle_panel.html
       title="Contributing"
       tags="devdocs"
       except_tags="extending"
       datagroups='["contributing"]'
       image="fa-github"
       description="If you'd like to help us build PMD, these topics may interest you. See you around!"
    %}



    {% include custom/shuffle_panel.html
       title="Tools and integrations"
       tags="tools"
       datagroups='["userdocs"]'
       description="These pages describe solutions that integrate PMD within your build process."
    %}

    {% include custom/shuffle_panel.html
       title="Major contributions"
       tags="devdocs,extending"
       datagroups='["contributing","extending"]'
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
