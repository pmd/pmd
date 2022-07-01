/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.apex.ast.ApexParserTestBase;

/**
 * <p>Sharing settings are not inherited by inner classes. Sharing settings need to be declared on the class that
 * contains the Database method, DML, SOQL, or SOSL.</p>
 *
 * <p>This test runs against Apex code that has an Outer class and and Inner class. Different Apex code is generated
 * based on the boolean permutations. Any classes that includes data access cod, but doesn't declare a sharing setting
 * should trigger a violation.</p>
 */
class ApexSharingViolationsNestedClassTest extends ApexParserTestBase {
    /**
     * Type of operation that may require a sharing declaration.
     */
    private enum Operation {
        NONE(null),
        DML_DELETE("Contact c = new Contact(); delete c;"),
        DML_INSERT("Contact c = new Contact(); insert c;"),
        DML_MERGE("Contact c1 = new Contact(); Contact c2 = new Contact(); merge c1 c2;"),
        DML_UNDELETE("Contact c = new Contact(); undelete c;"),
        DML_UPDATE("Contact c = new Contact(); update c;"),
        DML_UPSERT("Contact c = new Contact(); upsert c;"),
        METHOD_DATABASE("Database.query('Select Id from Contact LIMIT 100');"),
        SOQL("[SELECT Name FROM Contact];"),
        SOSL("[FIND 'Foo' IN ALL FIELDS RETURNING Account(Name)];");

        final boolean requiresSharingDeclaration;
        final String codeSnippet;

        Operation(String codeSnippet) {
            this.requiresSharingDeclaration = codeSnippet != null;
            this.codeSnippet = codeSnippet;
        }
    }

    /**
     * The permutations used for class generation. See {@link #generateClass(boolean, Operation, boolean, Operation)}
     */
    @ParameterizedTest
    @MethodSource("data")
    void testSharingPermutation(boolean outerSharingDeclared, Operation outerOperation,
                                boolean innerSharingDeclared, Operation innerOperation,
                                int expectedViolations, List<Integer> expectedLineNumbers) {
        String apexClass = generateClass(outerSharingDeclared, outerOperation, innerSharingDeclared, innerOperation);
        ApexSharingViolationsRule rule = new ApexSharingViolationsRule();
        rule.setMessage("a message");
        Report rpt = apex.executeRule(rule, apexClass);
        List<RuleViolation> violations = rpt.getViolations();
        assertEquals(expectedViolations, violations.size(), "Unexpected Violation Size\n" + apexClass);
        List<Integer> lineNumbers = violations.stream().map(v -> v.getBeginLine()).collect(Collectors.toList());
        assertEquals(expectedLineNumbers, lineNumbers, "Unexpected Line Numbers\n" + apexClass);
    }

    /**
     * Parameter provider that covers are all permutations
     */
    static Collection<?> data() {
        List<Object[]> data = new ArrayList<>();

        boolean[] boolPermutations = {false, true};

        for (boolean outerSharingDeclared : boolPermutations) {
            for (Operation outerOperation : Operation.values()) {
                for (boolean innerSharingDeclared : boolPermutations) {
                    for (Operation innerOperation : Operation.values()) {
                        int expectedViolations = 0;
                        List<Integer> expectedLineNumbers = new ArrayList<>();
                        if (outerOperation.requiresSharingDeclaration && !outerSharingDeclared) {
                            // The outer class contains SOQL but doesn't declare sharing
                            expectedViolations++;
                            expectedLineNumbers.add(1);
                        }

                        if (innerOperation.requiresSharingDeclaration && !innerSharingDeclared) {
                            // The inner class contains SOQL but doesn't declare sharing
                            expectedViolations++;
                            // The location of the inner class declaration depends upon the content of the outer class
                            expectedLineNumbers.add(outerOperation.requiresSharingDeclaration ? 3 : 2);
                        }
                        data.add(new Object[]{outerSharingDeclared, outerOperation, innerSharingDeclared, innerOperation,
                                              expectedViolations, expectedLineNumbers});
                    }
                }
            }
        }

        return data;
    }

    /**
     * <p>Generate an Apex class with various Sharing/Database/DML/SOQL/SOSL permutations. An example of the class
     * returned when invoked with generateClass(true, SOQL, true, SOQL).</p>
     *
     * <pre>
     * public with sharing class Outer {
     *    public void outerSOQL() {[SELECT Name FROM Contact];}
     *    public with sharing class Inner {
     *       public void innerSOQL() {[SELECT Name FROM Contact];}
     *    }
     * }
     * </pre>
     *
     * @param outerSharing Add 'with sharing' to Outer class definition
     * @param outerOperation Add a method to Outer class that performs the given operation
     * @param innerSharing Add 'with sharing' to Inner class definition
     * @param innerOperation Add a method to Inner class that performs the given operation
     * @return String that represents Apex code
     */
    private static String generateClass(boolean outerSharing, Operation outerOperation, boolean innerSharing,
                                        Operation innerOperation) {
        StringBuilder sb = new StringBuilder();

        sb.append("public ");
        if (outerSharing) {
            sb.append("with sharing ");
        }
        sb.append("class Outer {\n");
        switch (outerOperation) {
        case NONE:
            // Do nothing
            break;
        default:
            sb.append(String.format("\t\tpublic void outer%s(){ %s }\n", outerOperation.name(), outerOperation.codeSnippet));
            break;
        }
        sb.append("\tpublic ");
        if (innerSharing) {
            sb.append("with sharing ");
        }
        sb.append("class Inner {\n");
        switch (innerOperation) {
        case NONE:
            // DO Nothing
            break;
        default:
            sb.append(String.format("\t\tpublic void inner%s(){ %s }\n", innerOperation.name(), innerOperation.codeSnippet));
            break;
        }
        sb.append("\t}\n"); // Closes class Inner
        sb.append("}\n"); // Closes class Outer

        return sb.toString();
    }
}
