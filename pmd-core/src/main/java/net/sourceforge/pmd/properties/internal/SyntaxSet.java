/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.internal;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.w3c.dom.Element;

/**
 * A set of syntaxes for read and write. One special syntax is designated
 * as the one used to write elements, the others are used to read.
 */
public final class SyntaxSet<T> extends XmlSyntax<T> {

    private final XmlSyntax<T> forWrite;
    private final Map<String, XmlSyntax<T>> readIndex;

    public SyntaxSet(XmlSyntax<T> forWrite, Collection<? extends XmlSyntax<T>> forRead) {
        super(forWrite.getElementName());
        this.forWrite = forWrite;

        if (forRead.isEmpty()) {
            throw new IllegalArgumentException("Empty set of reads strategies!");
        }


        this.readIndex = forRead.stream().collect(Collectors.toMap(
            XmlSyntax::getElementName,
            it -> it,
            (a, b) -> {
                // merge function
                throw new IllegalArgumentException(
                    "Duplicate name '" + a.getElementName() + "', for syntaxes " + a + " and " + b
                );
            }
        ));
    }

    @Override
    public T fromXml(Element element, XmlErrorReporter err) {
        XmlSyntax<T> syntax = readIndex.get(element.getTagName());
        if (syntax == null) {
            throw err.error(
                element,
                "Unexpected element name " + enquote(element.getTagName()) + ", expecting " + formatPossibilities()
            );
        } else {
            return syntax.fromXml(element, err);
        }
    }

    @Override
    public void toXml(Element container, T value) {
        forWrite.toXml(container, value);
    }

    // nullable
    private String formatPossibilities() {
        Set<String> strings = readIndex.keySet();
        if (strings.isEmpty()) {
            return null;
        } else if (strings.size() == 1) {
            return enquote(strings.iterator().next());
        } else {
            return "one of " + strings.stream().map(SyntaxSet::enquote).collect(Collectors.joining(", "));
        }
    }

    private static String enquote(String it) {return "'" + it + "'";}

}
