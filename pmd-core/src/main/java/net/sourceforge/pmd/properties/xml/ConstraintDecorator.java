/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.xml;

import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;

import net.sourceforge.pmd.properties.constraints.PropertyConstraint;
import net.sourceforge.pmd.properties.xml.internal.XmlErrorMessages;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Decorates an XmlMapper with some {@link PropertyConstraint}s.
 * Those are checked when the value is parsed. This is used to
 * report errors on the most specific failing element.
 *
 * <p>Note that this is the only XmlMapper that *applies* constraints
 * in {@link #fromXml(Element, XmlErrorReporter)}. A {@link SeqSyntax}
 * or {@link OptionalSyntax} may return some constraints in {@link #getConstraints()}
 * that are derived from the constraints of the item, yet not check them
 * on elements (they will be applied on each element by the {@link XmlMapper}
 * they wrap).
 */
class ConstraintDecorator<T> extends XmlMapper<T> {


    private final XmlMapper<T> xmlMapper;
    private final List<PropertyConstraint<? super T>> constraints;

    ConstraintDecorator(XmlMapper<T> mapper, List<PropertyConstraint<? super T>> constraints) {
        this.xmlMapper = mapper;
        this.constraints = constraints;
    }

    @Override
    public T fromXml(Element element, XmlErrorReporter err) {
        T t = xmlMapper.fromXml(element, err);

        XmlSyntaxUtils.checkConstraintsThrow(
            t,
            constraints,
            s -> err.error(element, XmlErrorMessages.CONSTRAINT_NOT_SATISFIED, s)
        );

        return t;
    }

    @Override
    public List<PropertyConstraint<? super T>> getConstraints() {
        return constraints;
    }

    @Override
    public XmlMapper<T> withConstraint(PropertyConstraint<? super T> t) {
        return new ConstraintDecorator<>(this.xmlMapper, CollectionUtil.plus(this.constraints, t));
    }

    @Override
    public void toXml(Element container, T value) {
        xmlMapper.toXml(container, value);
    }


    @Override
    public String getWriteElementName(T value) {
        return xmlMapper.getWriteElementName(value);
    }


    @Override
    public Set<String> getReadElementNames() {
        return xmlMapper.getReadElementNames();
    }


    @Override
    protected List<String> examplesImpl(String curIndent, String baseIndent) {
        return xmlMapper.examplesImpl(curIndent, baseIndent);
    }

    @Override
    public boolean supportsStringMapping() {
        return xmlMapper.supportsStringMapping();
    }

    @Override
    public boolean isStringParserDelimited() {
        return xmlMapper.isStringParserDelimited();
    }

    @Override
    public T fromString(String attributeData) {
        return xmlMapper.fromString(attributeData);
    }

    @Override
    public String toString(T value) {
        return xmlMapper.toString(value);
    }

    @Override
    public String toString() {
        return xmlMapper.toString();
    }

}
