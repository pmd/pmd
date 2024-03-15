---
title: Velocity Template Language (VTL) support
permalink: pmd_languages_velocity.html
last_updated: February 2024 (7.0.0)
tags: [languages, PmdCapableLanguage, CpdCapableLanguage]
summary: "VTL-specific features and guidance"
---

> [Velocity](https://velocity.apache.org/engine/devel/vtl-reference.html) is a Java-based template engine.
> It permits web page designers to reference methods defined in Java code.

{% include language_info.html name='Velocity Template Language (VTL)' id='velocity' implementation='velocity::lang.velocity.VtlLanguageModule' supports_pmd=true supports_cpd=true since='5.1.0' %}

{% capture id_change_note %}
The language id of the Velocity module was in PMD 6 just "vm". In PMD 7, this has been changed to "velocity". Also the
package name of the classes has been changed from vm to "velocity". For classes, that used the `Vm` prefix, now `Vtl`
is used as the prefix, e.g. `VtlLanguageModule`, `VtlNode`.
{% endcapture %}
{% include note.html content=id_change_note %}
