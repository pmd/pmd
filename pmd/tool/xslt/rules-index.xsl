<?xml version="1.0" encoding="UTF-8"?>
<!--
	BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

	<!--  FUTURE: Externalising text to allow i18n documnetation -->
	<xsl:variable 	name="Title"	select="'PMD Rulesets index'"/>
	<xsl:variable 	name="PageDesc"	select="'List of rulesets and rules contained in each ruleset.'"/>

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
               		<xsl:for-each select="ruleset">
	                    <li>
	                    	<a>
	                    		<xsl:attribute name="href">#<xsl:value-of select="@name"/></xsl:attribute>
	                    		<xsl:value-of select="@name"/>
	                    	</a>: <xsl:value-of select="description"/>
	                    </li>
                	</xsl:for-each>
                </ul>
                <xsl:for-each select="ruleset">
						<subsection>
							<xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
							<xsl:for-each select="./rule">
								<li> <xsl:value-of select="@name"/>: <xsl:value-of select="description"/></li>
							</xsl:for-each>
						</subsection>
                </xsl:for-each>
			</section>
			<!--
			TODO
			<section name="Rules by name">
			    <p>List of rules sorted alphabetically.</p>
			</section>
			-->
			</body>
		</document>
	</xsl:template>

</xsl:stylesheet>