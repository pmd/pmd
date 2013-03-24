<?xml version="1.0" encoding="UTF-8"?>
<!--
  BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 -->
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>

    <xsl:template match="root">
		<xsl:comment>
        THIS FILE HAS BEEN AUTOMATICLY GENERATED.
        </xsl:comment>
		<rulesets>
			<xsl:apply-templates/>
		</rulesets>
    </xsl:template>

    <xsl:template match="ruleset">
    	<!--  Opening the appropriate file -->
        <xsl:variable name="filename" select="@file"/>
        <xsl:element name="language">
          <xsl:attribute name="name"><xsl:value-of select="@language"/></xsl:attribute>

          <!--  Adding to our tree all the nodes present there -->
          <xsl:variable name="rules_file" select="document($filename)"/>
		  <xsl:copy-of select="$rules_file"/>
		</xsl:element>
    </xsl:template>

</xsl:stylesheet>
