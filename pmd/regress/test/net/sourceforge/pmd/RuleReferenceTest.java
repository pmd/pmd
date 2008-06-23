package test.net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import junit.framework.JUnit4TestAdapter;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSetReference;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.RuleReference;

import org.junit.Test;

public class RuleReferenceTest {

	@Test
	public void testRuleSetReference() {
		RuleReference ruleReference = new RuleReference();
		RuleSetReference ruleSetReference = new RuleSetReference();
		ruleReference.setRuleSetReference(ruleSetReference);
		assertEquals("Not same rule set reference", ruleSetReference, ruleReference.getRuleSetReference());
	}

	@Test
	public void testOverride() {
		MockRule rule = new MockRule();
		rule.setLanguage(Language.XML);
		rule.setName("name1");
		rule.addProperty("property1", "value1");
		rule.setMessage("message1");
		rule.setDescription("description1");
		rule.addExample("example1");
		rule.setExternalInfoUrl("externalInfoUrl1");
		rule.setPriority(RulePriority.HIGH);

		RuleReference ruleReference = new RuleReference();
		ruleReference.setRule(rule);
		ruleReference.setLanguage(Language.JAVA);
		ruleReference.setMinimumLanguageVersion(LanguageVersion.JAVA_13);
		ruleReference.setMaximumLanguageVersion(LanguageVersion.JAVA_17);
		ruleReference.setDeprecated(true);
		ruleReference.setName("name2");
		ruleReference.addProperty("property1", "value2");
		ruleReference.setMessage("message2");
		ruleReference.setDescription("description2");
		ruleReference.addExample("example2");
		ruleReference.setExternalInfoUrl("externalInfoUrl2");
		ruleReference.setPriority(RulePriority.MEDIUM_HIGH);

		assertEquals("Override failed", Language.JAVA, ruleReference.getLanguage());
		assertEquals("Override failed", Language.JAVA, ruleReference.getOverriddenLanguage());

		assertEquals("Override failed", LanguageVersion.JAVA_13, ruleReference.getMinimumLanguageVersion());
		assertEquals("Override failed", LanguageVersion.JAVA_13, ruleReference.getOverriddenMinimumLanguageVersion());

		assertEquals("Override failed", LanguageVersion.JAVA_17, ruleReference.getMaximumLanguageVersion());
		assertEquals("Override failed", LanguageVersion.JAVA_17, ruleReference.getOverriddenMaximumLanguageVersion());

		assertEquals("Override failed", false, ruleReference.getRule().isDeprecated());
		assertEquals("Override failed", true, ruleReference.isDeprecated());
		assertEquals("Override failed", true, ruleReference.isOverriddenDeprecated());

		assertEquals("Override failed", "name2", ruleReference.getName());
		assertEquals("Override failed", "name2", ruleReference.getOverriddenName());

		assertEquals("Override failed", "value2", ruleReference.getProperties().getProperty("property1"));
		assertEquals("Override failed", "value2", ruleReference.getOverriddenProperties().getProperty("property1"));

		assertEquals("Override failed", "message2", ruleReference.getMessage());
		assertEquals("Override failed", "message2", ruleReference.getOverriddenMessage());

		assertEquals("Override failed", "description2", ruleReference.getDescription());
		assertEquals("Override failed", "description2", ruleReference.getOverriddenDescription());

		// TODO Examples is being tested as currently working, but needs to be fixed at some point
		assertEquals("Override failed", "example2", ruleReference.getExample());
		assertEquals("Override failed", 2, ruleReference.getExamples().size());
		assertEquals("Override failed", "example1", ruleReference.getExamples().get(0));
		assertEquals("Override failed", "example2", ruleReference.getExamples().get(1));
		assertEquals("Override failed", "example2", ruleReference.getOverriddenExamples().get(0));

		assertEquals("Override failed", "externalInfoUrl2", ruleReference.getExternalInfoUrl());
		assertEquals("Override failed", "externalInfoUrl2", ruleReference.getOverriddenExternalInfoUrl());

		assertEquals("Override failed", RulePriority.MEDIUM_HIGH, ruleReference.getPriority());
		assertEquals("Override failed", RulePriority.MEDIUM_HIGH, ruleReference.getOverriddenPriority());
	}

	@Test
	public void testNotOverride() {
		MockRule rule = new MockRule();
		rule.setLanguage(Language.JAVA);
		rule.setMinimumLanguageVersion(LanguageVersion.JAVA_13);
		rule.setMaximumLanguageVersion(LanguageVersion.JAVA_17);
		rule.setName("name1");
		rule.addProperty("property1", "value1");
		rule.setMessage("message1");
		rule.setDescription("description1");
		rule.addExample("example1");
		rule.setExternalInfoUrl("externalInfoUrl1");
		rule.setPriority(RulePriority.HIGH);

		RuleReference ruleReference = new RuleReference();
		ruleReference.setRule(rule);
		ruleReference.setLanguage(Language.JAVA);
		ruleReference.setMinimumLanguageVersion(LanguageVersion.JAVA_13);
		ruleReference.setMaximumLanguageVersion(LanguageVersion.JAVA_17);
		ruleReference.setDeprecated(false);
		ruleReference.setName("name1");
		ruleReference.addProperty("property1", "value1");
		ruleReference.setMessage("message1");
		ruleReference.setDescription("description1");
		ruleReference.addExample("example1");
		ruleReference.setExternalInfoUrl("externalInfoUrl1");
		ruleReference.setPriority(RulePriority.HIGH);

		assertEquals("Override failed", Language.JAVA, ruleReference.getLanguage());
		assertNull("Override failed", ruleReference.getOverriddenLanguage());

		assertEquals("Override failed", LanguageVersion.JAVA_13, ruleReference.getMinimumLanguageVersion());
		assertNull("Override failed", ruleReference.getOverriddenMinimumLanguageVersion());

		assertEquals("Override failed", LanguageVersion.JAVA_17, ruleReference.getMaximumLanguageVersion());
		assertNull("Override failed", ruleReference.getOverriddenMaximumLanguageVersion());

		assertEquals("Override failed", false, ruleReference.isDeprecated());
		assertNull("Override failed", ruleReference.isOverriddenDeprecated());

		assertEquals("Override failed", "name1", ruleReference.getName());
		assertNull("Override failed", ruleReference.getOverriddenName());

		assertEquals("Override failed", "value1", ruleReference.getProperties().getProperty("property1"));
		assertNull("Override failed", ruleReference.getOverriddenProperties());

		assertEquals("Override failed", "message1", ruleReference.getMessage());
		assertNull("Override failed", ruleReference.getOverriddenMessage());

		assertEquals("Override failed", "description1", ruleReference.getDescription());
		assertNull("Override failed", ruleReference.getOverriddenDescription());

		assertEquals("Override failed", "example1", ruleReference.getExample());
		assertEquals("Override failed", 1, ruleReference.getExamples().size());
		assertEquals("Override failed", "example1", ruleReference.getExamples().get(0));
		assertNull("Override failed", ruleReference.getOverriddenExamples());

		assertEquals("Override failed", "externalInfoUrl1", ruleReference.getExternalInfoUrl());
		assertNull("Override failed", ruleReference.getOverriddenExternalInfoUrl());

		assertEquals("Override failed", RulePriority.HIGH, ruleReference.getPriority());
		assertNull("Override failed", ruleReference.getOverriddenPriority());
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(RuleReferenceTest.class);
	}
}
