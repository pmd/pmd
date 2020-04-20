---
title: Report formats for CPD
tags: [cpd, userdocs]
keywords: [formats, renderers]
summary: "Overview of the built-in report formats for CPD"
permalink: pmd_userdocs_cpd_report_formats.html
author: Andreas Dangel <andreas.dangel@pmd-code.org>
---

## Overview

CPD collects occurrences of found duplications and provides them to the selected report format.
Each found code duplication appears in one or more other files, so that each code duplication can
have multiple locations. Not all report formats display all locations.

The following examples always describe the same duplications:

1. a code block of 239 tokens spanning 33 lines in RuleReferenceTest. This is a duplication within the same file.
2. a code block of 110 tokens spanning 16 lines in JaxenXPathRuleQueryTest. This is a duplication that appears
   3 times within the same file.


## text

This is the default format.

All duplications are reported one after another. For each duplication, the complete code snippet is output.
Each duplication is separated by `======`.

Example:

```
Found a 33 line (239 tokens) duplication in the following files: 
Starting at line 32 of /home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/RuleReferenceTest.java
Starting at line 68 of /home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/RuleReferenceTest.java

    public void testOverride() {
        final StringProperty PROPERTY1_DESCRIPTOR = new StringProperty("property1", "Test property", null, 0f);
        MockRule rule = new MockRule();
        rule.definePropertyDescriptor(PROPERTY1_DESCRIPTOR);
        rule.setLanguage(LanguageRegistry.getLanguage(Dummy2LanguageModule.NAME));
        rule.setName("name1");
        rule.setProperty(PROPERTY1_DESCRIPTOR, "value1");
        rule.setMessage("message1");
        rule.setDescription("description1");
        rule.addExample("example1");
        rule.setExternalInfoUrl("externalInfoUrl1");
        rule.setPriority(RulePriority.HIGH);

        final StringProperty PROPERTY2_DESCRIPTOR = new StringProperty("property2", "Test property", null, 0f);
        RuleReference ruleReference = new RuleReference();
        ruleReference.setRule(rule);
        ruleReference.definePropertyDescriptor(PROPERTY2_DESCRIPTOR);
        ruleReference.setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        ruleReference
                .setMinimumLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.3"));
        ruleReference
                .setMaximumLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.7"));
        ruleReference.setDeprecated(true);
        ruleReference.setName("name2");
        ruleReference.setProperty(PROPERTY1_DESCRIPTOR, "value2");
        ruleReference.setProperty(PROPERTY2_DESCRIPTOR, "value3");
        ruleReference.setMessage("message2");
        ruleReference.setDescription("description2");
        ruleReference.addExample("example2");
        ruleReference.setExternalInfoUrl("externalInfoUrl2");
        ruleReference.setPriority(RulePriority.MEDIUM_HIGH);

        validateOverridenValues(PROPERTY1_DESCRIPTOR, PROPERTY2_DESCRIPTOR, ruleReference);
=====================================================================
Found a 16 line (110 tokens) duplication in the following files: 
Starting at line 66 of /home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/lang/rule/xpath/JaxenXPathRuleQueryTest.java
Starting at line 88 of /home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/lang/rule/xpath/JaxenXPathRuleQueryTest.java
Starting at line 110 of /home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/lang/rule/xpath/JaxenXPathRuleQueryTest.java

        JaxenXPathRuleQuery query = createQuery(xpath);
        List<String> ruleChainVisits = query.getRuleChainVisits();
        Assert.assertEquals(2, ruleChainVisits.size());
        Assert.assertTrue(ruleChainVisits.contains("dummyNode"));
        // Note: Having AST_ROOT in the rule chain visits is probably a mistake. But it doesn't hurt, it shouldn't
        // match a real node name.
        Assert.assertTrue(ruleChainVisits.contains(JaxenXPathRuleQuery.AST_ROOT));

        DummyNodeWithListAndEnum dummy = new DummyNodeWithListAndEnum(1);
        RuleContext data = new RuleContext();
        data.setLanguageVersion(LanguageRegistry.findLanguageByTerseName("dummy").getDefaultVersion());

        query.evaluate(dummy, data);
        // note: the actual xpath queries are only available after evaluating
        Assert.assertEquals(2, query.nodeNameToXPaths.size());
        Assert.assertEquals("self::node()[(attribute::Test1 = \"false\")][(attribute::Test2 = \"true\")]", query.nodeNameToXPaths.get("dummyNode").get(0).toString());
```


## xml

This format uses XML to output the duplications in a more structured format.

