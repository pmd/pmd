/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.multifile;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaFieldSigMask;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSigMask;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSignature.Role;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaSignature.Visibility;
import net.sourceforge.pmd.lang.java.multifile.testdata.SignatureCountTestData;
import net.sourceforge.pmd.lang.java.qname.QualifiedNameFactory;

/**
 * @author Clément Fournier
 */
public class ClassStatsTest {

    @Before
    public void resetMultifile() {
        PackageStats.INSTANCE.reset();
    }


    @Test
    @Ignore("Exception in typeresolution visit")
    public void testCountOpSigs() {

        JavaParsingHelper.WITH_PROCESSING.parseClass(SignatureCountTestData.class);

        final ProjectMirror toplevel = PackageStats.INSTANCE;

        final ClassMirror classMirror = toplevel.getClassMirror(QualifiedNameFactory.ofClass(SignatureCountTestData.class));

        final FluentOperationSigMask opSigMask = new FluentOperationSigMask();

        opSigMask.mask.coverAbstract();
        opSigMask.restrictRolesTo(Role.STATIC);

        assertEquals(4, classMirror.countMatchingOpSigs(opSigMask.mask));
        assertEquals(2, classMirror.countMatchingOpSigs(opSigMask.restrictVisibilitiesTo(Visibility.PRIVATE)));
        assertEquals(2, classMirror.countMatchingOpSigs(opSigMask.restrictVisibilitiesTo(Visibility.PUBLIC)));

        opSigMask.restrictRolesTo(Role.METHOD).coverAllVisibilities();

        assertEquals(6, classMirror.countMatchingOpSigs(opSigMask.mask));
        assertEquals(1, classMirror.countMatchingOpSigs(opSigMask.restrictVisibilitiesTo(Visibility.PUBLIC)));
        assertEquals(1, classMirror.countMatchingOpSigs(opSigMask.restrictVisibilitiesTo(Visibility.PRIVATE)));
        assertEquals(4, classMirror.countMatchingOpSigs(opSigMask.restrictVisibilitiesTo(Visibility.PROTECTED)));
        assertEquals(2, classMirror.countMatchingOpSigs(opSigMask.forbidAbstract()));

        opSigMask.restrictRolesTo(Role.GETTER_OR_SETTER).coverAllVisibilities();

        assertEquals(8, classMirror.countMatchingOpSigs(opSigMask.mask));
        assertEquals(4, classMirror.countMatchingOpSigs(opSigMask.restrictVisibilitiesTo(Visibility.PACKAGE)));
        assertEquals(4, classMirror.countMatchingOpSigs(opSigMask.restrictVisibilitiesTo(Visibility.PUBLIC)));

        opSigMask.restrictRolesTo(Role.CONSTRUCTOR).coverAllVisibilities();

        assertEquals(3, classMirror.countMatchingOpSigs(opSigMask.mask));
        assertEquals(3, classMirror.countMatchingOpSigs(opSigMask.restrictVisibilitiesTo(Visibility.PUBLIC)));


        FluentFieldSigMask fieldSigMask = new FluentFieldSigMask();

        assertEquals(6, classMirror.countMatchingFieldSigs(fieldSigMask.mask));
        assertEquals(3, classMirror.countMatchingFieldSigs(fieldSigMask.restrictVisibilitiesTo(Visibility.PUBLIC)));
        assertEquals(1, classMirror.countMatchingFieldSigs(fieldSigMask.restrictVisibilitiesTo(Visibility.PROTECTED)));
        assertEquals(2, classMirror.countMatchingFieldSigs(fieldSigMask.restrictVisibilitiesTo(Visibility.PRIVATE)));

        fieldSigMask.mask.forbidFinal();

        assertEquals(0, classMirror.countMatchingFieldSigs(fieldSigMask.restrictVisibilitiesTo(Visibility.PRIVATE)));


    }

    // Containers to clear up tests
    private class FluentOperationSigMask {

        JavaOperationSigMask mask = new JavaOperationSigMask();


        JavaOperationSigMask forbidAbstract() {
            mask.coverAbstract(false);
            return mask;
        }


        JavaOperationSigMask restrictVisibilitiesTo(Visibility... visibilities) {
            mask.restrictVisibilitiesTo(visibilities);
            return mask;
        }


        JavaOperationSigMask restrictRolesTo(Role... roles) {
            mask.restrictRolesTo(roles);
            return mask;
        }
    }

    private class FluentFieldSigMask {

        JavaFieldSigMask mask = new JavaFieldSigMask();


        JavaFieldSigMask restrictVisibilitiesTo(Visibility... visibilities) {
            mask.restrictVisibilitiesTo(visibilities);
            return mask;
        }
    }


}
