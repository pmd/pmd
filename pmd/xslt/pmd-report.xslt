<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="pmd">
<html>
<head>
    <title>PMD report</title>
    <style type="text/css">
        body { margin-left: 2%; margin-right: 2%; font:normal 68% verdana,arial,helvetica; color:#000000; }
        table tr td, tr th { font-size: 68%; }
        table.details tr th { font-weight: bold; text-align:left; background:#a6caf0; }
        table.details tr td { background:#eeeee0; }
    </style>
</head>
<body>
    <H1>PMD report</H1>
    <hr/>
    <xsl:for-each select="file">
        <xsl:sort data-type="number" order="descending" select="count(violation)"/>
        <H3><xsl:value-of disable-output-escaping="yes" select="@name"/></H3>
        <table border="0" width="100%" class="details">
            <tr>
                <th width="50">Line</th>
                <th align="left">Description</th>
            </tr>
            <xsl:apply-templates select="violation"/>
        </table>
        <br/>
    </xsl:for-each>
</body>
</html>
</xsl:template>

<xsl:template match="violation">
    <tr>
        <td><xsl:value-of disable-output-escaping="yes" select="@line"/></td>
        <td align="left"><xsl:value-of disable-output-escaping="yes" select="."/></td>
    </tr>
</xsl:template>

</xsl:stylesheet>
