/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.xml;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.w3c.dom.Element;

import net.sourceforge.pmd.properties.constraints.PropertyConstraint;
import net.sourceforge.pmd.util.CollectionUtil;

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

    // TODO this scheme for string mapping is lossy, and is there just
    //  for compatibility with CodeClimateRenderer

    @Override
    public boolean supportsStringMapping() {
        return itemSyntax.supportsStringMapping();
    }

    @Override
    public String toString(Optional<T> value) {
        return value.map(itemSyntax::toString).orElse("");
    }

    @Override
    public Optional<T> fromString(String attributeData) {
        return attributeData.isEmpty() ? Optional.empty()
                                       : Optional.ofNullable(itemSyntax.fromString(attributeData));
    }

    @Override
    public List<PropertyConstraint<? super Optional<T>>> getConstraints() {
        return itemSyntax.getConstraints().stream()
                         .map(PropertyConstraint::toOptionalConstraint)
                         .collect(Collectors.toList());
    }

    @Override
    public void toXml(Element container, Optional<T> value) {
        if (value.isPresent()) {
            itemSyntax.toXml(container, value.get());
        } else {
            Element none = container.getOwnerDocument().createElement(EMPTY_NAME);
            container.appendChild(none);
        }
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
    protected List<String> examplesImpl(String curIndent, String baseIndent) {
        return CollectionUtil.plus(itemSyntax.examplesImpl(curIndent, baseIndent), curIndent + "<none/>");
    }
}
