package net.sourceforge.pmd;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of languages for which a rule can be written.
 * <p/>
 * This has no 1-on-1 mapping to the SourceType enumeration, because rules will often
 * apply to all versions of a programming language, and SourceType is version-specific.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public final class Language {
    private static Map mapNameOnRuleLanguage = new HashMap();

    private static final String JSP_RULE_LANGUAGE_NAME = "jsp";
    private static final String JAVA_RULE_LANGUAGE_NAME = "java";

    public static Language JAVA = new Language(JAVA_RULE_LANGUAGE_NAME);
    public static Language JSP = new Language(JSP_RULE_LANGUAGE_NAME);


    /**
     * Get the RuleLanguage that corresponds to the given name.
     *
     * @param name the common name of the rule language; this must correspond to one of
     *             the name constants.
     * @return the corresponding RuleLanuage; or null if the name is not recognized
     */
    public static Language getByName(String name) {
        return (Language) mapNameOnRuleLanguage.get(name);
    }

    private String name;

    /**
     * Public constructor.
     *
     * @param name the common name of the rule language
     */
    private Language(String name) {
        this.name = name;
        mapNameOnRuleLanguage.put(name, this);
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj instanceof Language) {
            return ((Language) obj).getName().equals(name);
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return name.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Language [" + name + "]";
    }
}
