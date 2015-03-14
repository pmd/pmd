<?xml version="1.0" encoding="UTF-8"?>
<!-- BSD-style license; for more info see http://pmd.sourceforge.net/license.html -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" encoding="UTF-8" indent="yes" />

    <!-- FUTURE: Externalising text to allow i18n documnetation -->
    <xsl:variable name="Title" select="'PMD Rulesets index'" />
    <xsl:variable name="PageDesc" select="'List of rulesets and rules contained in each ruleset.'" />

    <xsl:template match="rulesets">
        <document>
            <properties>
                <author email="mikkey@sourceforge.net">Miguel Griffa</author>
                <title>
                    <xsl:value-of select="$Title" />
                </title>
            </properties>
            <body>
                <section name="Current Rulesets">
                    <p>
                        <xsl:value-of select="$PageDesc" />
                    </p>
                    <ul>
                        <xsl:for-each select="./language/ruleset">
                            <xsl:sort select="@name" />
                            <li>
                                <a>
                                    <xsl:attribute name="href">#<xsl:value-of
                                        select="translate(normalize-space(@name),' ','_')" /></xsl:attribute>
                                    <xsl:value-of select="@name" />
                                </a>: <xsl:value-of select="description" />
                            </li>
                        </xsl:for-each>
                    </ul>

                    <xsl:variable name="urlPrefixLength">
                        <xsl:value-of select="string-length('${pmd.website.baseurl}/rules/')" />
                    </xsl:variable>

                    <xsl:for-each select="language">
                        <xsl:variable name="language" select="@name" />
                        <xsl:for-each select="ruleset">
                            <xsl:variable name="rulesetname" select="translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ ', 'abcdefghijklmnopqrstuvwxyz_')" />
                            <xsl:element name="a">
                                <xsl:attribute name="name">
                                    <xsl:value-of select="translate(normalize-space(@name),' ','_')" />
                                </xsl:attribute>
                            </xsl:element>
                            <subsection>
                                <xsl:attribute name="name"><xsl:value-of select="@name" /> (<xsl:value-of
                                    select="$language" />)</xsl:attribute>
                                <ul>
                                    <xsl:for-each select="./rule">
                                        <li>
                                            <xsl:choose>
                                                <xsl:when test="@name and (not(@deprecated) or @deprecated='false')">
                                                    <a>
                                                        <xsl:attribute name="href"><xsl:value-of
                                                            select="substring(@externalInfoUrl,$urlPrefixLength + 1)" /></xsl:attribute>
                                                        <xsl:value-of select="@name" />
                                                    </a>: <xsl:value-of select="description" />
                                                </xsl:when>
                                                <xsl:when test="@name and @deprecated='true' and @ref and not(contains(@ref, '.xml'))">
                                                    <a>
                                                        <xsl:attribute name="href"><xsl:value-of select="concat($language, '/', $rulesetname, '.html#', @name)" /></xsl:attribute>
                                                        <xsl:value-of select="@name" />
                                                    </a>: Deprecated rule.
                                                </xsl:when>
                                                <xsl:when test="not(@name) and @deprecated='true' and contains(@ref, '.xml')">
                                                    <xsl:variable name="rulename">
                                                        <xsl:call-template name="last-token-after-last-slash">
                                                            <xsl:with-param name="ref" select="@ref"/>
                                                        </xsl:call-template>
                                                    </xsl:variable>
                                                    <a>
                                                        <xsl:attribute name="href"><xsl:value-of select="concat($language, '/', $rulesetname, '.html#', $rulename)" /></xsl:attribute>
                                                        <xsl:value-of select="$rulename"/>
                                                    </a>: Deprecated rule.
                                                </xsl:when>
                                            </xsl:choose>
                                        </li>
                                    </xsl:for-each>
                                </ul>
                            </subsection>
                        </xsl:for-each>
                    </xsl:for-each>
                </section>
            </body>
        </document>
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
