---
title: PMD Documentation website
keywords: java
tags: []
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

First time user? Then you may be interested in our [quickstart page](TODO).



<div class="row">
         <div class="col-lg-12">
             <h2 class="page-header">Quick access</h2>
         </div>
         <div class="col-md-3 col-sm-6">
             <div class="panel panel-default text-center">
                 <div class="panel-heading">
                     <span class="fa-stack fa-5x">
                           <i class="fa fa-circle fa-stack-2x text-primary"></i>
                           <i class="fa fa-paper-plane fa-stack-1x fa-inverse"></i>
                     </span>
                 </div>
                 <div class="panel-body">
                     <h4>Getting started</h4>
                     <p>A collection of pages explaining the gist of PMD <b>usage</b> and <b>ruleset making</b>.</p>
                     <a href="tag_getting_started.html" class="btn btn-primary">Learn More</a>
                 </div>
             </div>
         </div>
         <div class="col-md-3 col-sm-6">
             <div class="panel panel-default text-center">
                 <div class="panel-heading">
                     <span class="fa-stack fa-5x">
                           <i class="fa fa-circle fa-stack-2x text-primary"></i>
                           <i class="fa fa-expand fa-stack-1x fa-inverse"></i>
                     </span>
                 </div>
                 <div class="panel-body">
                     <h4>Extending PMD</h4>
                     <p>Guides about <b>writing rules</b>, metrics, and testing them properly.</p>
                     <a href="tag_navigation.html" class="btn btn-primary">Learn More</a>
                 </div>
             </div>
         </div>
         <div class="col-md-3 col-sm-6">
             <div class="panel panel-default text-center">
                 <div class="panel-heading">
                     <span class="fa-stack fa-5x">
                           <i class="fa fa-circle fa-stack-2x text-primary"></i>
                           <i class="fa fa-list fa-stack-1x fa-inverse"></i>
                     </span>
                 </div>
                 <div class="panel-body">
                     <h4>Rule reference</h4>
                     <p>Find an <b>existing rule</b> to craft your own rulesets.</p>
                     <a href="tag_single_sourcing.html" class="btn btn-primary">Learn More</a>
                 </div>
             </div>
         </div>
         <div class="col-md-3 col-sm-6">
             <div class="panel panel-default text-center">
                 <div class="panel-heading">
                     <span class="fa-stack fa-5x">
                           <i class="fa fa-circle fa-stack-2x text-primary"></i>
                           <i class="fa fa-code-fork fa-stack-1x fa-inverse"></i>
                     </span>
                 </div>
                 <div class="panel-body">
                     <h4>Contributing</h4>
                     <p>Help us make PMD better!</p>
                     <a href="tag_formatting.html" class="btn btn-primary">Learn More</a>
                 </div>
             </div>
         </div>
</div>


# More details


{% include custom/shuffle_panel.html
   tag="getting_started"
   title="Getting started" %}


{% include custom/shuffle_panel.html
   tag="rule_references"
   title="Rule references"
   description="Pick your language to find out about the rule it supports:"
   image="fa-database"
   titlemaker="page.language_name" %}

{% include custom/shuffle_panel_filler.html %}




<!-- {% include image.html file="pmd-logo-big.png" alt="PMD Logo" %} -->

{% include links.html %}
