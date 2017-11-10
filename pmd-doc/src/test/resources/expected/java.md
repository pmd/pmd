---
title: Java Rules
permalink: pmd_rules_java.html
folder: pmd/rules
---
## Sample

{% include callout.html content="Sample ruleset to test rule doc generation." %}

*   [DeprecatedSample](pmd_rules_java_sample.html#deprecatedsample): <span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f; font-size: 75%;">Deprecated</span> Just some description of a deprecated rule.
*   [JumbledIncrementer](pmd_rules_java_sample.html#jumbledincrementer): Avoid jumbled loop incrementers - its usually a mistake, and is confusing even if intentional.
*   [MovedRule](pmd_rules_java_sample.html#movedrule): <span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f; font-size: 75%;">Deprecated</span> The rule has been moved to another ruleset. Use instead [JumbledIncrementer](pmd_rules_java_sample2.html#jumbledincrementer).
*   [OverrideBothEqualsAndHashcode](pmd_rules_java_sample.html#overridebothequalsandhashcode): Override both 'public boolean Object.equals(Object other)', and 'public int Object.hashCode()', o...
*   [RenamedRule](pmd_rules_java_sample.html#renamedrule): <span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f; font-size: 75%;">Deprecated</span> The rule has been renamed. Use instead [JumbledIncrementer](pmd_rules_java_sample.html#jumbledincrementer).

## Additional rulesets

*   Other ruleset (`rulesets/ruledoctest/other-ruleset.xml`):

    Ruleset which serves a specific use case, such as Getting Started.

    It contains the following rules:

    [JumbledIncrementer](pmd_rules_java_sample.html#jumbledincrementer), [OverrideBothEqualsAndHashcode](pmd_rules_java_sample.html#overridebothequalsandhashcode)

*   Sample Deprecated (`rulesets/ruledoctest/sample-deprecated.xml`):

    <span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f; font-size: 75%;">Deprecated</span>  This ruleset is for backwards compatibility.

    It contains the following rules:

    [JumbledIncrementer](pmd_rules_java_sample.html#jumbledincrementer), [OverrideBothEqualsAndHashcode](pmd_rules_java_sample.html#overridebothequalsandhashcode)

