---
title: Architecture Decisions
sidebar: pmd_sidebar
permalink: pmd_projectdocs_decisions.html
last_updated: October 2022 (6.51.0)
---

<ul>
{% for page in site.pages %}
    {% if page.adr == true and page.adr_status != "" %}
        <li><a href="{{ page.permalink }}">{{ page.title }}</a> ({{ page.adr_status }})</li>
    {% endif %}
{% endfor %}
</ul>
