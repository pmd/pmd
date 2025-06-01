/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.TypePath;

interface TypeAnnotationReceiver {

    void acceptTypeAnnotation(int typeRef, @Nullable TypePath path, SymAnnot annot);
}
