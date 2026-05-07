/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.errorprone;

import static net.sourceforge.pmd.lang.apex.multifile.ApexMultifileTestSupport.assertViolation;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.lang.apex.multifile.ApexMultifileTestSupport;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.test.PmdRuleTst;

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

    private Report runRule(Path testProjectDir) throws IOException {
        return ApexMultifileTestSupport.runRule(tempDir, testProjectDir, "errorprone", "AvoidInterfaceAsMapKey");
    }
}
