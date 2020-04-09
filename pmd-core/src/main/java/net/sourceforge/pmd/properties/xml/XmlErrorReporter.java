/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.xml;

import java.util.logging.Logger;

/**
 * Reports errors in an XML document. Implementations have a way to
 * associate nodes with their location in the document.
 *
 * TODO this is a placeholder for now, I need to publish the impl to maven
 */
public interface XmlErrorReporter {

    Logger LOGGER = Logger.getLogger(XmlErrorReporter.class.getName());

    default void warn(org.w3c.dom.Node node, String message, Object... args) {
        LOGGER.warning(String.format(message, args));
    }


    default RuntimeException error(org.w3c.dom.Node node, String message, Object... args) {
        return new IllegalArgumentException(String.format(message, args));
    }


    default RuntimeException error(org.w3c.dom.Node node, Throwable ex) {
        return new IllegalArgumentException(ex);
    }


}
