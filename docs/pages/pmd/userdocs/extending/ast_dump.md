---
title: Creating XML dump of the AST
tags: [userdocs]
summary: Creating a XML representation of the AST allows to analyze the AST with other tools.
last_updated: January 2024 (7.0.0)
permalink: pmd_userdocs_extending_ast_dump.html
---

## Command line usage

```shell
$ pmd ast-dump --help
Usage: pmd ast-dump [-Dhi] [-e=<encoding>] [-f=<format>] [--file=<file>]
                    [-l=<language>] [-P=<String=String>]...
Dumps the AST of parsing source code
  -D, -v, --debug, --verbose
                          Debug mode.
  -e, --encoding=<encoding>
                          Specifies the character set encoding of the source
                            code files
  -f, --format=<format>   The output format.
                          Valid values: xml, text
      --file=<file>       The file to parse and dump.
  -h, --help              Show this help message and exit.
  -i, --read-stdin        Read source from standard input.
  -l, --language=<language>
                          The source code language.
                          Valid values: apex, ecmascript, html, java, jsp,
                            kotlin, modelica, plsql, pom, scala, swift, vf, vm,
                            wsdl, xml, xsl
  -P=<String=String>      Key-value pair defining a property for the report
                            format.
                          Supported values for each report format:
                          xml:
                            singleQuoteAttributes - Use single quotes to
                            delimit attribute values
                              Default: true
                            lineSeparator - Line separator to use. The default
                            is platform-specific. The values 'CR', 'CRLF',
                            'LF', '\r', '\r\n' and '\n' can be used to
                            represent a carriage return, line feed and their
                            combination more easily.
                              Default: \n
                            renderProlog - True to output a prolog
                              Default: true
                            renderCommonAttributes - True to render attributes
                            like BeginLine, EndLine, etc.
                              Default: false
                          text:
                            onlyAsciiChars - Use only ASCII characters in the
                            structure
                              Default: false
                            maxLevel - Max level on which to recurse. Negative
                            means unbounded
                              Default: -1
```

## Example

```shell
$ cat Foo.java
public class Foo {
  int a;
}

$ pmd ast-dump --format xml --language java --file Foo.java > Foo.xml
$ cat Foo.xml
<?xml version='1.0' encoding='UTF-8' ?>
<CompilationUnit Image='' PackageName=''>
    <ClassDeclaration Abstract='false' Annotation='false' Anonymous='false' BinaryName='Foo' CanonicalName='Foo' EffectiveVisibility='public' Enum='false' Final='false' Image='Foo' Interface='false' Local='false' Native='false' Nested='false' PackageName='' PackagePrivate='false' Private='false' Protected='false' Public='true' Record='false' RegularClass='true' RegularInterface='false' SimpleName='Foo' Static='false' Strictfp='false' Synchronized='false' SyntacticallyAbstract='false' SyntacticallyFinal='false' SyntacticallyPublic='true' SyntacticallyStatic='false' TopLevel='true' Transient='false' Visibility='public' Volatile='false'>
        <ModifierList Image='' />
        <ClassBody Empty='false' Image='' Size='1'>
            <FieldDeclaration Abstract='false' EffectiveVisibility='package' Final='false' Image='' Native='false' PackagePrivate='true' Private='false' Protected='false' Public='false' Static='false' Strictfp='false' Synchronized='false' SyntacticallyAbstract='false' SyntacticallyFinal='false' SyntacticallyPublic='false' SyntacticallyStatic='false' Transient='false' VariableName='a' Visibility='package' Volatile='false'>
                <ModifierList Image='' />
                <PrimitiveType ArrayDepth='0' ArrayType='false' ClassOrInterfaceType='false' Image='' Kind='int' PrimitiveType='true' TypeImage='int' />
                <VariableDeclarator Image='' Initializer='false' Name='a'>
                    <VariableId Abstract='false' ArrayType='false' EffectiveVisibility='package' EnumConstant='false' ExceptionBlockParameter='false' Field='true' Final='false' ForLoopVariable='false' ForeachVariable='false' FormalParameter='false' Image='a' LambdaParameter='false' LocalVariable='false' Name='a' Native='false' PackagePrivate='true' PatternBinding='false' Private='false' Protected='false' Public='false' RecordComponent='false' ResourceDeclaration='false' Static='false' Strictfp='false' Synchronized='false' SyntacticallyAbstract='false' SyntacticallyFinal='false' SyntacticallyPublic='false' SyntacticallyStatic='false' Transient='false' TypeInferred='false' VariableName='a' Visibility='package' Volatile='false' />
                </VariableDeclarator>
            </FieldDeclaration>
        </ClassBody>
    </ClassDeclaration>
</CompilationUnit>

$ xmlstarlet select -t -c "//VariableId[@VariableName='a']" Foo.xml
<VariableId Abstract="false" ArrayType="false" EffectiveVisibility="package" EnumConstant="false" ExceptionBlockParameter="false" Field="true" Final="false" ForLoopVariable="false" ForeachVariable="false" FormalParameter="false" Image="a" LambdaParameter="false" LocalVariable="false" Name="a" Native="false" PackagePrivate="true" PatternBinding="false" Private="false" Protected="false" Public="false" RecordComponent="false" ResourceDeclaration="false" Static="false" Strictfp="false" Synchronized="false" SyntacticallyAbstract="false" SyntacticallyFinal="false" SyntacticallyPublic="false" SyntacticallyStatic="false" Transient="false" TypeInferred="false" VariableName="a" Visibility="package" Volatile="false"/>
```

This example uses [xmlstarlet](http://xmlstar.sourceforge.net/) to query the xml document for any variables/fields
with the name "a".


## Programmatic usage

Just parse your source code to get the AST and pass it on to the `XmlTreeRenderer`:

```java
import java.io.IOException;

import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguageProcessorRegistry;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.PmdCapableLanguage;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.util.treeexport.XmlTreeRenderer;

public class TreeExportTest {
    public static void main(String[] args) throws IOException {
        PmdCapableLanguage java = (PmdCapableLanguage) LanguageRegistry.PMD.getLanguageById("java");
        LanguageProcessor processor = java.createProcessor(java.newPropertyBundle());
        Parser parser = processor.services().getParser();

        try (TextDocument textDocument = TextDocument.readOnlyString("class Foo { int a; }", java.getDefaultVersion());
             LanguageProcessorRegistry lpr = LanguageProcessorRegistry.singleton(processor)) {

            Parser.ParserTask task = new Parser.ParserTask(textDocument, SemanticErrorReporter.noop(), lpr);
            RootNode root = parser.parse(task);

            new XmlTreeRenderer().renderSubtree(root, System.out);
        }
    }
}
```
