/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone.closeresource;

import java.io.InputStream;

public class MyFactory {
    public InputStream getResource() {
        return null;
    }
}
