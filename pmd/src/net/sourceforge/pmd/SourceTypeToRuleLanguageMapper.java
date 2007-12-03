package net.sourceforge.pmd;

import java.util.Map;

import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Mapping of SourceType to RuleLanguage.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class SourceTypeToRuleLanguageMapper {
    /**
     * Map of SourceType on RuleLanguage.
     */
    private static Map<SourceType, Language> mapSourceTypeOnRuleLanguage = CollectionUtil.mapFrom(
            new SourceType[] { SourceType.JAVA_13, SourceType.JAVA_14,
                    SourceType.JAVA_15, SourceType.JAVA_16, SourceType.JAVA_17, SourceType.JSP, },
            new Language[] { Language.JAVA, Language.JAVA, Language.JAVA,
                    Language.JAVA, Language.JAVA, Language.JSP, });

    private SourceTypeToRuleLanguageMapper() {};
    /**
     * Get the RuleLanguage that corresponds to the given SourceType.
     * 
     * @param sourceType
     *            the SourceType
     * @return a RuleLanguage
     */
    public static Language getMappedLanguage(SourceType sourceType) {
        return mapSourceTypeOnRuleLanguage.get(sourceType);
    }
}
