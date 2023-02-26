/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.findAll;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.head;
import static com.github.tomakehurst.wiremock.client.WireMock.headRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.util.ResourceLoader;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

@WireMockTest
class RuleSetReferenceIdTest {

    private static void assertRuleSetReferenceId(final boolean expectedExternal, final String expectedRuleSetFileName,
            final boolean expectedAllRules, final String expectedRuleName, final String expectedToString,
            final RuleSetReferenceId reference) {

        assertEquals(expectedExternal, reference.isExternal(), "Wrong external");
        assertEquals(expectedRuleSetFileName, reference.getRuleSetFileName(), "Wrong RuleSet file name");
        assertEquals(expectedAllRules, reference.isAllRules(), "Wrong all Rule reference");
        assertEquals(expectedRuleName, reference.getRuleName(), "Wrong Rule name");
        assertEquals(expectedToString, reference.toString(), "Wrong toString()");
    }

    @Test
    void testCommaInSingleId() {
        assertThrows(IllegalArgumentException.class, () -> new RuleSetReferenceId("bad,id"));
    }

    @Test
    void testInternalWithInternal() {
        assertThrows(IllegalArgumentException.class, () ->
            new RuleSetReferenceId("SomeRule", new RuleSetReferenceId("SomeOtherRule")));
    }

    @Test
    void testExternalWithExternal() {
        assertThrows(IllegalArgumentException.class, () ->
            new RuleSetReferenceId("someruleset.xml/SomeRule", new RuleSetReferenceId("someruleset.xml/SomeOtherRule")));
    }

    @Test
    void testExternalWithInternal() {
        assertThrows(IllegalArgumentException.class, () ->
            new RuleSetReferenceId("someruleset.xml/SomeRule", new RuleSetReferenceId("SomeOtherRule")));
    }

    @Test
    void testInteralWithExternal() {
        // This is okay
        new RuleSetReferenceId("SomeRule", new RuleSetReferenceId("someruleset.xml/SomeOtherRule"));
    }

    @Test
    void testEmptyRuleSet() {
        // This is representative of how the Test framework creates
        // RuleSetReferenceId from static RuleSet XMLs
        RuleSetReferenceId reference = new RuleSetReferenceId(null);
        assertRuleSetReferenceId(true, null, true, null, "anonymous all Rule", reference);
    }

    @Test
    void testInternalWithExternalRuleSet() {
        // This is representative of how the RuleSetFactory temporarily pairs an
        // internal reference
        // with an external reference.
        RuleSetReferenceId internalRuleSetReferenceId = new RuleSetReferenceId("MockRuleName");
        assertRuleSetReferenceId(false, null, false, "MockRuleName", "MockRuleName", internalRuleSetReferenceId);
        RuleSetReferenceId externalRuleSetReferenceId = new RuleSetReferenceId("rulesets/java/basic.xml");
        assertRuleSetReferenceId(true, "rulesets/java/basic.xml", true, null, "rulesets/java/basic.xml",
                externalRuleSetReferenceId);

        RuleSetReferenceId pairRuleSetReferenceId = new RuleSetReferenceId("MockRuleName", externalRuleSetReferenceId);
        assertRuleSetReferenceId(true, "rulesets/java/basic.xml", false, "MockRuleName",
                "rulesets/java/basic.xml/MockRuleName", pairRuleSetReferenceId);
    }

    @Test
    void testConstructorGivenHttpUrlIdSucceedsAndProcessesIdCorrectly() {

        final String sonarRulesetUrlId = "http://localhost:54321/profiles/export?format=pmd&language=java&name=Sonar%2520way";

        RuleSetReferenceId ruleSetReferenceId = new RuleSetReferenceId("  " + sonarRulesetUrlId + "  ");
        assertRuleSetReferenceId(true, sonarRulesetUrlId, true, null, sonarRulesetUrlId, ruleSetReferenceId);
    }

    @Test
    void testConstructorGivenHttpUrlInputStream(WireMockRuntimeInfo wmRuntimeInfo) throws Exception {
        String path = "/profiles/export?format=pmd&language=java&name=Sonar%2520way";
        String rulesetUrl = "http://localhost:" + wmRuntimeInfo.getHttpPort() + path;
        stubFor(head(urlEqualTo(path)).willReturn(aResponse().withStatus(200)));
        stubFor(get(urlEqualTo(path))
                .willReturn(aResponse().withStatus(200).withHeader("Content-type", "text/xml").withBody("xyz")));

        RuleSetReferenceId ruleSetReferenceId = new RuleSetReferenceId("  " + rulesetUrl + "  ");
        assertRuleSetReferenceId(true, rulesetUrl, true, null, rulesetUrl, ruleSetReferenceId);

        try (InputStream inputStream = ruleSetReferenceId.getInputStream(new ResourceLoader())) {
            String loaded = IOUtil.readToString(inputStream, StandardCharsets.UTF_8);
            assertEquals("xyz", loaded);
        }

        verify(1, headRequestedFor(urlEqualTo(path)));
        verify(0, headRequestedFor(urlEqualTo("/profiles")));
        verify(1, getRequestedFor(urlEqualTo(path)));
        assertEquals(1, findAll(headRequestedFor(urlMatching(".*"))).size());
        assertEquals(1, findAll(getRequestedFor(urlMatching(".*"))).size());
    }

