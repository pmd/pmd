<?xml version="1.0" encoding="UTF-8"?>
<!--
  BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" encoding="UTF-8" indent="yes"/>

  <xsl:template match="root">
    <xsl:comment>THIS FILE HAS BEEN AUTOMATICLY GENERATED.</xsl:comment>
    <menu name="Rule Sets">
      <xsl:apply-templates/>
    </menu>
  </xsl:template>

  <xsl:template match="language">
    <xsl:variable name="language"><xsl:value-of select="@name"/></xsl:variable>
    <xsl:element name="item">
      <xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
      <xsl:attribute name="collapse"><xsl:value-of select="'false'"/></xsl:attribute>
      <xsl:attribute name="href">/rules/index.html</xsl:attribute>

      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="ruleset">
      <xsl:variable name="ruleset_file" select="document(@file)"/>
      <!--
      <xsl:message>Looking in <xsl:value-of select="@file"/> to find ruleset "pretty" name: <xsl:value-of select="document(@file)//attribute::name"/></xsl:message>
      !-->
      <xsl:element name="item">
        <xsl:attribute name="name"><xsl:value-of select="$ruleset_file//attribute::name"/></xsl:attribute>
        <xsl:attribute name="href"><xsl:value-of select="concat('/rules/',concat(@language,concat('/',concat(substring-before(@filename,'.'),'.html'))))"/></xsl:attribute>
    </xsl:element>
  </xsl:template>

</xsl:stylesheet>
