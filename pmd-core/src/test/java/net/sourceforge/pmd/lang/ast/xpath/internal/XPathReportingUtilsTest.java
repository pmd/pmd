/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

/**
 * @author Clément Fournier
 */
public class XPathReportingUtilsTest {

    @Test
    public void testDeprecatedAttrNames() {

        String testOutput = "août 08, 2019 10:30:29 PM net.sourceforge.pmd.PMD processFiles\n"
            + "AVERTISSEMENT: This analysis could be faster, please consider using Incremental Analysis: https://pmd.github.io/latest/pmd_userdocs_incremental_analysis.html\n"
            + "août 08, 2019 10:30:29 PM net.sourceforge.pmd.processor.AbstractPMDProcessor removeBrokenRules\n"
            + "AVERTISSEMENT: Removed misconfigured rule: LoosePackageCoupling  cause: No packages or classes specified\n"
            + "août 08, 2019 10:30:29 PM net.sourceforge.pmd.lang.ast.xpath.internal.XPathReportingUtils logIfDeprecated\n"
            + "AVERTISSEMENT: Use of deprecated attribute 'ClassOrInterfaceDeclaration/@Image' in XPath query, use @SimpleName instead\n"
            + "août 08, 2019 10:30:29 PM net.sourceforge.pmd.lang.ast.xpath.internal.XPathReportingUtils logIfDeprecated\n"
            + "AVERTISSEMENT: Use of deprecated attribute 'VariableDeclaratorId/@Image' in XPath query, use @VariableName instead\n";

        List<String> detected = XPathReportingUtils.deprecatedAttrNames(testOutput);

        assertTrue("Should detect VariableDeclaratorId", detected.contains("VariableDeclaratorId/@Image"));
        assertTrue("Should detect ClassOrInterfaceDeclaration", detected.contains("ClassOrInterfaceDeclaration/@Image"));
        assertEquals("Should not contain more", 2, detected.size());

    }
}
