/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.xml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.w3c.dom.Element;

/**
 * Serialize an optional value. If the value is itself an {@code Optional<S>},
 * then mentioning {@code </none>} will yield a toplevel empty optional. So
 * having a non-empty optional with an empty optional inside is disallowed.
 */
final class OptionalSyntax<T> extends XmlMapper<Optional<T>> {

    private static final String EMPTY_NAME = "none";
    private final XmlMapper<T> itemSyntax;

    OptionalSyntax(XmlMapper<T> itemSyntax) {
        this.itemSyntax = itemSyntax;

    }

    @Override
    public void toXml(Element container, Optional<T> value) {

    }

    @Override
    public Optional<T> fromXml(Element element, XmlErrorReporter err) {
        if (element.getTagName().equals(EMPTY_NAME)) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(itemSyntax.fromXml(element, err));
        }
    }

    @Override
    public String getWriteElementName(Optional<T> value) {
        return value.map(itemSyntax::getWriteElementName).orElse(EMPTY_NAME);
    }

    @Override
    public Set<String> getReadElementNames() {
        HashSet<String> strings = new HashSet<>(itemSyntax.getReadElementNames());
        strings.add(EMPTY_NAME);
        return strings;
    }

    @Override
    public List<String> examples() {
        ArrayList<String> list = new ArrayList<>(itemSyntax.examples());
        list.add("<none/>");
        return list;
    }
}
