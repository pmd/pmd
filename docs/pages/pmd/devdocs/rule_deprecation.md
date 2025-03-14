---
title: Rule deprecation policy
tags: [devdocs]
summary: Describes when and how rules are deprecated
last_updated: November 15, 2019
permalink: pmd_devdocs_rule_deprecation_policy.html
author: Andreas Dangel
---

When improving PMD over time, some rules might become obsolete. This could be because the underlying
technology a specific rule is checking (such as a specific JVM version) is not relevant anymore or a rule
has been replaced by a better implementation.

In order to remove the requirement to maintain such rules forever, these rules can be marked as **deprecated**.
This means, that such rules can entirely be removed in the future.
However, the rules must not be removed immediately, since that would break any (custom) ruleset, that
references this rule.

This policy tries to establish some ground rules about how and when rules are deprecated and removed.
The main goal is, to maintain compatibility of custom rulesets throughout the deprecation process.


## Renaming rules

If a rule is enhanced, it might make sense to also rename the rule, to reflect that enhancement. However,
simply renaming the rule would break existing (custom) ruleset. Therefore the following procedure should be used:

*   Rename the rule to the new name (and also the rule tests and resources)
*   Add a deprecated rule reference with the old name, that points to the new rule name:
    
    `<rule name="OldRuleName" ref="NewRuleName" deprecated="true" />`

*Note:* When referencing the complete rulesets or categories,
these deprecated rule references are ignored, so that the rule is not used twice.

## Moving rules between categories

Every rule is in one category. It might happen, that the focus of the rule shifts and it makes more
sense, if it would be in a different, better fitting category.

*   Move the rule to the new category (and also the rule tests and resources)
*   Add a deprecated rule reference in the old category, that points to the rule in the new category:
    
    `<rule name="MyRule" ref="category/java/errorprone.xml/MyRule" deprecated="true" />`

*Note:* When referencing the complete rulesets or categories,
these deprecated rule references are ignored, so that the rule is not used twice, if both categories
are used.

## Deprecating rules

Before a rule can be removed, it must have been marked as deprecated:

```
<rule name="MyRule" class="...." deprecated="true">
...
</rule>
```

This has the effect, that it is **automatically disabled** if the complete ruleset or category
is referenced. The rule can still be used, if it is referenced directly.

The reasons for the deprecation should be explained in the rule description. If there is a replacement rule
available, then this rule should be mentioned in the description as well.

## Removing rules

Removing rules completely can only be done

*   if the rules have been deprecated before
*   for a new **major** release.

Removing a rule from a ruleset or category will break any custom ruleset, that references
this rule directly. Therefore rules can only be removed with the next major release of PMD.

## Rule property compatibility

Renaming or removing rule properties is not backwards compatible and can only be done
with a major release of PMD.

In order to prepare for the change, properties can be deprecated as well: If the property description
starts with the magic string `deprecated!`, then this property is rendered in the rule documentation
as deprecated. However, there is no automatic check done if such a property is used and no
deprecation warning is issued with the log.

Therefore, the process for **renaming a property** looks like this:

*   Create a new property with the same type and new name
*   Prefix the description of the old property with `deprecated!` and also add a explanation
    either in the property description or in the rule description, which property should be used
    instead of the deprecated property.
*   Adjust the rule implementation to first check the old property. If it has a value other than the
    default value, then the old (deprecated) property has been used and a deprecation warning should
    be logged. If the new property is used (it has a value other than the default), then it takes
    preference, but the deprecation warning for the old property should still be issued.
*   The deprecated property can be removed with the next major release of PMD.


**Changing the default value** of a property might have some results, that make the rule
behavioral incompatible: E.g. it could find many more violations with a different default
configuration and therefore lead to a sudden increase of violations after a PMD upgrade.
It should be judged per case, whether the new default can be considered compatible or not.
If it is not compatible, then the new default value should be configured only with the next
major release of PMD.


