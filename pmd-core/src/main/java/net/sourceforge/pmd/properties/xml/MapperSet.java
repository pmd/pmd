/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.xml;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.w3c.dom.Element;

import net.sourceforge.pmd.internal.util.xml.XmlErrorMessages;
import net.sourceforge.pmd.internal.util.xml.XmlUtil;
import net.sourceforge.pmd.properties.constraints.PropertyConstraint;
import net.sourceforge.pmd.util.CollectionUtil;

import com.github.oowekyala.ooxml.messages.XmlErrorReporter;

/**
 * A set of syntaxes for read and write. One special syntax is designated
 * as the one used to write elements, the others are used to read.
 */
final class MapperSet<T> extends XmlMapper<T> {

    private final XmlMapper<T> forWrite;
    private final Map<String, XmlMapper<T>> readIndex;

    /**
     * @param newSyntax Newer syntax (eg seq)
     * @param compat    Value syntax (eg delimited string for sequence)
     */
    MapperSet(XmlMapper<T> newSyntax, ValueSyntax<T> compat, boolean preferNew) {
        // the set here prunes duplicates
        this(preferNew ? newSyntax : compat, CollectionUtil.setOf(newSyntax, compat));
    }

    /**
     * TODO This constructor is a bit too general for now. The other constructor
     *  is the only one that's public. The problem with publishing this constructor,
     *  is that there may be a SyntaxSet somewhere in the 'forRead' set, and some
     *  overlapping values may be unlocked
     *
     * @param forWrite Designated strategy for writing
     * @param forRead  Set of supported syntaxes, must have pairwise different read names
     */
    private MapperSet(XmlMapper<T> forWrite, Collection<? extends XmlMapper<T>> forRead) {
        super();
        this.forWrite = forWrite;

        if (forRead.isEmpty()) {
            throw new IllegalArgumentException("Empty set of reads strategies!");
        }

        Map<String, XmlMapper<T>> map = new LinkedHashMap<>();
        for (XmlMapper<T> syntax : forRead) {
            for (String name : syntax.getReadElementNames()) {

                map.merge(name, syntax, (a, b) -> {
                    // merge function
                    throw new IllegalArgumentException(
                        "Duplicate name '" + name + "', for syntaxes " + a + " and " + b
                    );
                });
            }
        }
        this.readIndex = map;
    }

    @Override
    public Set<String> getReadElementNames() {
        return readIndex.keySet();
    }

    @Override
    public String getWriteElementName(T value) {
        return forWrite.getWriteElementName(value);
    }

    @Override
    public @Nullable T fromString(@NonNull String string) {

        for (XmlMapper<T> syntax : supportedReadStrategies()) {
            if (syntax.supportsStringMapping()) {
                return syntax.fromString(string);
            }
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public @NonNull String toString(T value) {

        for (XmlMapper<T> syntax : supportedReadStrategies()) {
            if (syntax.supportsStringMapping()) {
                return syntax.toString(value);
            }
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public List<PropertyConstraint<? super T>> getConstraints() {
        return readIndex.values().iterator().next().getConstraints();
    }

    private Collection<XmlMapper<T>> supportedReadStrategies() {
        return readIndex.values();
    }

    @Override
    public boolean supportsStringMapping() {
        return supportedReadStrategies().stream().anyMatch(XmlMapper::supportsStringMapping)
            && forWrite.supportsStringMapping();
    }

    @Override
    public T fromXml(Element element, XmlErrorReporter err) {
        XmlMapper<T> syntax = readIndex.get(element.getTagName());
        if (syntax == null) {
            throw err.error(element, XmlErrorMessages.ERR__UNEXPECTED_ELEMENT, element.getTagName(), XmlUtil.formatPossibleNames(readIndex.keySet()));
        } else {
            return syntax.fromXml(element, err);
        }
    }

    @Override
    public void toXml(Element container, T value) {
        forWrite.toXml(container, value);
    }


    @Override
    protected List<String> examplesImpl(String curIndent, String baseIndent) {
        return readIndex.values().stream().flatMap(it -> it.examplesImpl(curIndent, baseIndent).stream()).collect(Collectors.toList());
    }
}
