---
title: Using the Rule Designer
short_title: Using the Rule Designer
tags: [userdocs, extending, designer]
summary: "Learn how to use the designer"
last_updated: July 2018 (6.6.0)
permalink: pmd_userdocs_extending_designer_usage.html
author: Clément Fournier <clement.fournier76@gmail.com>
---

## Getting around

We here explain the global organisation of the UI.

{% include image.html file="userdocs/designer-overview-with-numbers.png" alt="Designer overview" %}

### Examining the AST

The code in zone (1) on the image will be parsed automatically, and its AST rendered
in zone (2). You can examine each node of the AST individually by clicking on it.
Node-specific information will be displayed in zone (3). In that panel, you can view
in different tabs:
* The XPath attributes of the node and their value. These are the attributes accessible
via XPath, but for some languages are also accessible as methods of the node for your Java rules.
* The scope hierarchy of the node, for languages that support it (e.g. Java). This will
be a tree of the scopes that enclose the node and their declarations.
* The value of metrics on the node, for languages that support it (Java and Apex). Note that
metrics can only be computed on a few node types, e.g. class or method declarations.


### XPath tools

The bottom panel has a tab with tools to develop XPath rules. In zone (4), you can edit an
XPath expression that will be automatically evaluated on the current AST. The results will
be displayed in zone (5), and highlighted on the main code area (1).


#### Editing properties

The table in zone (6) allows you to define properties for your XPath rule and change their
values. This is good practice if you want to develop a configurable rule. To add a property,
right-click on the table and select "Add property...". A pop-up will allow you to set the
name, type, and other attributes of the property.

Properties can then be referenced in your XPath query by their name prefixed with a dollar symbol,
e.g. `$myProperty`.

## FAQ


<div class="panel-group" id="accordion">

    {% include custom/faq_entry.html
       question="How do I change the parser language?"
       answer="The toolbar in the center of the UI allows you to select the language of the parser." %}

    {% include custom/faq_entry.html
       question="What's in the Scopes tab?"
       answer="For languages that support it (e.g. Java), this will
               be a tree of the scopes that enclose the node, and their declarations." %}

    {% include custom/faq_entry.html
           question="Why is the Metrics tab always disabled?"
           answer="The Metrics tab is only enabled when the currently selected node
                   supports the computation of metrics. These are for instance class
                   declaration or method declaration nodes." %}

    {% include custom/faq_entry.html
           question="Where are my preferences stored?"
           answer="The Designer saves the current state of the application upon closing
                   into an XML file. This file is located inside your home directory, in
                   the subdirectory `.pmd`" %}

</div>



