<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="pmd">
<html>
<head>
    <title>PMD report</title>
    <style type="text/css">
        body { margin-left: 2%; margin-right: 2% }
    </style>
</head>
<body>
    <H1>PMD report</H1>
    <hr/>
    <xsl:for-each select="file">
        <xsl:sort data-type="number" order="descending" select="count(violation)"/>
        <H3><xsl:value-of disable-output-escaping="yes" select="@name"/></H3>
        <table border="1" cellpadding="7" cellspacing="1" width="100%">
            <tr bgcolor="#87CEEB">
                <th>Line</th>
                <th align="left">Description</th>
            </tr>
            <xsl:apply-templates select="violation"/>
        </table>
        <br/><br/><br/>
    </xsl:for-each>
</body>
</html>
</xsl:template>

<xsl:template match="violation">
    <tr>
        <th><xsl:value-of disable-output-escaping="yes" select="@line"/></th>
        <th align="left"><xsl:value-of disable-output-escaping="yes" select="."/></th>
    </tr>
</xsl:template>

</xsl:stylesheet>
