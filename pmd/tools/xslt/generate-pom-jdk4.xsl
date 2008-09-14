<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    <!-- Copy all nodes from here. Can be overridden by a more specific XPath -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <!--Altering specific part of the pom to adjust to jdk4 -->
	<xsl:template match="project/artifactId">
		<artifactId>pmd-jdk14</artifactId>
	</xsl:template>

    <!--Removing description as it useless for retroweaver and may lead to defectious pom -->
	<xsl:template match="project/description">
		<xsl:comment>Description removed for this pom...</xsl:comment>
	</xsl:template>

	<xsl:template match="dependency[groupId/child::text() = 'junit' ]">
		<xsl:comment>Modified dependancy for jdk4</xsl:comment>
		    <dependency>
		    	<groupId>junit</groupId>
		    	<artifactId>junit</artifactId>
		    	<version>3.8.2</version>
		    </dependency>
		    <xsl:comment>Added dependancies for jdk4</xsl:comment>
		    <dependency>
      			<groupId>backport-util-concurrent</groupId>
      			<artifactId>backport-util-concurrent</artifactId>
      			<version>3.1</version>
			</dependency>
			<dependency>
				<groupId>net.sourceforge.retroweaver</groupId>
				<artifactId>retroweaver-rt</artifactId>
				<version>2.0.6</version>
			</dependency>
	</xsl:template>

</xsl:stylesheet>