/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;

interface AnnotationOwner {

    void addAnnotation(SymAnnot annot);

}
