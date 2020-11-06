/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath;

import java.util.HashMap;
import java.util.Map;

/**
 * Constants for XPath language version used in XPath queries.
 */
public enum XPathVersion {
    /**
     * XPath 1.0.
     *
     * @deprecated Will become unsupported in 7.0.0
     */
    @Deprecated
    XPATH_1_0(XPathRuleQuery.XPATH_1_0),

    /**
     * XPath 1.0 compatibility mode.
     *
     * @deprecated Will become unsupported in 7.0.0
     */
    @Deprecated
    XPATH_1_0_COMPATIBILITY(XPathRuleQuery.XPATH_1_0_COMPATIBILITY),

    /** XPath 2.0. */
    XPATH_2_0(XPathRuleQuery.XPATH_2_0);

    private static final Map<String, XPathVersion> BY_NAME = new HashMap<>();
    private final String version;


    static {
        for (XPathVersion value : values()) {
            BY_NAME.put(value.getXmlName(), value);
        }
    }


    XPathVersion(String version) {
        this.version = version;
    }


    /**
     * Returns the string used to represent the version in the XML.
     *
     * @return A string representation
     */
    public String getXmlName() {
        return version;
    }


    /**
     * Gets an XPath version from the string used to represent
     * it in the XML.
     *
     * @param version A version string
     *
     * @return An XPath version, or null if the argument is not a valid version
     */
    public static XPathVersion ofId(String version) {
        return BY_NAME.get(version);
    }
}
