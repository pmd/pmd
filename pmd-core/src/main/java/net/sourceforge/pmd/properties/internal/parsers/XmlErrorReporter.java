package net.sourceforge.pmd.properties.internal.parsers;

import org.w3c.dom.Node;

/**
 * Reports errors in an XML document. Implementations have a way to
 * associate nodes with their location in the document.
 *
 * TODO this is an interface I ripped off another project. It's a placeholder for now
 */
public interface XmlErrorReporter {

    void warn(Node node, String message, Object... args);


    RuntimeException error(Node node, String message, Object... args);


    RuntimeException error(Node node, Throwable ex);


}
