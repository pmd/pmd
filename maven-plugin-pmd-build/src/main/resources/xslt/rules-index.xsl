<?xml version="1.0" encoding="UTF-8"?>
<!--
  BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" encoding="UTF-8" indent="yes"/>

  <!--  FUTURE: Externalising text to allow i18n documnetation -->
  <xsl:variable   name="Title"  select="'PMD Rulesets index'"/>
  <xsl:variable   name="PageDesc"  select="'List of rulesets and rules contained in each ruleset.'"/>

  <xsl:template match="rulesets">
    <document>
      <properties>
        <author email="mikkey@sourceforge.net">Miguel Griffa</author>
        <title><xsl:value-of select="$Title"/></title>
      </properties>
      <body>
        <section name="Current Rulesets">
          <p><xsl:value-of select="$PageDesc"/></p>
          <ul>
            <xsl:for-each select="./language/ruleset">
              <xsl:sort select="@name"/>
              <li>
                <a>
                  <xsl:attribute name="href">#<xsl:value-of select="translate(normalize-space(@name),' ','_')"/></xsl:attribute>
                  <xsl:value-of select="@name"/>
                </a>: <xsl:value-of select="description"/>
              </li>
            </xsl:for-each>
          </ul>

          <xsl:variable name="urlPrefixLength"><xsl:value-of select="string-length('${pmd.website.baseurl}/rules/')"/></xsl:variable>

          <xsl:for-each select="language">
            <xsl:variable name="language"><xsl:value-of select="@name"/></xsl:variable>
            <xsl:for-each select="ruleset">
              <xsl:element name="a">
                <xsl:attribute name="name">
                  <xsl:value-of select="translate(normalize-space(@name),' ','_')"/>
                </xsl:attribute>
              </xsl:element>
              <subsection>
                <xsl:attribute name="name"><xsl:value-of select="@name"/> (<xsl:value-of select="$language"/>)</xsl:attribute>
                <ul>
                  <xsl:for-each select="./rule[@name]">
                    <li>
                      <a>
                        <xsl:attribute name="href"><xsl:value-of select="substring(@externalInfoUrl,$urlPrefixLength + 1)"/></xsl:attribute>
                        <xsl:value-of select="@name"/>
                      </a>: <xsl:value-of select="description"/></li>
                  </xsl:for-each>
                </ul>
              </subsection>
            </xsl:for-each>
          </xsl:for-each>
        </section>
      </body>
    </document>
  </xsl:template>
</xsl:stylesheet>
