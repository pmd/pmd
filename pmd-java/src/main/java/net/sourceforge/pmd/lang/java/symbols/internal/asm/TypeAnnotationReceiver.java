/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import org.objectweb.asm.TypePath;

import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;

interface TypeAnnotationReceiver {

    void addTypeAnnotation(TypePath path, SymAnnot annot);

}
