<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xpath-default-namespace="http://pmd.sourceforge.net/report/2.0.0" version="2.0">
<xsl:output method="text"/>

<xsl:template match="testsuites">
Junit test results
  <xsl:for-each select="testsuite[(@failures + @errors) &gt; 0]">
Test suite <xsl:value-of select="@name"/> failed (failures: <xsl:value-of select="@failures"/>, errors: <xsl:value-of select="@errors"/>)
      <xsl:for-each select="testcase[(count(error) + count(failure)) &gt; 0]">
  Test case <xsl:value-of select="@name"/> failed (failures: <xsl:value-of select="count(failure)"/>, errors: <xsl:value-of select="count(error)"/>)
      <xsl:for-each select="failure">
    failure: <xsl:value-of select="@message"/>.
      </xsl:for-each>
      <xsl:for-each select="error">
    error: <xsl:value-of select="@message"/>.
      </xsl:for-each>
    </xsl:for-each>
  </xsl:for-each>
Summary: <xsl:value-of select="count(//testcase)"/> tests
  failures: <xsl:value-of select="count(//failure)"/>, errors: <xsl:value-of select="count(//error)"/>.
</xsl:template>

</xsl:stylesheet>
