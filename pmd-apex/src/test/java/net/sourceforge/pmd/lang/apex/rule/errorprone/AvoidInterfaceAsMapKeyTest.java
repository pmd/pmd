/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.errorprone;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.apex.ApexLanguageProperties;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.RuleSet;
import net.sourceforge.pmd.lang.rule.RuleSetLoader;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.RuleViolation;
import net.sourceforge.pmd.test.PmdRuleTst;

import com.nawforce.pkgforce.path.PathLike;
import com.nawforce.runtime.platform.Environment;
import scala.Option;

/**
 * Tests for AvoidInterfaceAsMapKey rule. Extends PmdRuleTst for XML-based tests
 * and adds integration tests that require multifile analysis.
 */
class AvoidInterfaceAsMapKeyTest extends PmdRuleTst {

    private static final String TEST_RESOURCES_BASE =
            "src/test/resources/net/sourceforge/pmd/lang/apex/rule/errorprone/AvoidInterfaceAsMapKey/";

    @TempDir
    private Path tempDir;

    /**
     * project1: Interface + abstract implementor with BOTH equals AND hashCode.
     * Expected: 3 violations (field, parameter, local variable).
     */
    @Test
    void abstractImplementorWithBothEqualsAndHashCodeViolations() throws Exception {
        Report report = runRule(Paths.get(TEST_RESOURCES_BASE + "project1"));

        assertEquals(3, report.getViolations().size(), "Expected 3 violations in MapUser.cls");
        for (RuleViolation v : report.getViolations()) {
            assertEquals("MapUser.cls", v.getFileId().getFileName());
        }
        assertViolation(report.getViolations().get(0), "MapUser.cls", 7);  // field
        assertViolation(report.getViolations().get(1), "MapUser.cls", 10); // parameter
        assertViolation(report.getViolations().get(2), "MapUser.cls", 15); // local variable
    }

    /**
     * project2: Interface with NO implementors at all.
     * Expected: 0 violations.
     */
    @Test
    void noImplementorsNoViolations() throws Exception {
        Report report = runRule(Paths.get(TEST_RESOURCES_BASE + "project2"));
        assertEquals(0, report.getViolations().size(), "Expected no violations when interface has no implementors");
    }

    /**
     * project3: Abstract implementor WITHOUT equals or hashCode.
     * Expected: 0 violations.
     */
    @Test
    void abstractImplementorWithoutEqualsOrHashCodeNoViolations() throws Exception {
        Report report = runRule(Paths.get(TEST_RESOURCES_BASE + "project3"));
        assertEquals(0, report.getViolations().size(),
                "Expected no violations when abstract implementor doesn't define equals/hashCode");
    }

    /**
     * project4: Abstract implementor with ONLY equals (no hashCode).
     * Expected: 1 violation - dispatch bug still occurs with just equals.
     */
    @Test
    void abstractImplementorWithOnlyEqualsViolation() throws Exception {
        Report report = runRule(Paths.get(TEST_RESOURCES_BASE + "project4"));
        assertEquals(1, report.getViolations().size(),
                "Expected 1 violation when abstract implementor defines only equals");
        assertViolation(report.getViolations().get(0), "MapUser.cls", 7);
    }

    /**
     * project5: Abstract implementor with ONLY hashCode (no equals).
     * Expected: 1 violation - dispatch bug still occurs with just hashCode.
     */
    @Test
    void abstractImplementorWithOnlyHashCodeViolation() throws Exception {
        Report report = runRule(Paths.get(TEST_RESOURCES_BASE + "project5"));
        assertEquals(1, report.getViolations().size(),
                "Expected 1 violation when abstract implementor defines only hashCode");
        assertViolation(report.getViolations().get(0), "MapUser.cls", 7);
    }

