package net.sourceforge.pmd;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapping of SourceType to RuleLanguage.
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 *
 */
public class SourceTypeToRuleLanguageMapper {
	/**
	 * Map of SourceType on RuleLanguage.
	 */
	private static Map mapSourceTypeOnRuleLanguage = new HashMap();
	
	static {
		mapSourceTypeOnRuleLanguage.put( SourceType.JAVA_13, Language.JAVA );
		mapSourceTypeOnRuleLanguage.put( SourceType.JAVA_14, Language.JAVA );
		mapSourceTypeOnRuleLanguage.put( SourceType.JAVA_15, Language.JAVA );
		
		mapSourceTypeOnRuleLanguage.put( SourceType.JSP, Language.JSP );
	}
	
	/**
	 * Get the RuleLanguage that corresponds to the given SourceType.
	 * @param sourceType the SourceType
	 * @return a RuleLanguage
	 */
	public static Language getMappedLanguage(SourceType sourceType) {
		return (Language) mapSourceTypeOnRuleLanguage.get(sourceType);
	}
}
