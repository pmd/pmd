/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle.unnecessaryimport;

import net.sourceforge.pmd.lang.java.rule.codestyle.unnecessaryimport.ConcFlow.Subscription;

abstract class Hello {

    public static void sayHello() {
        //...
        Subscription x;
    }
}
