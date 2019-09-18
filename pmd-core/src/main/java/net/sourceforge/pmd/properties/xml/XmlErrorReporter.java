/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.xml;

import org.w3c.dom.Node;

/**
 * Reports errors in an XML document. Implementations have a way to
 * associate nodes with their location in the document.
 *
 * TODO this is a placeholder for now.
 */
public interface XmlErrorReporter {

    default void warn(Node node, String message, Object... args) {
        throw new UnsupportedOperationException("TODO");
    }


    default RuntimeException error(Node node, String message, Object... args) {
        return new IllegalArgumentException(String.format(message, args));
    }


    default RuntimeException error(Node node, Throwable ex) {
        return new IllegalArgumentException(ex);
    }


}
