/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.testdata;

/**
 * @see <a href="https://github.com/pmd/pmd/issues/3101">[java] NullPointerException when running PMD under JRE 11 #3101</a>
 */
public interface MyList<E> {

    <E> MyList<E> of(E e1);
}
