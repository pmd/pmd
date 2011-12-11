<?xml version="1.0" encoding="UTF-8"?>
<!--
	BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    <xsl:param name="menufile"/>

    <!-- Copy all nodes from here. Can be overridden by a more specific XPath -->
    <xsl:template match="@*|node()">
      <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
      </xsl:copy>
    </xsl:template>

    <xsl:template match="//menu[@name='Rule Sets']">
      <!-- <xsl:message>Edit existing site.xml with <xsl:value-of select="$menufile"/></xsl:message> -->
      <xsl:variable name="items_to_add" select="document($menufile)"/>
      <xsl:copy-of select="$items_to_add"/>
      <xsl:apply-templates/>
    </xsl:template>

</xsl:stylesheet>