Example:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<pmd-cpd>
   <duplication lines="33" tokens="239">
      <file column="29" endcolumn="75" endline="64" line="32"
            path="/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/RuleReferenceTest.java"/>
      <file column="37" endcolumn="75" endline="100" line="68"
            path="/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/RuleReferenceTest.java"/>
      <codefragment><![CDATA[    public void testOverride() {
        final StringProperty PROPERTY1_DESCRIPTOR = new StringProperty("property1", "Test property", null, 0f);
        MockRule rule = new MockRule();
        rule.definePropertyDescriptor(PROPERTY1_DESCRIPTOR);
        rule.setLanguage(LanguageRegistry.getLanguage(Dummy2LanguageModule.NAME));
        rule.setName("name1");
        rule.setProperty(PROPERTY1_DESCRIPTOR, "value1");
        rule.setMessage("message1");
        rule.setDescription("description1");
        rule.addExample("example1");
        rule.setExternalInfoUrl("externalInfoUrl1");
        rule.setPriority(RulePriority.HIGH);

        final StringProperty PROPERTY2_DESCRIPTOR = new StringProperty("property2", "Test property", null, 0f);
        RuleReference ruleReference = new RuleReference();
        ruleReference.setRule(rule);
        ruleReference.definePropertyDescriptor(PROPERTY2_DESCRIPTOR);
        ruleReference.setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        ruleReference
                .setMinimumLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.3"));
        ruleReference
                .setMaximumLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.7"));
        ruleReference.setDeprecated(true);
        ruleReference.setName("name2");
        ruleReference.setProperty(PROPERTY1_DESCRIPTOR, "value2");
        ruleReference.setProperty(PROPERTY2_DESCRIPTOR, "value3");
        ruleReference.setMessage("message2");
        ruleReference.setDescription("description2");
        ruleReference.addExample("example2");
        ruleReference.setExternalInfoUrl("externalInfoUrl2");
        ruleReference.setPriority(RulePriority.MEDIUM_HIGH);

        validateOverridenValues(PROPERTY1_DESCRIPTOR, PROPERTY2_DESCRIPTOR, ruleReference);]]></codefragment>
   </duplication>
   <duplication lines="16" tokens="110">
      <file column="9" endcolumn="28" endline="81" line="66"
            path="/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/lang/rule/xpath/JaxenXPathRuleQueryTest.java"/>
      <file column="9" endcolumn="28" endline="103" line="88"
            path="/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/lang/rule/xpath/JaxenXPathRuleQueryTest.java"/>
      <file column="9" endcolumn="28" endline="125" line="110"
            path="/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/lang/rule/xpath/JaxenXPathRuleQueryTest.java"/>
      <codefragment><![CDATA[        JaxenXPathRuleQuery query = createQuery(xpath);
        List<String> ruleChainVisits = query.getRuleChainVisits();
        Assert.assertEquals(2, ruleChainVisits.size());
        Assert.assertTrue(ruleChainVisits.contains("dummyNode"));
        // Note: Having AST_ROOT in the rule chain visits is probably a mistake. But it doesn't hurt, it shouldn't
        // match a real node name.
        Assert.assertTrue(ruleChainVisits.contains(JaxenXPathRuleQuery.AST_ROOT));

        DummyNodeWithListAndEnum dummy = new DummyNodeWithListAndEnum(1);
        RuleContext data = new RuleContext();
        data.setLanguageVersion(LanguageRegistry.findLanguageByTerseName("dummy").getDefaultVersion());

        query.evaluate(dummy, data);
        // note: the actual xpath queries are only available after evaluating
        Assert.assertEquals(2, query.nodeNameToXPaths.size());
        Assert.assertEquals("self::node()[(attribute::Test1 = \"false\")][(attribute::Test2 = \"true\")]", query.nodeNameToXPaths.get("dummyNode").get(0).toString());]]></codefragment>
   </duplication>
</pmd-cpd>
```


## csv

This outputs the duplication as comma separated values. It only reports the duplication size (number
of lines and tokens) and the number of occurrences. After that, the begin lines and filenames are reported on
after another.

Example:

```
lines,tokens,occurrences
33,239,2,32,/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/RuleReferenceTest.java,68,/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/RuleReferenceTest.java
16,110,3,66,/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/lang/rule/xpath/JaxenXPathRuleQueryTest.java,88,/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/lang/rule/xpath/JaxenXPathRuleQueryTest.java,110,/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/lang/rule/xpath/JaxenXPathRuleQueryTest.java
```


## csv_with_linecount_per_file

This format is similar to "csv", but it has one difference: The duplication size in number of lines is reported
for each occurrence separately. While the tokens are the same, due to formatting or comments, the code blocks might be
different. Whitespace and comments are usually ignored when finding duplicated code.

In each line, the duplication size in tokens is reported, then the number of occurrences. And after that, for each
file, the begin line, the number of duplicated lines and the filename.

Example:

```
tokens,occurrences
239,2,32,33,/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/RuleReferenceTest.java,68,33,/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/RuleReferenc
eTest.java
110,3,66,16,/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/lang/rule/xpath/JaxenXPathRuleQueryTest.java,88,16,/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/lang/rule/xpath/JaxenXPathRuleQueryTest.java,110,16,/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/lang/rule/xpath/JaxenXPathRuleQueryTest.java
```


## vs

This outputs the duplication in a format, that Visual Studio. CPD can be added as a external tool and the output
is shown in the console. You can then click on the filenames to jump to the source where the duplication is located.

Each occurrence of a duplication is reported in a separate line, that's why in this example, we have 5 lines.

Example:

```
/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/RuleReferenceTest.java(32): Between lines 32 and 65
/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/RuleReferenceTest.java(68): Between lines 68 and 101
/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/lang/rule/xpath/JaxenXPathRuleQueryTest.java(66): Between lines 66 and 82
/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/lang/rule/xpath/JaxenXPathRuleQueryTest.java(88): Between lines 88 and 104
/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/lang/rule/xpath/JaxenXPathRuleQueryTest.java(110): Between lines 110 and 126
```