    @Test
    void testConstructorGivenHttpUrlSingleRuleInputStream(WireMockRuntimeInfo wmRuntimeInfo) throws Exception {
        String path = "/profiles/export?format=pmd&language=java&name=Sonar%2520way";
        String completePath = path + "/DummyBasicMockRule";
        String hostpart = "http://localhost:" + wmRuntimeInfo.getHttpPort();
        String basicRuleSet = IOUtil
                .readToString(RuleSetReferenceId.class.getResourceAsStream("/rulesets/dummy/basic.xml"), StandardCharsets.UTF_8);

        stubFor(head(urlEqualTo(completePath)).willReturn(aResponse().withStatus(404)));
        stubFor(head(urlEqualTo(path)).willReturn(aResponse().withStatus(200).withHeader("Content-type", "text/xml")));
        stubFor(get(urlEqualTo(path))
                .willReturn(aResponse().withStatus(200).withHeader("Content-type", "text/xml").withBody(basicRuleSet)));

        RuleSetReferenceId ruleSetReferenceId = new RuleSetReferenceId("  " + hostpart + completePath + "  ");
        assertRuleSetReferenceId(true, hostpart + path, false, "DummyBasicMockRule", hostpart + completePath,
                ruleSetReferenceId);

        try (InputStream inputStream = ruleSetReferenceId.getInputStream(new ResourceLoader())) {
            String loaded = IOUtil.readToString(inputStream, StandardCharsets.UTF_8);
            assertEquals(basicRuleSet, loaded);
        }

        verify(1, headRequestedFor(urlEqualTo(completePath)));
        verify(1, headRequestedFor(urlEqualTo(path)));
        verify(1, getRequestedFor(urlEqualTo(path)));
        verify(0, getRequestedFor(urlEqualTo(completePath)));
        assertEquals(2, findAll(headRequestedFor(urlMatching(".*"))).size());
        assertEquals(1, findAll(getRequestedFor(urlMatching(".*"))).size());
    }

    @Test
    void testOneSimpleRuleSet() {

        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("dummy-basic");
        assertEquals(1, references.size());
        assertRuleSetReferenceId(true, "rulesets/dummy/basic.xml", true, null, "rulesets/dummy/basic.xml",
                references.get(0));
    }

