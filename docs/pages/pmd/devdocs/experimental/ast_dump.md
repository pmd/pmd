---
title: Creating XML dump of the AST
tags: [devdocs, experimental]
summary: Creating a XML representation of the AST allows to analyze the AST with other tools.
last_updated: January 17, 2020 (6.21.0)
permalink: pmd_devdocs_experimental_ast_dump.html
---

## Command line usage

```shell
$ run.sh ast-dump --help
Usage: ast-dump [options]
  Options:
    --encoding, -e
      Encoding of the source file.
      Default: UTF-8
    --file
      The file to dump
    --format, -f
      The output format.
      Default: xml
    --help, -h
      Display usage.
    --language, -l
      Specify the language to use.
      Default: java
    --read-stdin, -i
      Read source from standard input
      Default: false
    -P
      Properties for the renderer.
      Syntax: -Pkey=value
      Default: {}

Available languages: apex ecmascript java jsp modelica plsql pom scala text vf vm wsdl xml xsl 
Available formats: xml                           XML format with the same structure as the one used in XPath
+ Properties                  
  + singleQuoteAttributes         Use single quotes to delimit attribute values (default true)
  + lineSeparator                 Line separator to use. The default is platform-specific. (default \n)
  + renderProlog                  True to output a prolog (default true)
  + renderCommonAttributes        True to render attributes like BeginLine, EndLine, etc. (default false)
```

## Example

```shell
$ cat Foo.java
public class Foo {
  int a;
}

$ run.sh ast-dump --format xml --language java --file Foo.java > Foo.xml
-------------------------------------------------------------------------------
This command line utility is experimental. It might change at any time without
prior notice.
-------------------------------------------------------------------------------

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