    /**
     * project6: Abstract implementor WITHOUT equals/hashCode, but CONCRETE subclass defines them.
     * Expected: 1 violation - dispatch bug occurs because abstract class is in the chain.
     */
    @Test
    void concreteSubclassDefinesEqualsHashCodeViolation() throws Exception {
        Report report = runRule(Paths.get(TEST_RESOURCES_BASE + "project6"));
        assertEquals(1, report.getViolations().size(),
                "Expected 1 violation when concrete subclass of abstract implementor defines equals/hashCode");
        assertViolation(report.getViolations().get(0), "MapUser.cls", 7);
    }

    /**
     * project7: All types (interface, abstract, concrete) are INNER classes within a single outer class.
     * Expected: 3 violations - multifile analysis should handle nested types.
     */
    @Test
    void innerClassesViolation() throws Exception {
        Report report = runRule(Paths.get(TEST_RESOURCES_BASE + "project7"));
        assertEquals(3, report.getViolations().size(),
                "Expected 3 violations when interface and abstract implementor are inner classes");
        assertViolation(report.getViolations().get(0), "OuterClass.cls", 29); // field
        assertViolation(report.getViolations().get(1), "OuterClass.cls", 31); // parameter
        assertViolation(report.getViolations().get(2), "OuterClass.cls", 32); // local variable
    }

    /**
     * project8: Indirect implementation via superclass.
     * Hierarchy: IKey <- BaseKey (virtual concrete) <- MidKey (abstract with equals)
     * Expected: 1 violation - abstract class indirectly implements the interface.
     */
    @Test
    void indirectImplementationViaSuperclassViolation() throws Exception {
        Report report = runRule(Paths.get(TEST_RESOURCES_BASE + "project8"));
        assertEquals(1, report.getViolations().size(),
                "Expected 1 violation when abstract class indirectly implements interface via superclass");
        assertViolation(report.getViolations().get(0), "MapUser.cls", 3);
    }

    /**
     * project9: Deep class hierarchy with abstract class far from interface.
     * Hierarchy: IBaseKey <- ConcreteBase <- MidKey <- DeepAbstractKey (abstract with equals)
     * Expected: 1 violation - abstract class is 3 levels removed from interface.
     */
    @Test
    void deepHierarchyViolation() throws Exception {
        Report report = runRule(Paths.get(TEST_RESOURCES_BASE + "project9"));
        assertEquals(1, report.getViolations().size(),
                "Expected 1 violation when abstract class is deep in hierarchy");
        assertViolation(report.getViolations().get(0), "MapUser.cls", 4);
    }

    private void assertViolation(RuleViolation violation, String fileName, int lineNumber) {
        assertEquals(fileName, violation.getFileId().getFileName());
        assertEquals(lineNumber, violation.getBeginLine());
    }

    private Report runRule(Path testProjectDir) throws IOException {
        Option<PathLike> pathLikeOption = Option.apply(new com.nawforce.runtime.platform.Path(tempDir));
        Option<Option<PathLike>> cacheDirOption = Option.apply(pathLikeOption);
        Environment.setCacheDirOverride(cacheDirOption);

        Language apexLanguage = ApexLanguageModule.getInstance();
        LanguageVersion languageVersion = apexLanguage.getDefaultVersion();
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setIgnoreIncrementalAnalysis(true);
        configuration.setDefaultLanguageVersion(languageVersion);
        configuration.setThreads(0); // don't use separate threads

        configuration.getLanguageProperties(apexLanguage)
                .setProperty(ApexLanguageProperties.MULTIFILE_DIRECTORY, Optional.of(testProjectDir.toString()));

        RuleSet parsedRset = new RuleSetLoader().warnDeprecated(false).loadFromResource("category/apex/errorprone.xml");
        Rule rule = parsedRset.getRuleByName("AvoidInterfaceAsMapKey");

        try (PmdAnalysis pmd = PmdAnalysis.create(configuration)) {
            pmd.files().addDirectory(testProjectDir);
            pmd.addRuleSet(RuleSet.forSingleRule(rule));
            pmd.addListener(GlobalAnalysisListener.exceptionThrower());
            return pmd.performAnalysisAndCollectReport();
        }
    }
}
