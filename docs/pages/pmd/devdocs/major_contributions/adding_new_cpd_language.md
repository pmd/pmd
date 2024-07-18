---
title: How to add a new CPD language
short_title: Adding a new CPD language
tags: [devdocs, extending]
summary: How to add a new language module with CPD support.
last_updated: June 2024 (7.3.0)
permalink: pmd_devdocs_major_adding_new_cpd_language.html
author: Matías Fraga, Clément Fournier
---

## Adding support for a CPD language

CPD works generically on the tokens produced by a {% jdoc core::cpd.CpdLexer %}.
To add support for a new language, the crucial piece is writing a CpdLexer that
splits the source file into the tokens specific to your language. Thankfully you
can use a stock [Antlr grammar](https://github.com/antlr/grammars-v4) or JavaCC
grammar to generate a lexer for you. If you cannot use a lexer generator, for
instance because you are wrapping a lexer from another library, it is still relatively
easy to implement the Tokenizer interface.

Use the following guide to set up a new language module that supports CPD.

1. Create a new Maven module for your language. You can take [the Golang module](https://github.com/pmd/pmd/tree/master/pmd-go/pom.xml) as an example.
   - Make sure to add your new module to the parent pom as `<module>` entry, so that it is built alongside the
     other languages.
   - Also add your new module to the dependencies list in "pmd-languages-deps/pom.xml", so that the new language
     is automatically available in the binary distribution (pmd-dist).

2. Implement a {% jdoc core::cpd.CpdLexer %}.
    - For Antlr grammars you can take the grammar from [antlr/grammars-v4](https://github.com/antlr/grammars-v4) and place it in `src/main/antlr4` followed by the package name of the language. You then need to call the appropriate ant wrapper to generate
    the lexer from the grammar. To do so, edit `pom.xml` (eg like [the Golang module](https://github.com/pmd/pmd/tree/master/pmd-go/pom.xml)).
      Once that is done, `mvn generate-sources` should generate the lexer sources for you.

      You can now implement a CpdLexer, for instance by extending {% jdoc core::cpd.impl.AntlrCpdLexer %}. The following reproduces the Go implementation:
    ```java
    // mind the package convention if you are going to make a PR
    package net.sourceforge.pmd.lang.go.cpd;

    public class GoCpdLexer extends AntlrCpdLexer {

        @Override
        protected Lexer getLexerForSource(CharStream charStream) {
            return new GolangLexer(charStream);
        }
    }
    ```
    
    - If your language is case-insensitive, then you might want to overwrite `getImage(AntlrToken)`. There you can
      change each token e.g. into uppercase, so that CPD sees the same strings and can find duplicates even when
      the casing differs. See {% jdoc tsql::lang.tsql.cpd.TSqlCpdLexer %} for an example. You will also need a
      "CaseChangingCharStream", so that antlr itself is case-insensitive.
    - For JavaCC grammars, place your grammar in `etc/grammar` and edit the `pom.xml` like the [Python implementation](https://github.com/pmd/pmd/blob/master/pmd-python/pom.xml) does.
      You can then subclass {% jdoc core::cpd.impl.JavaccCpdLexer %} instead of AntlrCpdLexer.
    - If your JavaCC based language is case-insensitive (option `IGNORE_CASE=true`), then you need to implement
      {%jdoc core::lang.ast.impl.javacc.JavaccTokenDocument.TokenDocumentBehavior %}, which can change each token
      e.g. into uppercase. See {%jdoc plsql::lang.plsql.ast.PLSQLParser %} for an example.
    - For any other scenario just implement the interface however you can. Look at the Scala or Apex module for existing implementations.

3. Create a {% jdoc core::lang.Language %} implementation, and make it implement {% jdoc core::cpd.CpdCapableLanguage %}.
If your language only supports CPD, then you can subclass {% jdoc core::lang.impl.CpdOnlyLanguageModuleBase %} to get going:
    
    ```java
    // mind the package convention if you are going to make a PR
    package net.sourceforge.pmd.lang.go;

    public class GoLanguageModule extends CpdOnlyLanguageModuleBase {
        
        // A public noarg constructor is required.
        public GoLanguageModule() {
            super(LanguageMetadata.withId("go").name("Go").extensions("go"));
        }

        @Override
        public Tokenizer createCpdLexer(LanguagePropertyBundle bundle) {
            // This method should return an instance of the CpdLexer you created.
            return new GoCpdLexer();
        }
    } 
    ```

   To make PMD find the language module at runtime, write the fully-qualified name of your language class into the file `src/main/resources/META-INF/services/net.sourceforge.pmd.lang.Language`.

   At this point the new language module should be available in {% jdoc core::lang.LanguageRegistry#CPD %} and usable by CPD like any other language.

4. Update the test that asserts the list of supported languages by updating the `SUPPORTED_LANGUAGES` constant in [BinaryDistributionIT](https://github.com/pmd/pmd/blob/master/pmd-dist/src/test/java/net/sourceforge/pmd/dist/BinaryDistributionIT.java).

5. Add some tests for your CpdLexer by following the [section below](#testing-your-implementation).

6. Finishing up your new language module by adding a page in the documentation. Create a new markdown file
   `<langId>.md` in `docs/pages/pmd/languages/`. This file should have the following frontmatter:

   ```
   ---
   title: <Language Name>
   permalink: pmd_languages_<langId>.html
   last_updated: <Month> <Year> (<PMD Version>)
   tags: [languages, CpdCapableLanguage]
   ---
   ```

   On this page, language specifics can be documented, e.g. when the language was first supported by PMD.
   There is also the following Jekyll Include, that creates summary box for the language:

   ```
   {% raw %}
   {% include language_info.html name='<Language Name>' id='<langId>' implementation='<langId>::lang.<langId>.<langId>LanguageModule' supports_cpd=true %}
   {% endraw %}
   ```

### Declaring CpdLexer options

To make the CpdLexer configurable, first define some property descriptors using
{% jdoc core::properties.PropertyFactory %}. Look at {% jdoc core::cpd.CpdLanguageProperties %}
for some predefined ones which you can reuse (prefer reusing property descriptors if you can).
You need to override {% jdoc core::lang.Language#newPropertyBundle() %}
and call `definePropertyDescriptor` to register the descriptors.
After that you can access the values of the properties from the parameter
of {% jdoc core::cpd.CpdCapableLanguage#createCpdTokenizer(core::lang.LanguagePropertyBundle) %}.

To implement simple token filtering, you can use {% jdoc core::cpd.impl.BaseTokenFilter %}
as a base class, or another base class in {% jdoc_package core::cpd.impl %}.
Take a look at the [Kotlin token filter implementation](https://github.com/pmd/pmd/blob/master/pmd-kotlin/src/main/java/net/sourceforge/pmd/lang/kotlin/cpd/KotlinCpdLexer.java), or the [Java one](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/cpd/JavaCpdLexer.java).


### Testing your implementation

Add a Maven dependency on `pmd-lang-test` (scope `test`) in your `pom.xml`.
This contains utilities to test your CpdLexer.

Create a test class extending from {% jdoc lang-test::lang.test.cpd.CpdTextComparisonTest %}.
To add tests, you need to write regular JUnit `@Test`-annotated methods, and
call the method `doTest` with the name of the test file.

For example, for the Dart language:

```java
package net.sourceforge.pmd.lang.dart.cpd;

public class DartTokenizerTest extends CpdTextComparisonTest {

    /**********************************
      Implementation of the superclass
    ***********************************/


    public DartTokenizerTest() {
        super("dart", ".dart"); // the ID of the language, then the file extension used by test files
    }

    @Override
    protected String getResourcePrefix() {
        // "testdata" is the default value, you don't need to override.
        // This specifies that you should place the test files in
        // src/test/resources/net/sourceforge/pmd/lang/dart/cpd/testdata
        return "testdata";
    }

    /**************
      Test methods
    ***************/


    @Test  // don't forget the JUnit annotation
    public void testLiterals() {
        // This will look for a file named literals.dart
        // in the directory identified by getResourcePrefix,
        // tokenize it, then compare the result against a baseline
        // literals.txt file in the same directory

        // If the baseline file does not exist, it is created automatically
        doTest("literals");
    }

}
```
