/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JavaTypeDefinitionToStringNPE<A extends JavaTypeDefinitionToStringNPE.SelfReferringType<A>> {

    Map<TypeLink<?, ? super A>, Collection<?>> contents = new HashMap<>();

    public static class TypeLink<U, T> { }

    public static class SelfReferringType<T extends SelfReferringType<T>> { }

    public final void putNull(TypeLink<?, ? super A> field) {
        contents.put(field, Collections.singleton(null));
    }
}
