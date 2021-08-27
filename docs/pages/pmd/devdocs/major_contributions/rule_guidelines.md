---
title: Guidelines for standard rules
short_title: Rule guidelines
tags: [devdocs, extending]
summary: "Guidelines for rules that are included in the standard distribution"
last_updated: August, 2021
sidebar: pmd_sidebar
permalink: pmd_devdocs_major_rule_guidelines.html
---

{% include note.html content="
These guidelines are new and most rules don't follow these guidelines yet.
The goal is, that eventually all rules are updated.
" %}

## Why do we need these guidelines?

*   To prevent low quality contributions
*   To reduce time reviewing rules

They just apply to rules included in the standard distribution.

## Requirements for standard rules

To be included in stock PMD, a rule needs

*   Broad applicability. It may be specific to a framework, but then, this framework should be widely used
*   Solid documentation. See below
*   If it's a performance rule: solid benchmarks. No micro-optimization rules
*   No overlap with other rules

## Dos/Don'ts (rule rules)

*   Rule naming
    *   **Don't** put the implementation of the rule in the name, because it will be awkward
        if the scope of the rule changes 
        *   Eg. *SwitchStmtShouldHaveDefault* -> since enums are a thing they don't necessarily
            need to have a default anymore, they should be exhaustive. So the rule name lies now...
        *   Eg. *MissingBreakInSwitch* -> it's obvious that this is supposed to find fall-through
            switches. Counting breaks is not a clever way to do it, but since it's in the name
            we can't change it without renaming the rule.
    *   **Do** use rule names that name the underlying problem that violations exhibit
        *   Eg. instead of *SwitchStmtShouldHaveDefault*, use *NonExhaustiveSwitchStatement* -> this
            is the problem, the description of the rule will clarify why it is a problem and how
            to fix it (add a default, or add branches, or something else in the future)
        *   Eg. instead of *MissingBreakInSwitch*, use *SwitchCaseFallsThrough*
    *   **Don't** create several rules for instances of the same problem
        *   *EmptyIfStmt* and *EmptyWhileStmt* are actually the same problem, namely,
            that there's useless syntax in the tree.
    *   **Don't** limit the rule name to strictly what the rule can do today
        *   Eg. *UnusedPrivateField* is a bad name. The problem is that there is an unused field,
            not that it is private as well. If we had the ability to find unused package-private
            fields, we would report them too. So if one day we get that ability,
            using a name like *UnusedField* would allow us to keep the name.
*   Rule messages
    *   **Do** write rule messages that neutrally point out a problem or construct that should
        be reviewed ("Unnecessary parentheses")
    *   **Don't** write rule messages that give an order ("Avoid unnecessary parentheses")
        especially without explaining why, like here
    *   **Don't** write rule messages that are tautological ("Unnecessary parentheses should be removed").
        The answer to this would be an annoyed "yes I know, so what?".
*   **Do** use Markdown in rule descriptions and break lines at a reasonable 80 chars
*   **Do** thoroughly comment rule examples. It must be obvious where to look
*   **Do** comment your xpath expressions too

## Rule description template

*   What the rule reports (1 summary line)
*   Why the rule exists and where it might be useful (including, since which language version, etc)
*   Blank line
*   Explain all assumptions that the rule makes and keywords used in the previous paragraph.
    ("overridden methods are ignored", "for the purposes of this rule, a 'visible' field is
    non-private").
*   Describe known limitations if any
*   Blank line
*   For each property, explain how it modifies the assumptions and why you would want to use it.
    **If you can't explain why it's there then it shouldn’t be there!**
