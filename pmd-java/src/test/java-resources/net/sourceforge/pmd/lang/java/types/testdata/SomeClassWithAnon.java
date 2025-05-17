/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.testdata;

/**
 * #2756
 */
public class SomeClassWithAnon {

    {
        new Runnable() {

            @Override
            public void run() {

            }
        };


    }


}
