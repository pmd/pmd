/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sourceforge.pmd.lang.Dummy2LanguageModule;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.properties.StringProperty;

public class RuleReferenceTest {

    @Test
    public void testRuleSetReference() {
        RuleReference ruleReference = new RuleReference();
        RuleSetReference ruleSetReference = new RuleSetReference("somename");
        ruleReference.setRuleSetReference(ruleSetReference);
        assertEquals("Not same rule set reference", ruleSetReference, ruleReference.getRuleSetReference());
    }

    @Test
    public void testOverride() {
        final StringProperty PROPERTY1_DESCRIPTOR = new StringProperty("property1", "Test property", null, 0f);
        MockRule rule = new MockRule();
        rule.definePropertyDescriptor(PROPERTY1_DESCRIPTOR);
        rule.setLanguage(LanguageRegistry.getLanguage(Dummy2LanguageModule.NAME));
        rule.setName("name1");
        rule.setProperty(PROPERTY1_DESCRIPTOR, "value1");
        rule.setMessage("message1");
        rule.setDescription("description1");
        rule.addExample("example1");
        rule.setExternalInfoUrl("externalInfoUrl1");
        rule.setPriority(RulePriority.HIGH);

        final StringProperty PROPERTY2_DESCRIPTOR = new StringProperty("property2", "Test property", null, 0f);
        RuleReference ruleReference = new RuleReference();
        ruleReference.setRule(rule);
        ruleReference.definePropertyDescriptor(PROPERTY2_DESCRIPTOR);
        ruleReference.setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        ruleReference
                .setMinimumLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.3"));
        ruleReference
                .setMaximumLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.7"));
        ruleReference.setDeprecated(true);
        ruleReference.setName("name2");
        ruleReference.setProperty(PROPERTY1_DESCRIPTOR, "value2");
        ruleReference.setProperty(PROPERTY2_DESCRIPTOR, "value3");
        ruleReference.setMessage("message2");
        ruleReference.setDescription("description2");
        ruleReference.addExample("example2");
        ruleReference.setExternalInfoUrl("externalInfoUrl2");
        ruleReference.setPriority(RulePriority.MEDIUM_HIGH);

        validateOverridenValues(PROPERTY1_DESCRIPTOR, PROPERTY2_DESCRIPTOR, ruleReference);
    }

    @Test
    public void testDeepCopyOverride() {
        final StringProperty PROPERTY1_DESCRIPTOR = new StringProperty("property1", "Test property", null, 0f);
        MockRule rule = new MockRule();
        rule.definePropertyDescriptor(PROPERTY1_DESCRIPTOR);
        rule.setLanguage(LanguageRegistry.getLanguage(Dummy2LanguageModule.NAME));
        rule.setName("name1");
        rule.setProperty(PROPERTY1_DESCRIPTOR, "value1");
        rule.setMessage("message1");
        rule.setDescription("description1");
        rule.addExample("example1");
        rule.setExternalInfoUrl("externalInfoUrl1");
        rule.setPriority(RulePriority.HIGH);

        final StringProperty PROPERTY2_DESCRIPTOR = new StringProperty("property2", "Test property", null, 0f);
        RuleReference ruleReference = new RuleReference();
        ruleReference.setRule(rule);
        ruleReference.definePropertyDescriptor(PROPERTY2_DESCRIPTOR);
        ruleReference.setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        ruleReference
                .setMinimumLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.3"));
        ruleReference
                .setMaximumLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.7"));
        ruleReference.setDeprecated(true);
        ruleReference.setName("name2");
        ruleReference.setProperty(PROPERTY1_DESCRIPTOR, "value2");
        ruleReference.setProperty(PROPERTY2_DESCRIPTOR, "value3");
        ruleReference.setMessage("message2");
        ruleReference.setDescription("description2");
        ruleReference.addExample("example2");
        ruleReference.setExternalInfoUrl("externalInfoUrl2");
        ruleReference.setPriority(RulePriority.MEDIUM_HIGH);

        validateOverridenValues(PROPERTY1_DESCRIPTOR, PROPERTY2_DESCRIPTOR, (RuleReference) ruleReference.deepCopy());
    }