    @Test
    void testMultipleSimpleRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("dummy-unusedcode,dummy-basic");
        assertEquals(2, references.size());
        assertRuleSetReferenceId(true, "rulesets/dummy/unusedcode.xml", true, null, "rulesets/dummy/unusedcode.xml",
                references.get(0));
        assertRuleSetReferenceId(true, "rulesets/dummy/basic.xml", true, null, "rulesets/dummy/basic.xml",
                references.get(1));
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1201/
     */
    @Test
    void testMultipleRulesWithSpaces() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("dummy-basic, dummy-unusedcode, dummy2-basic");
        assertEquals(3, references.size());
        assertRuleSetReferenceId(true, "rulesets/dummy/basic.xml", true, null, "rulesets/dummy/basic.xml",
                references.get(0));
        assertRuleSetReferenceId(true, "rulesets/dummy/unusedcode.xml", true, null, "rulesets/dummy/unusedcode.xml",
                references.get(1));
        assertRuleSetReferenceId(true, "rulesets/dummy2/basic.xml", true, null, "rulesets/dummy2/basic.xml",
                references.get(2));
    }

    @Test
    void testOneReleaseRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("50");
        assertEquals(1, references.size());
        assertRuleSetReferenceId(true, "rulesets/releases/50.xml", true, null, "rulesets/releases/50.xml",
                references.get(0));
    }

    @Test
    void testOneFullRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("rulesets/java/unusedcode.xml");
        assertEquals(1, references.size());
        assertRuleSetReferenceId(true, "rulesets/java/unusedcode.xml", true, null, "rulesets/java/unusedcode.xml",
                references.get(0));
    }

    @Test
    void testOneFullRuleSetURL() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("file://somepath/rulesets/java/unusedcode.xml");
        assertEquals(1, references.size());
        assertRuleSetReferenceId(true, "file://somepath/rulesets/java/unusedcode.xml", true, null,
                "file://somepath/rulesets/java/unusedcode.xml", references.get(0));
    }

    @Test
    void testMultipleFullRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId
                .parse("rulesets/java/unusedcode.xml,rulesets/java/basic.xml");
        assertEquals(2, references.size());
        assertRuleSetReferenceId(true, "rulesets/java/unusedcode.xml", true, null, "rulesets/java/unusedcode.xml",
                references.get(0));
        assertRuleSetReferenceId(true, "rulesets/java/basic.xml", true, null, "rulesets/java/basic.xml",
                references.get(1));
    }

    @Test
    void testMixRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("rulesets/dummy/unusedcode.xml,dummy2-basic");
        assertEquals(2, references.size());
        assertRuleSetReferenceId(true, "rulesets/dummy/unusedcode.xml", true, null, "rulesets/dummy/unusedcode.xml",
                references.get(0));
        assertRuleSetReferenceId(true, "rulesets/dummy2/basic.xml", true, null, "rulesets/dummy2/basic.xml",
                references.get(1));
    }

    @Test
    void testUnknownRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("nonexistant.xml");
        assertEquals(1, references.size());
        assertRuleSetReferenceId(true, "nonexistant.xml", true, null, "nonexistant.xml", references.get(0));
    }

    @Test
    void testUnknownAndSimpleRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("dummy-basic,nonexistant.xml");
        assertEquals(2, references.size());
        assertRuleSetReferenceId(true, "rulesets/dummy/basic.xml", true, null, "rulesets/dummy/basic.xml",
                references.get(0));
        assertRuleSetReferenceId(true, "nonexistant.xml", true, null, "nonexistant.xml", references.get(1));
    }

    @Test
    void testSimpleRuleSetAndRule() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("dummy-basic/DummyBasicMockRule");
        assertEquals(1, references.size());
        assertRuleSetReferenceId(true, "rulesets/dummy/basic.xml", false, "DummyBasicMockRule",
                "rulesets/dummy/basic.xml/DummyBasicMockRule", references.get(0));
    }

    @Test
    void testFullRuleSetAndRule() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("rulesets/java/basic.xml/EmptyCatchBlock");
        assertEquals(1, references.size());
        assertRuleSetReferenceId(true, "rulesets/java/basic.xml", false, "EmptyCatchBlock",
                "rulesets/java/basic.xml/EmptyCatchBlock", references.get(0));
    }

    @Test
    void testFullRuleSetURLAndRule() {
        List<RuleSetReferenceId> references = RuleSetReferenceId
                .parse("file://somepath/rulesets/java/unusedcode.xml/EmptyCatchBlock");
        assertEquals(1, references.size());
        assertRuleSetReferenceId(true, "file://somepath/rulesets/java/unusedcode.xml", false, "EmptyCatchBlock",
                "file://somepath/rulesets/java/unusedcode.xml/EmptyCatchBlock", references.get(0));
    }

    @Test
    void testInternalRuleSetAndRule() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("EmptyCatchBlock");
        assertEquals(1, references.size());
        assertRuleSetReferenceId(false, null, false, "EmptyCatchBlock", "EmptyCatchBlock", references.get(0));
    }

    @Test
    void testRelativePathRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("pmd/pmd-ruleset.xml");
        assertEquals(1, references.size());
        assertRuleSetReferenceId(true, "pmd/pmd-ruleset.xml", true, null, "pmd/pmd-ruleset.xml", references.get(0));
    }

    @Test
    void testAbsolutePathRuleSet() {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse("/home/foo/pmd/pmd-ruleset.xml");
        assertEquals(1, references.size());
        assertRuleSetReferenceId(true, "/home/foo/pmd/pmd-ruleset.xml", true, null, "/home/foo/pmd/pmd-ruleset.xml",
                references.get(0));
    }

    @Test
    void testFooRules() throws Exception {
        String fooRulesFile = new File("./src/test/resources/net/sourceforge/pmd/rulesets/foo-project/foo-rules")
                .getCanonicalPath();
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse(fooRulesFile);
        assertEquals(1, references.size());
        assertRuleSetReferenceId(true, fooRulesFile, true, null, fooRulesFile, references.get(0));
    }

    @Test
    void testNullRulesetString() throws Exception {
        List<RuleSetReferenceId> references = RuleSetReferenceId.parse(null);
        assertTrue(references.isEmpty());
    }
}
