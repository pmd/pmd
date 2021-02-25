/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

import net.sourceforge.pmd.typeresolution.testdata.MyList;
import net.sourceforge.pmd.typeresolution.testdata.MyListAbstract;

/**
 * Note: This class is not compiled (the source is only in src/test/resources). Hence we can't
 * resolve {@code GitHubBug3101.Inner} and the explicit type arguments is not resolved.
 *
 * @see <a href="https://github.com/pmd/pmd/issues/3101">[java] NullPointerException when running PMD under JRE 11 #3101</a>
 */
public class GitHubBug3101 {
    {
        MyList<Inner> a = MyListAbstract.<Inner>of(new Inner());
    }

    private static class Inner { }
}
