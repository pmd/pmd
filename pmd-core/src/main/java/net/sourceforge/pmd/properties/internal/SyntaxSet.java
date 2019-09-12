/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.internal;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.w3c.dom.Element;

import net.sourceforge.pmd.util.CollectionUtil;

/**
 * A set of syntaxes for read and write. One special syntax is designated
 * as the one used to write elements, the others are used to read.
 */
public final class SyntaxSet<T> extends XmlSyntax<T> {

    private final XmlSyntax<T> forWrite;
    private final Map<String, XmlSyntax<T>> readIndex;

    /**
     * @param newSyntax Newer syntax (eg seq)
     * @param compat    Value syntax (eg delimited string for sequence)
     */
    public SyntaxSet(XmlSyntax<T> newSyntax, ValueSyntax<T> compat, boolean preferNew) {
        // the set here prunes duplicates
        this(preferNew ? newSyntax : compat, CollectionUtil.setOf(newSyntax, compat));
    }

    /**
     * @param forWrite Designated strategy for writing
     * @param forRead  Set of supported syntaxes, must have pairwise different read names
     */
    private SyntaxSet(XmlSyntax<T> forWrite, Collection<? extends XmlSyntax<T>> forRead) {
        super(forWrite.getWriteElementName(), readNames(forRead));
        this.forWrite = forWrite;

        if (forRead.isEmpty()) {
            throw new IllegalArgumentException("Empty set of reads strategies!");
        }


        this.readIndex = forRead.stream().collect(Collectors.toMap(
            XmlSyntax::getWriteElementName,
            it -> it,
            (a, b) -> {
                // merge function
                throw new IllegalArgumentException(
                    "Duplicate name '" + a.getWriteElementName() + "', for syntaxes " + a + " and " + b
                );
            },
            LinkedHashMap::new
        ));
    }

    @Override
    public @Nullable T fromString(String string) {

        for (XmlSyntax<T> syntax : supportedReadStrategies()) {
            if (syntax.supportsStringMapping()) {
                return syntax.fromString(string);
            }
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public String toString(T value) {

        for (XmlSyntax<T> syntax : supportedReadStrategies()) {
            if (syntax.supportsStringMapping()) {
                return syntax.toString(value);
            }
        }

        throw new UnsupportedOperationException();
    }

    public Set<XmlSyntax<T>> supportedReadStrategies() {
        return new LinkedHashSet<>(readIndex.values());
    }

    @Override
    public boolean supportsStringMapping() {
        return supportedReadStrategies().stream().anyMatch(XmlSyntax::supportsStringMapping);
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

    private static Set<String> readNames(Collection<? extends XmlSyntax<?>> syntaxes) {
        return syntaxes.stream()
                       .flatMap(it -> it.getSupportedReadElementNames().stream())
                       .collect(Collectors.toSet());
    }

    private static String enquote(String it) {return "'" + it + "'";}

    @Override
    public List<String> examples() {
        return readIndex.values().stream().flatMap(it -> it.examples().stream()).collect(Collectors.toList());
    }
}
