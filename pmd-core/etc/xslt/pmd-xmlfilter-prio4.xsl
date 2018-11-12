<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xpath-default-namespace="http://pmd.sourceforge.net/report/2.0.0" version="2.0">
	<xsl:output method="xml"/>
	<xsl:template match="node() | @*">
		<xsl:copy>
			<xsl:apply-templates select="node() | @*"/>
		</xsl:copy>
	</xsl:template>
	<xsl:template match="pmd/file/violation[@priority='1']"/>
	<xsl:template match="pmd/file/violation[@priority='2']"/>
	<xsl:template match="pmd/file/violation[@priority='3']"/>
	<xsl:template match="pmd/file/violation[@priority='5']"/>
		
</xsl:stylesheet>

