---
title: Adding PMD support for a new JavaCC grammar based language
short_title: Adding a new language with JavaCC
tags: [devdocs, extending]
summary: "How to add a new language to PMD using JavaCC grammar."
last_updated: February 2023 (7.0.0)
sidebar: pmd_sidebar
permalink: pmd_devdocs_major_adding_new_language_javacc.html
folder: pmd/devdocs
---

{% include callout.html type="warning" content="

**Before you start...**<br><br>

This is really a big contribution and can't be done with a drive by contribution. It requires dedicated passion
and long commitment to implement support for a new language.<br><br>

This step-by-step guide is just a small intro to get the basics started and it's also not necessarily up-to-date
or complete and you have to be able to fill in the blanks.<br><br>

After the basic support for a language is there, there are lots of missing features left. Typical features
that can greatly improve rule writing are: symbol table, type resolution, call/data flow analysis.<br><br>

Symbol table keeps track of variables and their usages. Type resolution tries to find the actual class type
of each used type, following along method calls (including overloaded and overwritten methods), allowing
to query sub types and type hierarchy. This requires additional configuration of an auxiliary classpath.
Call and data flow analysis keep track of the data as it is moving through different execution paths
a program has.<br><br>

These features are out of scope of this guide. Type resolution and data flow are features that
definitely don't come for free. It is much effort and requires perseverance to implement.<br><br>

" %}


## 1.  Start with a new sub-module
*    See pmd-java or pmd-vm for examples.

## 2.  Implement an AST parser for your language
*   Ideally an AST parser should be implemented as a JJT file *(see VmParser.jjt or Java.jjt for example)*
*   There is nothing preventing any other parser implementation, as long as you have some way to convert an input
    stream into an AST tree. Doing it as a JJT simplifies maintenance down the road.
*   See this link for reference: [https://javacc.java.net/doc/JJTree.html](https://javacc.java.net/doc/JJTree.html)

## 3.  Create AST node classes
*   For each AST node that your parser can generate, there should be a class
*   The name of the AST class should be “AST” + “whatever is the name of the node in JJT file”.
    *   For example, if JJT contains a node called “IfStatement”, there should be a class called “ASTIfStatement”
*   Each AST class should have one package-private constructor, that takes an `int id`.
*   It’s a good idea to create a parent AST class for all AST classes of the language. This simplifies rule
    creation later. *(see SimpleNode for Velocity and AbstractJavaNode for Java for example)*
*   Note: These AST node classes are generated usually once by javacc/jjtree and can then be modified as needed.

## 4.  Generate your parser (using JJT)
*   An ant script is being used to compile jjt files into classes. This is in `javacc-wrapper.xml` file in the
    top-level pmd sources.
*   The ant script is executed via the `maven-antrun-plugin`. Add this plugin to your `pom.xml` file and configure
    it the language name. You can use `pmd-java/pom.xml` as an example.
*   The ant script is called in the phase `generate-sources` whenever the whole project is built. But you can
    call `./mvnw generate-sources` directly for your module if you want your parser to be generated.

## 5.  Create a PMD parser “adapter”
*   Create a new class that extends `JjtreeParserAdapter`.
*   This is a generic class, and you need to declare the root AST node.
*   There are two important methods to implement
    *   `tokenBehavior` method should return a new instance of `TokenDocumentBehavior` constructed with the list
        of tokes in your language. The compile step #4 will generate a class `$langTokenKinds` which has
        all the available tokens in the field `TOKEN_NAMES`.
    *   `parseImpl` method should return the root node of the AST tree obtained by parsing the CharStream source
    *   See `VmParser` class as an example

## 6.  Create a language version handler
*   Extend `AbstractPmdLanguageVersionHandler` *(see VmHandler for example)*
*   This class is sort of a gateway between PMD and all parsing logic specific to your language.
*   For a minimal implementation, it just needs to return a parser *(see step #5)*.
*   It can be used to provide other features for your language like
    *   violation suppression logic
    *   violation decorators, to add additional language specific information to the created violations
    *   metrics (see below "Optional features")
    *   custom XPath functions
*   See `VmHandler` class as an example

## 7.  Create a base visitor
*   A parser visitor adapter is not needed anymore with PMD 7. The visitor interface now provides a default
    implementation.
*   The visitor for JavaCC based AST is generated along the parser from the grammar file. The
    base interface for a visitor is [`AstVisitor`](https://github.com/pmd/pmd/blob/pmd/7.0.x/pmd-core/src/main/java/net/sourceforge/pmd/lang/ast/AstVisitor.java).
*   The generated visitor class for VM is called `VmVisitor`.
*   In order to help use this visitor later on, a base visitor class should be created.
    See `VmVisitorBase` as an example.

## 8. Make PMD recognize your language
*   Create your own subclass of `net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase`. *(see VmLanguageModule or
    JavaLanguageModule as an example)*
*   Add for each version of your language a call to `addVersion` in your language module’s constructor.
    Use `addDefaultVersion` for defining the default version.
*   You’ll need to refer the language version handler created in step #6.
*   Create the service registration via the text file `src/main/resources/META-INF/services/net.sourceforge.pmd.lang.Language`.
    Add your fully qualified class name as a single line into it.

## 9. Add AST regression tests

For languages, that use an external library for parsing, the AST can easily change when upgrading the library.
Also for languages, where we have the grammar under our control, it is useful to have such tests.

The tests parse one or more source files and generate a textual representation of the AST. This text is compared
against a previously recorded version. If there are differences, the test fails.

This helps to detect anything in the AST structure that changed, maybe unexpectedly.

*   Create a test class in the package `net.sourceforge.pmd.lang.$lang.ast` with the name `$langTreeDumpTest`.
*   This test class must extend `net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest`. Note: This class
    is written in kotlin and is available in the module "lang-test".
*   Add a default constructor, that calls the super constructor like so:
    
    ```java
        public $langTreeDumpTest() {
            super(NodePrintersKt.getSimpleNodePrinter(), ".$extension");
        }
    ```
    
    Replace "$lang" and "$extension" accordingly.
*   Implement the method `getParser()`. It must return a
    subclass of `net.sourceforge.pmd.lang.ast.test.BaseParsingHelper`. See 
    `net.sourceforge.pmd.lang.ecmascript.ast.JsParsingHelper` for an example.
    With this parser helper you can also specify, where the test files are searched, by using
    the method `withResourceContext(Class<?>, String)`.
*   Add one or more test methods. Each test method parses one file and compares the result. The base
    class has a helper method `doTest(String)` that does all the work. This method just needs to be called:
    
    ```java
        @Test
        public void myFirstAstTest() {
            doTest("filename-without-extension");
        }
    ```
*   On the first test run the test fails. A text file (with the extension `.txt`) is created, that records the
    current AST. On the next run, the text file is used as comparison and the test should pass. Don't forget
    to commit the generated text file.

A complete example can be seen in the JavaScript module: `net.sourceforge.pmd.lang.ecmascript.ast.JsTreeDumpTest`.
The test resources are in the subpackage "testdata": `pmd-javascript/src/test/resources/net/sourceforge/pmd/lang/ecmascript/ast/testdata/`.

The Scala module also has a test, written in Kotlin instead of Java:
`net.sourceforge.pmd.lang.scala.ast.ScalaParserTests`.


## 10. Create an abstract rule class for the language
*   Extend `AbstractRule` and implement the parser visitor interface for your language *(see AbstractVmRule for example)*
*   All other rules for your language should extend this class. The purpose of this class is to implement visit
    methods for all AST types to simply delegate to default behavior. This is useful because most rules care only
    about specific AST nodes, but PMD needs to know what to do with each node - so this just lets you use default
    behavior for nodes you don’t care about.

## 11. Create rules
*   Rules are created by extending the abstract rule class created in step 9 *(see `EmptyForeachStmtRule` for example)*
*   Creating rules is already pretty well documented in PMD - and it’s no different for a new language,
    except you may have different AST nodes.

## 12. Test the rules
*   Testing rules is described in depth in [Testing your rules](pmd_userdocs_extending_testing.html).
    *   Each rule has its own test class: Create a test class for your rule extending `PmdRuleTst`
        *(see AvoidReassigningParametersTest in pmd-vm for example)*
    *   Create a category rule set for your language *(see category/vm/bestpractices.xml for example)*
    *   Place the test XML file with the test cases in the correct location
    *   When executing the test class
        *   this triggers the unit test to read the corresponding XML file with the rule test data
            *(see `AvoidReassigningParameters.xml` for example)*
        *   This test XML file contains sample pieces of code which should trigger a specified number of
            violations of this rule. The unit test will execute the rule on this piece of code, and verify
            that the number of violations matches.
*   To verify the validity of the created ruleset, create a subclass of `AbstractRuleSetFactoryTest`
    (*see `RuleSetFactoryTest` in pmd-vm for example)*.
    This will load all rulesets and verify, that all required attributes are provided.

    *Note:* You'll need to add your category ruleset to `categories.properties`, so that it can be found.

## Debugging with Rule Designer

When implementing your grammar it may be very useful to see how PMD parses your example files.
This can be achieved with Rule Designer:
*   Override the `getXPathNodeName` in your AST nodes for Designer to show node names.
*   Make sure to override both `jjtOpen` and `jjtClose` in your AST node base class so that they set both start and end line and column for proper node bound highlighting.
*   _Not strictly required but trivial and useful:_ implement syntax highlighting for Rule Designer:
    *   Fork and clone the [pmd/pmd-designer](https://github.com/pmd/pmd-designer) repository.
    *   Add a syntax highlighter implementation to `net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting` (you could use Java as an example).
    *   Register it in the `AvailableSyntaxHighlighters` enumeration.
    *   Now build your implementation and place the `target/pmd-ui-<version>-SNAPSHOT.jar` to the `lib` directory inside your `pmd-bin-...` distribution (you have to delete old `pmd-ui-*.jar` from there).

# Optional features

## Metrics

If you want to add support for computing metrics:
* Create a package `lang.<langname>.metrics`
* Create a utility class `<langname>Metrics`
* Implement new metrics and add them as static constants. Be sure to document them.
* Implement {% jdoc core::lang.LanguageVersionHandler#getLanguageMetricsProvider() %}, to make the metrics available in the designer.

See {% jdoc java::lang.java.metrics.JavaMetrics %} for an example.
