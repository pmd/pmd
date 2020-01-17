---
title: Creating XML dump of the AST
tags: [devdocs, experimental]
summary: Creating a XML representation of the AST allows to analyze the AST with other tools.
last_updated: January 17, 2020 (6.21.0)
permalink: pmd_devdocs_experimental_ast_dump.html
---

## Command line usage

```shell
$ run.sh ast-dump
The following options are required: [--format | -f], [--language | -l]
Usage: ast-dump [options] The file to dump
  Options:
    --encoding, -e
      Encoding of the source file.
      Default: UTF-8
  * --format, -f
      The output format.
    --help, -h
      Display usage.
  * --language, -l
      Specify the language to use.

Available languages: apex ecmascript java jsp modelica plsql pom scala text vf vm wsdl xml xsl 
Available formats: xml 

Example: ast-dump --format xml --language java MyFile.java

```

## Example

```shell
$ cat Foo.java 
public class Foo {
  int a;
}

$ run.sh ast-dump --format xml --language java Foo.java > Foo.xml
-------------------------------------------------------------------------------
This command line utility is experimental. It might change at any time without
prior notice.
-------------------------------------------------------------------------------
Jan 17, 2020 10:08:58 AM net.sourceforge.pmd.lang.ast.xpath.Attribute getValue
WARNING: Use of deprecated attribute 'CompilationUnit/@declarationsAreInDefaultPackage' in XPath query
Jan 17, 2020 10:08:58 AM net.sourceforge.pmd.lang.ast.xpath.Attribute getValue
WARNING: Use of deprecated attribute 'ClassOrInterfaceDeclaration/@Image' in XPath query
Jan 17, 2020 10:08:58 AM net.sourceforge.pmd.lang.ast.xpath.Attribute getValue
WARNING: Use of deprecated attribute 'FieldDeclaration/@Array' in XPath query
Jan 17, 2020 10:08:58 AM net.sourceforge.pmd.lang.ast.xpath.Attribute getValue
WARNING: Use of deprecated attribute 'FieldDeclaration/@VariableName' in XPath query
Jan 17, 2020 10:08:58 AM net.sourceforge.pmd.lang.ast.xpath.Attribute getValue
WARNING: Use of deprecated attribute 'FieldDeclaration/@ArrayDepth' in XPath query
Jan 17, 2020 10:08:58 AM net.sourceforge.pmd.lang.ast.xpath.Attribute getValue
WARNING: Use of deprecated attribute 'Type/@Array' in XPath query
Jan 17, 2020 10:08:58 AM net.sourceforge.pmd.lang.ast.xpath.Attribute getValue
WARNING: Use of deprecated attribute 'Type/@ArrayDepth' in XPath query
Jan 17, 2020 10:08:58 AM net.sourceforge.pmd.lang.ast.xpath.Attribute getValue
WARNING: Use of deprecated attribute 'PrimitiveType/@Array' in XPath query
Jan 17, 2020 10:08:58 AM net.sourceforge.pmd.lang.ast.xpath.Attribute getValue
WARNING: Use of deprecated attribute 'PrimitiveType/@ArrayDepth' in XPath query
Jan 17, 2020 10:08:58 AM net.sourceforge.pmd.lang.ast.xpath.Attribute getValue
WARNING: Use of deprecated attribute 'VariableDeclaratorId/@Array' in XPath query
Jan 17, 2020 10:08:58 AM net.sourceforge.pmd.lang.ast.xpath.Attribute getValue
WARNING: Use of deprecated attribute 'VariableDeclaratorId/@ArrayDepth' in XPath query

$ cat Foo.xml
<?xml version='1.0' encoding='UTF-8' ?>
<CompilationUnit BeginColumn='1' BeginLine='1' EndColumn='2' EndLine='3' FindBoundary='false' Image='' PackageName='' SingleLine='false' declarationsAreInDefaultPackage='true'>
    <TypeDeclaration BeginColumn='1' BeginLine='1' EndColumn='1' EndLine='3' FindBoundary='false' Image='' SingleLine='false'>
        <ClassOrInterfaceDeclaration Abstract='false' BeginColumn='8' BeginLine='1' BinaryName='Foo' Default='false' EndColumn='1' EndLine='3' Final='false' FindBoundary='false' Image='Foo' Interface='false' Local='false' Modifiers='1' Native='false' Nested='false' PackagePrivate='false' Private='false' Protected='false' Public='true' SimpleName='Foo' SingleLine='false' Static='false' Strictfp='false' Synchronized='false' Transient='false' TypeKind='CLASS' Volatile='false'>
            <ClassOrInterfaceBody AnonymousInnerClass='false' BeginColumn='18' BeginLine='1' EndColumn='1' EndLine='3' EnumChild='false' FindBoundary='false' Image='' SingleLine='false'>
                <ClassOrInterfaceBodyDeclaration AnonymousInnerClass='false' BeginColumn='3' BeginLine='2' EndColumn='8' EndLine='2' EnumChild='false' FindBoundary='false' Image='' Kind='FIELD' SingleLine='true'>
                    <FieldDeclaration Abstract='false' AnnotationMember='false' Array='false' ArrayDepth='0' BeginColumn='3' BeginLine='2' Default='false' EndColumn='8' EndLine='2' Final='false' FindBoundary='false' Image='' InterfaceMember='false' Modifiers='0' Native='false' PackagePrivate='true' Private='false' Protected='false' Public='false' SingleLine='true' Static='false' Strictfp='false' Synchronized='false' SyntacticallyFinal='false' SyntacticallyPublic='false' SyntacticallyStatic='false' Transient='false' VariableName='a' Volatile='false'>
                        <Type Array='false' ArrayDepth='0' ArrayType='false' BeginColumn='3' BeginLine='2' EndColumn='5' EndLine='2' FindBoundary='false' Image='' SingleLine='true' TypeImage='int'>
                            <PrimitiveType Array='false' ArrayDepth='0' BeginColumn='3' BeginLine='2' Boolean='false' EndColumn='5' EndLine='2' FindBoundary='false' Image='int' SingleLine='true' />
                        </Type>
                        <VariableDeclarator BeginColumn='7' BeginLine='2' EndColumn='7' EndLine='2' FindBoundary='false' Image='' Initializer='false' Name='a' SingleLine='true'>
                            <VariableDeclaratorId Array='false' ArrayDepth='0' ArrayType='false' BeginColumn='7' BeginLine='2' EndColumn='7' EndLine='2' ExceptionBlockParameter='false' ExplicitReceiverParameter='false' Field='true' Final='false' FindBoundary='false' FormalParameter='false' Image='a' LambdaParameter='false' LocalVariable='false' ResourceDeclaration='false' SingleLine='true' TypeInferred='false' VariableName='a' />
                        </VariableDeclarator>
                    </FieldDeclaration>
                </ClassOrInterfaceBodyDeclaration>
            </ClassOrInterfaceBody>
        </ClassOrInterfaceDeclaration>
    </TypeDeclaration>
</CompilationUnit>

$ xmlstarlet select -t -c "//VariableDeclaratorId[@VariableName='a']" Foo.xml
<VariableDeclaratorId Array="false" ArrayDepth="0" ArrayType="false" BeginColumn="7" BeginLine="2" EndColumn="7" EndLine="2" ExceptionBlockParameter="false" ExplicitReceiverParameter="false" Field="true" Final="false" FindBoundary="false" FormalParameter="false" Image="a" LambdaParameter="false" LocalVariable="false" ResourceDeclaration="false" SingleLine="true" TypeInferred="false" VariableName="a"/>
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
