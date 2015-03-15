<?xml version="1.0" encoding="UTF-8"?>
<!-- BSD-style license; for more info see http://pmd.sourceforge.net/license.html -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="text" encoding="UTF-8" omit-xml-declaration="true" />

<!--                 <author email="mikkey@sourceforge.net">Miguel Griffa</author> -->

    <!-- FUTURE: Externalising text to allow i18n documnetation -->
    <xsl:variable name="Title" select="'PMD Rulesets index'" />
    <xsl:variable name="PageDesc" select="'List of rulesets and rules contained in each ruleset.'" />

    <xsl:variable name='newline'><xsl:text>&#xa;</xsl:text></xsl:variable>

    <xsl:template match="rulesets">
# <xsl:value-of select="$Title" />: Current Rulesets

<xsl:value-of select="$PageDesc" />
<xsl:value-of select="$newline" />

<xsl:for-each select="./language/ruleset">
    <xsl:sort select="@name" />
*   [<xsl:value-of select="@name" />](#<xsl:value-of select="translate(normalize-space(@name),' ','_')" />): <xsl:value-of select="description" />
    <xsl:value-of select="$newline"/>
    <xsl:value-of select="$newline"/>
</xsl:for-each>



<xsl:variable name="urlPrefixLength">
    <xsl:value-of select="string-length('${pmd.website.baseurl}/rules/')" />
</xsl:variable>


<xsl:for-each select="language">
    <xsl:variable name="language" select="@name" />
    <xsl:for-each select="ruleset">
        <xsl:variable name="rulesetname" select="translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ ', 'abcdefghijklmnopqrstuvwxyz_')" />

&lt;a name="<xsl:value-of select="translate(normalize-space(@name),' ','_')" />" />
## <xsl:value-of select="@name" /> (<xsl:value-of select="$language" />)

        <xsl:for-each select="./rule">
            <xsl:choose>
                <xsl:when test="@name and not(@ref)">
*   [<xsl:value-of select="@name" />](<xsl:value-of select="substring(@externalInfoUrl,$urlPrefixLength + 1)" />): <xsl:choose>
                        <xsl:when test="@deprecated='true'">
                            &lt;span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f; font-size: 75%;">Deprecated&lt;/span>
                        </xsl:when>
                    </xsl:choose><xsl:value-of select="description" /><xsl:value-of select="$newline"/><xsl:value-of select="$newline"/>
                </xsl:when>
                <xsl:when test="@name and @deprecated='true' and @ref and not(contains(@ref, '.xml'))">
*   [<xsl:value-of select="@name" />](<xsl:value-of select="concat($language, '/', $rulesetname, '.html#', @name)" />):
    &lt;span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f; font-size: 75%;">Deprecated&lt;/span>
    <xsl:value-of select="$newline"/>
                </xsl:when>
                <xsl:when test="not(@name) and @deprecated='true' and contains(@ref, '.xml')">
                    <xsl:variable name="rulename">
                        <xsl:call-template name="last-token-after-last-slash">
                            <xsl:with-param name="ref" select="@ref"/>
                        </xsl:call-template>
                    </xsl:variable>
*   [<xsl:value-of select="$rulename"/>](<xsl:value-of select="concat($language, '/', $rulesetname, '.html#', $rulename)" />):
    &lt;span style="border-radius: 0.25em; color: #fff; padding: 0.2em 0.6em 0.3em; display: inline; background-color: #d9534f; font-size: 75%;">Deprecated&lt;/span>
    <xsl:value-of select="$newline"/>
                </xsl:when>
            </xsl:choose>
        </xsl:for-each>
    </xsl:for-each>
</xsl:for-each>
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
</xsl:stylesheet>