    private void validateOverridenValues(final StringProperty propertyDescriptor1,
            final StringProperty propertyDescriptor2, RuleReference ruleReference) {
        assertEquals("Override failed", LanguageRegistry.getLanguage(DummyLanguageModule.NAME),
                ruleReference.getLanguage());
        assertEquals("Override failed", LanguageRegistry.getLanguage(DummyLanguageModule.NAME),
                ruleReference.getOverriddenLanguage());

        assertEquals("Override failed", LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.3"),
                ruleReference.getMinimumLanguageVersion());
        assertEquals("Override failed", LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.3"),
                ruleReference.getOverriddenMinimumLanguageVersion());

        assertEquals("Override failed", LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.7"),
                ruleReference.getMaximumLanguageVersion());
        assertEquals("Override failed", LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.7"),
                ruleReference.getOverriddenMaximumLanguageVersion());

        assertEquals("Override failed", false, ruleReference.getRule().isDeprecated());
        assertEquals("Override failed", true, ruleReference.isDeprecated());
        assertEquals("Override failed", true, ruleReference.isOverriddenDeprecated());

        assertEquals("Override failed", "name2", ruleReference.getName());
        assertEquals("Override failed", "name2", ruleReference.getOverriddenName());

        assertEquals("Override failed", "value2", ruleReference.getProperty(propertyDescriptor1));
        assertEquals("Override failed", "value3", ruleReference.getProperty(propertyDescriptor2));
        assertTrue("Override failed", ruleReference.getPropertyDescriptors().contains(propertyDescriptor1));
        assertTrue("Override failed", ruleReference.getPropertyDescriptors().contains(propertyDescriptor2));
        assertFalse("Override failed", ruleReference.getOverriddenPropertyDescriptors().contains(propertyDescriptor1));
        assertTrue("Override failed", ruleReference.getOverriddenPropertyDescriptors().contains(propertyDescriptor2));
        assertTrue("Override failed",
                ruleReference.getPropertiesByPropertyDescriptor().containsKey(propertyDescriptor1));
        assertTrue("Override failed",
                ruleReference.getPropertiesByPropertyDescriptor().containsKey(propertyDescriptor2));
        assertTrue("Override failed",
                ruleReference.getOverriddenPropertiesByPropertyDescriptor().containsKey(propertyDescriptor1));
        assertTrue("Override failed",
                ruleReference.getOverriddenPropertiesByPropertyDescriptor().containsKey(propertyDescriptor2));

        assertEquals("Override failed", "message2", ruleReference.getMessage());
        assertEquals("Override failed", "message2", ruleReference.getOverriddenMessage());

        assertEquals("Override failed", "description2", ruleReference.getDescription());
        assertEquals("Override failed", "description2", ruleReference.getOverriddenDescription());

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
        final StringProperty PROPERTY1_DESCRIPTOR = new StringProperty("property1", "Test property", null, 0f);
        MockRule rule = new MockRule();
        rule.definePropertyDescriptor(PROPERTY1_DESCRIPTOR);
        rule.setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        rule.setMinimumLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.3"));
        rule.setMaximumLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.7"));
        rule.setName("name1");
        rule.setProperty(PROPERTY1_DESCRIPTOR, "value1");
        rule.setMessage("message1");
        rule.setDescription("description1");
        rule.addExample("example1");
        rule.setExternalInfoUrl("externalInfoUrl1");
        rule.setPriority(RulePriority.HIGH);

        RuleReference ruleReference = new RuleReference();
        ruleReference.setRule(rule);
        ruleReference.setLanguage(LanguageRegistry.getLanguage(DummyLanguageModule.NAME));
        ruleReference
                .setMinimumLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.3"));
        ruleReference
                .setMaximumLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.7"));
        ruleReference.setDeprecated(false);
        ruleReference.setName("name1");
        ruleReference.setProperty(PROPERTY1_DESCRIPTOR, "value1");
        ruleReference.setMessage("message1");
        ruleReference.setDescription("description1");
        ruleReference.addExample("example1");
        ruleReference.setExternalInfoUrl("externalInfoUrl1");
        ruleReference.setPriority(RulePriority.HIGH);

        assertEquals("Override failed", LanguageRegistry.getLanguage(DummyLanguageModule.NAME),
                ruleReference.getLanguage());
        assertNull("Override failed", ruleReference.getOverriddenLanguage());

        assertEquals("Override failed", LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.3"),
                ruleReference.getMinimumLanguageVersion());
        assertNull("Override failed", ruleReference.getOverriddenMinimumLanguageVersion());

        assertEquals("Override failed", LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getVersion("1.7"),
                ruleReference.getMaximumLanguageVersion());
        assertNull("Override failed", ruleReference.getOverriddenMaximumLanguageVersion());

        assertEquals("Override failed", false, ruleReference.isDeprecated());
        assertNull("Override failed", ruleReference.isOverriddenDeprecated());

        assertEquals("Override failed", "name1", ruleReference.getName());
        assertNull("Override failed", ruleReference.getOverriddenName());

        assertEquals("Override failed", "value1", ruleReference.getProperty(PROPERTY1_DESCRIPTOR));

        assertEquals("Override failed", "message1", ruleReference.getMessage());
        assertNull("Override failed", ruleReference.getOverriddenMessage());

        assertEquals("Override failed", "description1", ruleReference.getDescription());
        assertNull("Override failed", ruleReference.getOverriddenDescription());

        assertEquals("Override failed", 1, ruleReference.getExamples().size());
        assertEquals("Override failed", "example1", ruleReference.getExamples().get(0));
        assertNull("Override failed", ruleReference.getOverriddenExamples());

        assertEquals("Override failed", "externalInfoUrl1", ruleReference.getExternalInfoUrl());
        assertNull("Override failed", ruleReference.getOverriddenExternalInfoUrl());

        assertEquals("Override failed", RulePriority.HIGH, ruleReference.getPriority());
        assertNull("Override failed", ruleReference.getOverriddenPriority());
    }
}
