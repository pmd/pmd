/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Range;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.document.TextRegion;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;


public class ASTHtmlElement extends AbstractHtmlNode<Element> {

    private final List<Attribute> attributes;

    ASTHtmlElement(Element element) {
        super(element);

        attributes = new ArrayList<>();
        for (org.jsoup.nodes.Attribute att : node.attributes()) {
            attributes.add(new Attribute(this, att.getKey(), att.getValue()));
        }
    }

    @Override
    protected <P, R> R acceptHtmlVisitor(HtmlVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public boolean hasAttribute(String name) {
        return attributes.stream().anyMatch(attribute -> name.equalsIgnoreCase(attribute.getName()));
    }

    public String getAttribute(String rel) {
        return attributes.stream()
                .filter(attribute -> rel.equalsIgnoreCase(attribute.getName()))
                .findFirst()
                .map(Attribute::getValue)
                .map(String::valueOf)
                .orElse(null);
    }

    /**
     * Returns the source range of the explicit closing tag, or {@code null}
     * if this element has no source-backed closing tag.
     *
     * @return The closing tag's source range, or {@code null}
     * @internalApi
     */
    @InternalApi
    public @Nullable TextRegion getClosingTagRegion() {
        Range range = node.endSourceRange();
        if (!range.isTracked() || range.isImplicit()) {
            return null;
        }
        return TextRegion.fromBothOffsets(range.startPos(), range.endPos());
    }

    @Override
    public Iterator<Attribute> getXPathAttributesIterator() {
        Iterator<Attribute> defaultAttributes = super.getXPathAttributesIterator();
        Iterator<Attribute> elementAttributes = attributes.iterator();

        return new Iterator<Attribute>() {
            @Override
            public boolean hasNext() {
                return defaultAttributes.hasNext() || elementAttributes.hasNext();
            }

            @Override
            public Attribute next() {
                if (defaultAttributes.hasNext()) {
                    return defaultAttributes.next();
                }
                return elementAttributes.next();
            }
        };
    }
}
