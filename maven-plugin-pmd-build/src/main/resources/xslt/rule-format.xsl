<?xml version="1.0" encoding="UTF-8"?>
<!-- BSD-style license; for more info see http://pmd.sourceforge.net/license.html -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="text" encoding="UTF-8" omit-xml-declaration="true" />

<!--
<author email="tom@infoether.com">Tom Copeland</author>
 -->

    <!-- FUTURE: Externalising text to allow i18n generation -->
    <xsl:variable name="Since" select="'Since: PMD '" />
    <xsl:variable name="Priority" select="'Priority'" />
    <xsl:variable name="definedByJavaClass" select="'This rule is defined by the following Java class'" />
    <xsl:variable name="ExampleLabel" select="'Example(s)'" />
    <xsl:variable name="PropertiesLabel" select="'This rule has the following properties'" />
    <xsl:variable name="Property.Name" select="'Name'" />
    <xsl:variable name="Property.DefaultValue" select="'Default Value'" />
    <xsl:variable name="Property.Desc" select="'Description'" />

    <xsl:variable name='newline'><xsl:text>&#xa;</xsl:text></xsl:variable>

    <xsl:template match="ruleset">
            <xsl:variable name="rulesetname" select="@name" />

# <xsl:value-of select="$rulesetname" />

<xsl:apply-templates />
    </xsl:template>

    <xsl:template match="rule[@name][not(@ref)]">
        <xsl:variable name="rulename" select="@name" />
        <xsl:variable name="classname" select="@class" />

## <xsl:value-of select="concat($rulename, $newline, $newline)" />

                <xsl:choose>
                    <xsl:when test="@deprecated='true'">
&lt;span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated&lt;/span>

                    </xsl:when>
                </xsl:choose>

<xsl:value-of select="$newline"/><xsl:value-of select="$Since" /> <xsl:value-of select="concat(@since, $newline, $newline)" />
<xsl:value-of select="$newline"/><xsl:value-of select="$Priority" />: <xsl:value-of select="concat(priority, $newline, $newline)" />
<xsl:value-of select="$newline"/><xsl:value-of select="description" />

            <xsl:choose>
                <xsl:when test="count(properties/property[@name='xpath']) != 0">
<xsl:value-of select="$newline"/>
<xsl:value-of select="$newline"/>
&lt;pre><xsl:value-of select="properties/property/value" />&lt;/pre>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:variable name="classfile">
                        <xsl:call-template name="url-maker">
                            <xsl:with-param name="classname" select="$classname" />
                        </xsl:call-template>
                    </xsl:variable>
<xsl:value-of select="$newline"/>
<xsl:value-of select="$definedByJavaClass" />: [<xsl:value-of select="@class" />](<xsl:value-of select="concat(concat('../../xref/',$classfile),'.html')" />)
<xsl:value-of select="$newline"/>
                </xsl:otherwise>
            </xsl:choose>

            <xsl:for-each select="./example">
<xsl:value-of select="$newline"/>
<xsl:value-of select="$newline"/>
<xsl:value-of select="$ExampleLabel" />:

&lt;pre><xsl:value-of select="." />&lt;/pre>
            </xsl:for-each>

            <xsl:variable name="hasproperties" select="count(properties/property[@name!='xpath'])" />
            <xsl:choose>
                <xsl:when test="$hasproperties != 0">
<xsl:value-of select="$newline"/>
<xsl:value-of select="$PropertiesLabel" />:<xsl:value-of select="concat($newline, $newline)"/>

&lt;table>
    &lt;th>
        <xsl:value-of select="$Property.Name" />
    &lt;/th>
    &lt;th>
        <xsl:value-of select="$Property.DefaultValue" />
    &lt;/th>
    &lt;th>
        <xsl:value-of select="$Property.Desc" />
    &lt;/th>
    <xsl:for-each select="properties/property[@name != 'xpath']">
        &lt;tr>
            &lt;td>
                <xsl:value-of select="@name" />
            &lt;/td>
            &lt;td>
                <xsl:value-of select="@value" />
            &lt;/td>
            &lt;td>
                <xsl:value-of select="@description" />
            &lt;/td>
        &lt;/tr>
    </xsl:for-each>
&lt;/table>
                </xsl:when>
            </xsl:choose>
    </xsl:template>

    <xsl:template match="rule[@name][@deprecated='true'][@ref][not(contains(@ref, '.xml'))]">
## <xsl:value-of select="concat(@name, $newline, $newline)" />

&lt;span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated&lt;/span>

This rule has been renamed. Use instead: [<xsl:value-of select="@ref"/>](#<xsl:value-of select="@ref" />)

    </xsl:template>

    <xsl:template match="rule[@deprecated='true'][@ref][contains(@ref, '.xml')][not(@name)]">
        <xsl:variable name="rulename">
            <xsl:call-template name="last-token-after-last-slash">
                <xsl:with-param name="ref" select="@ref"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="full-url" select="substring(@ref, 1, string-length(@ref) - string-length($rulename) - 1)"/>
        <xsl:variable name="ruleset-with-extension">
            <xsl:call-template name="last-token-after-last-slash">
                <xsl:with-param name="ref" select="$full-url"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="ruleset" select="substring($ruleset-with-extension, 1, string-length($ruleset-with-extension) - 4)"/>

## <xsl:value-of select="concat($rulename, $newline, $newline)" />

&lt;span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f;">Deprecated&lt;/span>

This rule has been moved to another ruleset. Use instead: [<xsl:value-of select="$rulename"/>](<xsl:value-of select="concat($ruleset, '.html#', $rulename)" />)

    </xsl:template>

    <xsl:template name="last-token-after-last-slash">
        <xsl:param name="ref" />
        <xsl:choose>
            <xsl:when test="contains($ref, '/')">
                <xsl:call-template name="last-token-after-last-slash">
                    <xsl:with-param name="ref" select="substring-after($ref, '/')" />
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$ref" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Watch out, recursing function... -->
    <xsl:template name="url-maker">
        <xsl:param name="classname" select="." />
        <!-- <xsl:message>classname is:<xsl:value-of select="$classname"/></xsl:message> -->
        <xsl:choose>
            <xsl:when test="contains($classname,'.')">
                <xsl:variable name="pre" select="concat(substring-before($classname,'.'),'/')" />
                <xsl:variable name="post" select="substring-after($classname,'.')" />
                <xsl:call-template name="url-maker">
                    <xsl:with-param name="classname" select="concat($pre,$post)" />
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$classname" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>