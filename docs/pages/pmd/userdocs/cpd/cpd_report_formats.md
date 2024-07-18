---
title: Report formats for CPD
tags: [cpd, userdocs]
keywords: [formats, renderers]
summary: "Overview of the built-in report formats for CPD"
permalink: pmd_userdocs_cpd_report_formats.html
author: Andreas Dangel <andreas.dangel@pmd-code.org>
last_updated: June 2024 (7.3.0)
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

        validateOverriddenValues(PROPERTY1_DESCRIPTOR, PROPERTY2_DESCRIPTOR, ruleReference);
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
The XML format can then further be processed using XSLT transformations. See [section xslt](#xslt) for examples.

Since PMD 7.3.0 any recoverable errors are also reported as additional elements `error` to help investigate
any errors occurred during analysis.

Example:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<pmd-cpd xmlns="https://pmd-code.org/schema/cpd-report"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         pmdVersion="7.3.0"
         timestamp="2024-06-23T09:00:00+02:00"
         version="1.0.0"
         xsi:schemaLocation="https://pmd-code.org/schema/cpd-report https://pmd.github.io/schema/cpd-report_1_0_0.xsd">
   <file path="/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/RuleReferenceTest.java" totalNumberOfTokens="523"/>
   <file path="/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/lang/rule/xpath/JaxenXPathRuleQueryTest.java" totalNumberOfTokens="120"/>
   <duplication lines="33" tokens="239">
      <file column="29" endcolumn="75" endline="64" line="32" begintoken="2356" endtoken="2594"
            path="/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/RuleReferenceTest.java"/>
      <file column="37" endcolumn="75" endline="100" line="68" begintoken="5700" endtoken="5938"
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

        validateOverriddenValues(PROPERTY1_DESCRIPTOR, PROPERTY2_DESCRIPTOR, ruleReference);]]></codefragment>
   </duplication>
   <duplication lines="16" tokens="110">
      <file column="9" endcolumn="28" endline="81" line="66" begintoken="3000" endtoken="3109"
            path="/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/lang/rule/xpath/JaxenXPathRuleQueryTest.java"/>
      <file column="9" endcolumn="28" endline="103" line="88" begintoken="3200" endtoken="3309"
            path="/home/pmd/source/pmd-core/src/test/java/net/sourceforge/pmd/lang/rule/xpath/JaxenXPathRuleQueryTest.java"/>
      <file column="9" endcolumn="28" endline="125" line="110" begintoken="3400" endtoken="3509"
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
   <error filename="/home/pmd/source/pmd-cli/src/test/resources/net/sourceforge/pmd/cli/cpd/badandgood/BadFile.java"
          msg="LexException: Lexical error in file '/home/pmd/source/pmd-cli/src/test/resources/net/sourceforge/pmd/cli/cpd/badandgood/BadFile.java' at line 4, column 14: &#34;\ufffd&#34; (65533), after : &#34;&#34; (in lexical state DEFAULT)">net.sourceforge.pmd.lang.ast.LexException: Lexical error in file '/home/pmd/source/pmd-cli/src/test/resources/net/sourceforge/pmd/cli/cpd/badandgood/BadFile.java' at line 4, column 14: "\ufffd" (65533), after : "" (in lexical state DEFAULT)
        at net.sourceforge.pmd.lang.ast.InternalApiBridge.newLexException(InternalApiBridge.java:25)
        at net.sourceforge.pmd.lang.java.ast.JavaParserImplTokenManager.getNextToken(JavaParserImplTokenManager.java:2698)
        at net.sourceforge.pmd.lang.java.ast.JavaParserImplTokenManager.getNextToken(JavaParserImplTokenManager.java:18)
        at net.sourceforge.pmd.cpd.impl.BaseTokenFilter.getNextToken(BaseTokenFilter.java:44)
        at net.sourceforge.pmd.cpd.impl.CpdLexerBase.tokenize(CpdLexerBase.java:40)
        at net.sourceforge.pmd.cpd.CpdLexer.tokenize(CpdLexer.java:29)
        at net.sourceforge.pmd.cpd.CpdAnalysis.doTokenize(CpdAnalysis.java:146)
        at net.sourceforge.pmd.cpd.CpdAnalysis.performAnalysis(CpdAnalysis.java:173)
        at net.sourceforge.pmd.cli.commands.internal.CpdCommand.doExecute(CpdCommand.java:134)
        at net.sourceforge.pmd.cli.commands.internal.CpdCommand.doExecute(CpdCommand.java:29)
        at net.sourceforge.pmd.cli.internal.PmdRootLogger.executeInLoggingContext(PmdRootLogger.java:55)
        at net.sourceforge.pmd.cli.commands.internal.AbstractAnalysisPmdSubcommand.execute(AbstractAnalysisPmdSubcommand.java:111)
        at net.sourceforge.pmd.cli.commands.internal.AbstractPmdSubcommand.call(AbstractPmdSubcommand.java:30)
        at net.sourceforge.pmd.cli.commands.internal.AbstractPmdSubcommand.call(AbstractPmdSubcommand.java:16)
        at picocli.CommandLine.executeUserObject(CommandLine.java:2041)
        at picocli.CommandLine.access$1500(CommandLine.java:148)
        at picocli.CommandLine$RunLast.executeUserObjectOfLastSubcommandWithSameParent(CommandLine.java:2461)
        at picocli.CommandLine$RunLast.handle(CommandLine.java:2453)
        at picocli.CommandLine$RunLast.handle(CommandLine.java:2415)
        at picocli.CommandLine$AbstractParseResultHandler.execute(CommandLine.java:2273)
        at picocli.CommandLine$RunLast.execute(CommandLine.java:2417)
        at picocli.CommandLine.execute(CommandLine.java:2170)
        at net.sourceforge.pmd.cli.PmdCli.main(PmdCli.java:24)
   </error>
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

## xslt

This is not a direct report format. But you can use `xml` to generate an XML report and then use one of the following
XSLT stylesheets to convert the report into html. Or you can write your own stylesheet.

You can either use [Ant's XSLT task](https://ant.apache.org/manual/Tasks/style.html) or use any other (CLI) xslt processor,
e.g. `xalan` (see <https://xalan.apache.org/>).

### cpdhtml.xslt

This stylesheet is available in the sources or from GitHub at: <https://raw.githubusercontent.com/pmd/pmd/master/pmd-core/etc/xslt/cpdhtml.xslt>.

```shell
xalan -in cpd-report-sample.xml -xsl cpdhtml.xslt -out cpd-report-sample-cpdhtml.html
```

[Example](report-examples/cpdhtml.html)

This stylesheet by default only consideres duplications longer than 30 lines. You can change the default value with
the param `lines`:

```shell
xalan -in cpd-report-sample.xml -xsl cpdhtml.xslt -out cpd-report-sample-cpdhtml.html -param lines 10
```

### cpdhtml-v2.xslt

This stylesheet is available in the sources or from GitHub at: <https://raw.githubusercontent.com/pmd/pmd/master/pmd-core/etc/xslt/cpdhtml-v2.xslt>.

```shell
xalan -in pmd-core-cpd-report.xml -xsl etc/xslt/cpdhtml-v2.xslt -out pmd-core-cpd-report-v2.html
```

[Example](report-examples/cpdhtml-v2.html)

It requires javascript enabled and uses [Bootstrap](https://getbootstrap.com/),
[jQuery](https://jquery.com/), and [DataTables](https://datatables.net/). 
