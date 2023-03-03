---
title: Creating XML dump of the AST
tags: [devdocs, experimental]
summary: Creating a XML representation of the AST allows to analyze the AST with other tools.
last_updated: January 17, 2020 (6.21.0)
permalink: pmd_devdocs_experimental_ast_dump.html
---

## Command line usage

```shell
$ pmd ast-dump --help
Usage: pmd ast-dump [-Dhi] [-e=<encoding>] [-f=<format>] [--file=<file>]
                    [-l=<language>] [-P=<String=String>]...
Experimental: dumps the AST of parsing source code
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
<CompilationUnit Image='' PackageName='' declarationsAreInDefaultPackage='true'>
    <TypeDeclaration Image=''>
        <ClassOrInterfaceDeclaration Abstract='false' BinaryName='Foo' Default='false' Final='false' Image='Foo' Interface='false' Local='false' Modifiers='1' Native='false' Nested='false' PackagePrivate='false' Private='false' Protected='false' Public='true' SimpleName='Foo' Static='false' Strictfp='false' Synchronized='false' Transient='false' TypeKind='CLASS' Volatile='false'>
            <ClassOrInterfaceBody AnonymousInnerClass='false' EnumChild='false' Image=''>
                <ClassOrInterfaceBodyDeclaration AnonymousInnerClass='false' EnumChild='false' Image='' Kind='FIELD'>
                    <FieldDeclaration Abstract='false' AnnotationMember='false' Array='false' ArrayDepth='0' Default='false' Final='false' Image='' InterfaceMember='false' Modifiers='0' Native='false' PackagePrivate='true' Private='false' Protected='false' Public='false' Static='false' Strictfp='false' Synchronized='false' SyntacticallyFinal='false' SyntacticallyPublic='false' SyntacticallyStatic='false' Transient='false' VariableName='a' Volatile='false'>
                        <Type Array='false' ArrayDepth='0' ArrayType='false' Image='' TypeImage='int'>
                            <PrimitiveType Array='false' ArrayDepth='0' Boolean='false' Image='int' />
                        </Type>
                        <VariableDeclarator Image='' Initializer='false' Name='a'>
                            <VariableDeclaratorId Array='false' ArrayDepth='0' ArrayType='false' ExceptionBlockParameter='false' ExplicitReceiverParameter='false' Field='true' Final='false' FormalParameter='false' Image='a' LambdaParameter='false' LocalVariable='false' ResourceDeclaration='false' TypeInferred='false' VariableName='a' />
                        </VariableDeclarator>
                    </FieldDeclaration>
                </ClassOrInterfaceBodyDeclaration>
            </ClassOrInterfaceBody>
        </ClassOrInterfaceDeclaration>
    </TypeDeclaration>
</CompilationUnit>

$ xmlstarlet select -t -c "//VariableDeclaratorId[@VariableName='a']" Foo.xml
<VariableDeclaratorId Array="false" ArrayDepth="0" ArrayType="false" ExceptionBlockParameter="false" ExplicitReceiverParameter="false" Field="true" Final="false" FormalParameter="false" Image="a" LambdaParameter="false" LocalVariable="false" ResourceDeclaration="false" TypeInferred="false" VariableName="a"/>
```

This example uses [xmlstarlet](http://xmlstar.sourceforge.net/) to query the xml document for any variables/fields
with the name "a".


## Programmatic usage

Just parse your source code to get the AST and pass it on to the `XmlTreeRenderer`:

```java
import java.io.IOException;
import java.io.StringReader;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.treeexport.XmlTreeRenderer;

public class TreeExport {
    public static void main(String[] args) throws IOException {
        LanguageVersionHandler java = LanguageRegistry.getLanguage("Java").getDefaultVersion().getLanguageVersionHandler();
        Parser parser = java.getParser(java.getDefaultParserOptions());
        Node root = parser.parse("foo", new StringReader("class Foo {}"));

        new XmlTreeRenderer().renderSubtree(root, System.out);
    }
}
```
