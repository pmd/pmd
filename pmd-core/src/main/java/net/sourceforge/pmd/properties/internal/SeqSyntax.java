/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.internal;

import java.util.Collection;
import java.util.function.Supplier;

import org.w3c.dom.Element;

/**
 * Serialize to and from a simple string. Examples:
 *
 * <pre>{@code
 *  <seq>1</seq>
 * }</pre>
 */
public final class SeqSyntax<T, C extends Collection<T>> extends XmlSyntax<C> {

    private final XmlSyntax<T> itemSyntax;
    private final Supplier<C> emptyCollSupplier;

    SeqSyntax(XmlSyntax<T> itemSyntax, Supplier<C> emptyCollSupplier, String name) {
        super(name);
        this.itemSyntax = itemSyntax;
        this.emptyCollSupplier = emptyCollSupplier;
    }


    @Override
    public void toXml(Element container, C value) {
        for (T v : value) {
            Element item = container.getOwnerDocument().createElement(itemSyntax.getElementName());
            itemSyntax.toXml(item, v);
            container.appendChild(item);
        }
    }

    @Override
    public C fromXml(Element element, XmlErrorReporter err) {
        C result = emptyCollSupplier.get();

        XmlUtils.getElementChildren(element).forEach(child -> {
            T item = XmlUtils.expectElement(err, child, itemSyntax);
            if (item != null) {
                result.add(item);
            }
        });
        return result;
    }
}
