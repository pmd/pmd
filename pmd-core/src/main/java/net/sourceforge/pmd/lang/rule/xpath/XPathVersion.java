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
     * @deprecated not supported anymore
     */
    @Deprecated
    XPATH_1_0("1.0"),
    /**
     * XPath 1.0 compatibility mode.
     *
     * @deprecated Not supported any more.
     */
    @Deprecated
    XPATH_1_0_COMPATIBILITY("1.0 compatibility"),

    /**
     * XPath 2.0.
     *
     * @deprecated Technically still supported, use 3.1 instead. There
     *     are no known incompatibilities.
     */
    @Deprecated
    XPATH_2_0("2.0"),
    /** XPath 3.1. */
    XPATH_3_1("3.1");


    /**
     * The default XPath version for XPath queries.
     */
    public static final XPathVersion DEFAULT = XPATH_3_1;

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

    @Override
    public String toString() {
        return getXmlName();
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
