<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

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
        <xsl:variable name="rules_file" select="document($filename)"/>
        <!--  Adding to our tree all the nodes present there -->
		<xsl:copy-of select="$rules_file"/>
    </xsl:template>

</xsl:stylesheet>