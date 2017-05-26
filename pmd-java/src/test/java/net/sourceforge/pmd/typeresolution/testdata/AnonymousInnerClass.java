/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

import java.util.AbstractList;
import java.util.List;

public class AnonymousInnerClass {
    List<Object> list = new AbstractList<Object>() {
        @Override
        public Object get(int index) {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }
    };
}
