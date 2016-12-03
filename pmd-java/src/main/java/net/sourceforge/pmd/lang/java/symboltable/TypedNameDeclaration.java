/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

public interface TypedNameDeclaration {

    String getTypeImage();

    Class<?> getType();

}
