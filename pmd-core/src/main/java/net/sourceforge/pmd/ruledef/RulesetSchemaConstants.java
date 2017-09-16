/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ruledef;

/**
 * Stores the string constants corresponding to attribute and node names of the ruleset schema. Prevents string literal
 * copy-paste.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public enum RulesetSchemaConstants {
    RULE("rule"),
    PROPERTIES("properties"),
    EXAMPLE("example"),
    PROPERTY("property"),
    PRIORITY("priority"),
    DESCRIPTION("description"),
    DEPRECATED("deprecated"),
    EXTERNAL_INFO_URL("externalInfoUrl"),
    MESSAGE("message"),
    CLASS("class"),
    LANGUAGE("language"),
    MINIMUM_LANGUAGE_VERSION("minimumLanguageVersion"),
    MAXIMUM_LANGUAGE_VERSION("maximumLanguageVersion"),
    SINCE("since"),
    DFA("dfa"),
    TYPERESOLUTION("typeResolution"),
    METRICS("metrics"),
    VALUE("value"),
    NAME("name");

    public final String name;


    RulesetSchemaConstants(String name) {
        this.name = name;
    }

}
