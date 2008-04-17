<?xml version="1.0" encoding="UTF-8"?>
<!--
	BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

	<!-- FUTURE: Externalising text to allow i18n generation  -->
	<xsl:variable name="Since"					select="'Since: PMD'"/>
	<xsl:variable name="definedByJavaClass"		select="'This rule is defined by the following Java class'"/>
	<xsl:variable name="ExampleLabel"			select="'Example(s)'"/>
	<xsl:variable name="PropertiesLabel"		select="'This rule has the following properties'"/>
	<xsl:variable name="Property.Name"			select="'Name'"/>
	<xsl:variable name="Property.DefaultValue"	select="'Default Value'"/>
	<xsl:variable name="Property.Desc"			select="'Description'"/>
	<xsl:variable name="RuleSet"				select="'Ruleset'"/>

	<xsl:template match="ruleset">
		<document>
			<xsl:variable name="rulesetname" select="@name"/>
			<properties>
				<author email="tom@infoether.com">Tom Copeland</author>
				<title><xsl:value-of select="$RuleSet"/>: <xsl:value-of select="$rulesetname"/></title>
			</properties>
			<body>
				<section>
					<xsl:attribute name="name">
						<xsl:value-of select="$rulesetname"/>
					</xsl:attribute>
					<xsl:apply-templates/>
				</section>
			</body>
		</document>
	</xsl:template>

	<xsl:template match="rule">
		<xsl:variable name="rulename" select="@name"/>

		<subsection>
			<xsl:attribute name="name">
				<xsl:value-of select="$rulename"/>
			</xsl:attribute>
			<p><xsl:value-of select="$Since"/><xsl:value-of select="@since"/></p>
			<p><xsl:value-of select="description"/></p>
			<xsl:choose>
				<xsl:when test="count(properties/property[@name='xpath']) != 0">
					<source>
						<xsl:value-of select="properties/property/value"/>
					</source>
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="classfile" select="@class"/>
					<!--
					<util:replace var="classfile" value="${classname}" newChar="/" oldChar="."/>
	    		     How to this ?
	    		         		    <j:set var="classname"><x:expr select="@class"/></j:set>
	    		    <util:replace var="classfile" value="${classname}" newChar="/" oldChar="."/>
	    		    <j:set var="classjxr" value="../xref/${classfile}.html"/>
					 -->
					<p><xsl:value-of select="$definedByJavaClass"/>:<xsl:value-of select="@class"/>
	    		    <!-- <a href="${classjxr}"> -->
	    		    </p>
				</xsl:otherwise>
			</xsl:choose>
<!--
			<xsl:for-each select="./example">

				<xsl:value-of select="$ExampleLabel"/>:
	        	<source>
	        		<xsl:value-of select="."/>
	        	</source>
	      	</xsl:for-each>
 -->
	        <xsl:variable name="hasproperties" select="count(properties/property[@name!='xpath'])"/>
	        <xsl:choose>
	            <xsl:when test="$hasproperties != 0">
	                <p><xsl:value-of select="$PropertiesLabel"/>:</p>
	                <table><th><xsl:value-of select="$Property.Name"/></th><th><xsl:value-of select="$Property.DefaultValue"/></th><th><xsl:value-of select="$Property.Desc"/></th>
	                <xsl:for-each select="properties/property[@name != 'xpath']">
	                    <tr>
		                    <td><xsl:value-of select="@name"/></td>
		                    <td><xsl:value-of select="@value"/></td>
		                    <td><xsl:value-of select="@description"/></td>
	                    </tr>
	                </xsl:for-each>
	                </table>
	            </xsl:when>
	        </xsl:choose>

		</subsection>
	</xsl:template>

</xsl:stylesheet>