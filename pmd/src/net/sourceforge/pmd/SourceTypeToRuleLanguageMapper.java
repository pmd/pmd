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
    private static Map mapSourceTypeOnRuleLanguage = CollectionUtil.mapFrom( new Object[][] {
    	{ SourceType.JAVA_13, Language.JAVA },
		{ SourceType.JAVA_14, Language.JAVA },
		{ SourceType.JAVA_15, Language.JAVA },
		{ SourceType.JAVA_16, Language.JAVA },
		{ SourceType.JSP, Language.JSP },
    	});

    private SourceTypeToRuleLanguageMapper() {};
    /**
     * Get the RuleLanguage that corresponds to the given SourceType.
     *
     * @param sourceType the SourceType
     * @return a RuleLanguage
     */
    public static Language getMappedLanguage(SourceType sourceType) {
        return (Language) mapSourceTypeOnRuleLanguage.get(sourceType);
    }
}
