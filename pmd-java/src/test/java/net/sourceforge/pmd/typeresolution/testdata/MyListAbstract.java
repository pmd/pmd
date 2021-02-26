/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

/**
 * @see <a href="https://github.com/pmd/pmd/issues/3101">[java] NullPointerException when running PMD under JRE 11 #3101</a>
 */
public abstract class MyListAbstract<E> implements MyList<E> {

    public <E> MyListAbstract<E> of(E e1) {
        return null;
    }
}
