<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:variable name="cvsweb">http://doc.ece.uci.edu/cvs/viewcvs.cgi/Zen/packages/src/</xsl:variable>

<xsl:template match="pmd">
<html>
<head>
    <title>PMD Report</title>
    <style type="text/css">
        body { margin-left: 2%; margin-right: 2%; font:normal verdana,arial,helvetica; color:#000000; }
        table.details tr th { font-weight: bold; text-align:left; background:#a6caf0; }
        table.details tr td { background:#eeeee0; }
    </style>
</head>
<body>
    <H1>PMD Report</H1>
    <hr/>
    <xsl:for-each select="file">
        <xsl:sort data-type="number" order="descending" select="count(violation)"/>
        <xsl:variable name="filename" select="@name"/>
        <H3><xsl:value-of disable-output-escaping="yes" select="substring-before(translate(@name,'/','.'),'.java')"/></H3>
        <table border="0" width="100%" class="details">
            <tr>
                <th>Line</th>
                <th align="left">Description</th>
            </tr>
	    
	    <xsl:for-each select="violation">
		    <tr>
			<td style="padding: 3px" align="right"><a><xsl:attribute name="href"><xsl:value-of select="$cvsweb"/><xsl:value-of select="$filename"/>?annotate=HEAD#<xsl:value-of disable-output-escaping="yes" select="@line"/></xsl:attribute><xsl:value-of disable-output-escaping="yes" select="@line"/></a></td>
			<td style="padding: 3px" align="left" width="100%"><xsl:value-of disable-output-escaping="yes" select="."/></td>
		    </tr>
	    </xsl:for-each>
    
        </table>
        <br/>
    </xsl:for-each>
</body>
</html>
</xsl:template>

</xsl:stylesheet>
