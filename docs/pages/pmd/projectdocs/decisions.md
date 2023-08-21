---
title: Architecture Decisions
sidebar: pmd_sidebar
permalink: pmd_projectdocs_decisions.html
last_updated: July 2022
---

<ul>
{% for page in site.pages %}
    {% if page.adr == true and page.adr_status != "" %}
        <li><a href="{{ page.permalink }}">{{ page.title }}</a> ({{ page.adr_status }})</li>
    {% endif %}
{% endfor %}
</ul>
