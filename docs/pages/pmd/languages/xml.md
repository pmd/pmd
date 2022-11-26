---
title: Processing XML files
permalink: pmd_languages_xml.html
last_updated: March 2022 (6.44.0)
---

## The XML language module

PMD has an XML language module which exposes the [DOM](https://de.wikipedia.org/wiki/Document_Object_Model)
of an XML document as an AST. Different flavours of XML are represented by separate
language instances, which all use the same parser under the hood. The following
table lists the languages currently provided by the `pmd-xml` maven module.

| Language ID | Description                       |
|-------------|-----------------------------------|
| xml         | Generic XML language              |
| pom         | Maven Project Object Model (POM)  |
| wsdl        | Web Services Description Language |
| xsl         | Extensible Stylesheet Language    |

Each of those languages has a separate rule index, and may provide domain-specific
[XPath functions](pmd_userdocs_extending_writing_xpath_rules.html#pmd-extension-functions).
At their core they use the same parsing facilities though.

### File attribution

Any file ending with `.xml` is associated with the `xml` language. Other XML flavours
use more specific extensions, like `.xsl`.

Some XML-based file formats do not conventionally use a `.xml` extension. To associate
these files with the XML language, you need to use the `--force-language xml` command-line
arguments, for instance:
```
$ pmd check -d /home/me/src/xml-file.ext -f text -R ruleset.xml --force-language xml
```
Please refer to [PMD CLI reference](pmd_userdocs_cli_reference.html#analyze-other-xml-formats)
for more examples.


### XPath rules in XML

While other languages use {% jdoc core::lang.rule.XPathRule %} to create XPath rules,
the use of this class is not recommended for XML languages. Instead, since 6.44.0, you
are advised to use {% jdoc xml::lang.xml.rule.DomXPathRule %}. This rule class interprets
XPath queries exactly as regular XPath, while `XPathRule` works on a wrapper for the
DOM which is inconsistent with the XPath spec. Since `DomXPathRule` conforms to the
XPath spec, you can
- test XML queries in any stock XPath testing tool, or use resources like StackOverflow
  to help you write XPath queries.
- match XML comments and processing instructions
- use standard XPath functions like `text()` or `fn:string`

{% include note.html content="The Rule Designer only works with `XPathRule`, and the tree it prints is inconsistent with the DOM representation used by `DomXPathRule`. You can use an online free XPath testing tool to test your query instead." %}

Here's an example declaration of a `DomXPathRule`:
```xml
<rule name="MyXPathRule"
      language="xml"
      message="A message"
      class="net.sourceforge.pmd.lang.xml.rule.DomXPathRule">

      <properties>
        <property name="xpath">
            <value><![CDATA[
            /a/b/c[@attr = "5"]
            ]]></value>
        </property>
        <!-- Note: the property "version" is unsupported. -->
      </properties>
</rule>
```
The most important change is the `class` attribute, which doesn't point to `XPathRule`
but to `DomXPathRule`. Please see the Javadoc for {% jdoc xml::lang.xml.rule.DomXPathRule %}
for more info about the differences with `XPathRule`.

