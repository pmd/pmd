---
title: 3rd party rulesets
# language_name is used by shuffle_panel in index.md
language_name: 3rd party rulesets
tags: [rule_references, userdocs]
summary: Lists rulesets and rules from the community
permalink: pmd_userdocs_3rdpartyrulesets.html
last_updated: December 2025 (7.20.0)
---

## For Java

* **jPinpoint rules:** PMD rule set for performance aware Java and Kotlin coding.
  * <https://github.com/jborgers/PMD-jPinpoint-rules>
* **arch4u-pmd** is a library with pmd rules that bring new regulations related to known problems in REST API, logging,
  monitoring, etc., including reconfigured default pmd rules to decrease false-positive violations during usage of
  well-known frameworks like Spring, Quarkus, etc.
  * <https://github.com/dgroup/arch4u-pmd>
* Sample ruleset from **maxdocs**, a multi markup wiki engine.
  * <https://github.com/bohni/maxdocs/blob/master/src/main/config/pmd/pmd-ruleset.xml>
* Sample ruleset from **geotools**, an open source Java library that provides tools for geospatial data.
  * <https://github.com/geotools/geotools/blob/main/build/qa/pmd-ruleset.xml>
  * <https://github.com/geotools/geotools/blob/main/build/qa/pmd-junit-ruleset.xml>
* **Alibaba p3c**: Implementation of [Alibaba Java Coding Guidelines](https://alibaba.github.io/Alibaba-Java-Coding-Guidelines)
  as PMD rules: <https://github.com/alibaba/p3c>

## For Apex
* **unhappy-soup**, a repository with problematic Salesforce code to showcase PMD, the SFDX Scanner CLI
  * <https://github.com/rsoesemann/unhappy-soup/blob/master/ruleset.xml>
* **sca-extra**, additional PMD and Regex rules for testing Salesforce Apex code using Salesforce Code Analyzer
  * <https://github.com/starch-uk/sca-extra>
* **test-pmd-tool**, tests PMD XPath rules for coverage using its own examples as unit tests
  * <https://github.com/starch-uk/test-pmd-tool>

