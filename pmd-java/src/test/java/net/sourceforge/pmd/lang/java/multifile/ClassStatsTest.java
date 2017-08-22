/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.multifile;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.ast.JavaQualifiedName;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaFieldSigMask;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSigMask;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSignature.Role;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaSignature.Visibility;
import net.sourceforge.pmd.lang.java.multifile.testdata.SignatureCountTestData;

/**
 * @author Clément Fournier
 */
public class ClassStatsTest {

    @Before
    public void resetMultifile() {
        MultifileFacade.reset();
    }


    @Test
    public void testCountOpSigs() {

        JavaMultifileVisitorTest.parseAndVisitForClass(SignatureCountTestData.class);

        final ProjectMirror toplevel = MultifileFacade.getTopLevelPackageStats();

        final ClassMirror classMirror = toplevel.getClassMirror(JavaQualifiedName.ofClass(SignatureCountTestData.class));

        final JavaOperationSigMask opSigMask = new JavaOperationSigMask().coverAbstract();
        opSigMask.restrictRolesTo(Role.STATIC);

        assertEquals(4, classMirror.countMatchingOpSigs(opSigMask));
        assertEquals(2, classMirror.countMatchingOpSigs(opSigMask.restrictVisibilitiesTo(Visibility.PRIVATE)));
        assertEquals(2, classMirror.countMatchingOpSigs(opSigMask.restrictVisibilitiesTo(Visibility.PUBLIC)));

        opSigMask.restrictRolesTo(Role.METHOD).coverAllVisibilities();

        assertEquals(6, classMirror.countMatchingOpSigs(opSigMask));
        assertEquals(1, classMirror.countMatchingOpSigs(opSigMask.restrictVisibilitiesTo(Visibility.PUBLIC)));
        assertEquals(1, classMirror.countMatchingOpSigs(opSigMask.restrictVisibilitiesTo(Visibility.PRIVATE)));
        assertEquals(4, classMirror.countMatchingOpSigs(opSigMask.restrictVisibilitiesTo(Visibility.PROTECTED)));
        assertEquals(2, classMirror.countMatchingOpSigs(opSigMask.forbidAbstract()));

        opSigMask.restrictRolesTo(Role.GETTER_OR_SETTER).coverAllVisibilities();

        assertEquals(8, classMirror.countMatchingOpSigs(opSigMask));
        assertEquals(4, classMirror.countMatchingOpSigs(opSigMask.restrictVisibilitiesTo(Visibility.PACKAGE)));
        assertEquals(4, classMirror.countMatchingOpSigs(opSigMask.restrictVisibilitiesTo(Visibility.PUBLIC)));

        opSigMask.restrictRolesTo(Role.CONSTRUCTOR).coverAllVisibilities();

        assertEquals(3, classMirror.countMatchingOpSigs(opSigMask));
        assertEquals(3, classMirror.countMatchingOpSigs(opSigMask.restrictVisibilitiesTo(Visibility.PUBLIC)));


        JavaFieldSigMask fieldSigMask = new JavaFieldSigMask();

        assertEquals(6, classMirror.countMatchingFieldSigs(fieldSigMask));
        assertEquals(3, classMirror.countMatchingFieldSigs(fieldSigMask.restrictVisibilitiesTo(Visibility.PUBLIC)));
        assertEquals(1, classMirror.countMatchingFieldSigs(fieldSigMask.restrictVisibilitiesTo(Visibility.PROTECTED)));
        assertEquals(2, classMirror.countMatchingFieldSigs(fieldSigMask.restrictVisibilitiesTo(Visibility.PRIVATE)));
        assertEquals(0, classMirror.countMatchingFieldSigs(fieldSigMask.restrictVisibilitiesTo(Visibility.PRIVATE).forbidFinal()));


    }


}
