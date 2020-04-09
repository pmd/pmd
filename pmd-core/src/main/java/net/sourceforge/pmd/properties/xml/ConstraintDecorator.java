/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.xml;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;

import net.sourceforge.pmd.properties.constraints.PropertyConstraint;
import net.sourceforge.pmd.properties.xml.internal.XmlErrorMessages;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Decorates an XmlMapper with a {@link PropertyConstraint},
 * that is checked when the value is parsed. This is used to
 * report errors on the most specific failing element.
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
        List<String> failures = checkConstraints(t);
        if (!failures.isEmpty()) {
            throw err.error(element, XmlErrorMessages.CONSTRAINT_NOT_SATISFIED, failures);
        }
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
    protected List<String> examples(String curIndent, String baseIndent) {
        return xmlMapper.examples(curIndent, baseIndent);
    }

    public XmlMapper<T> getXmlMapper() {
        return xmlMapper;
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

    static <T> ConstraintDecorator<T> constrain(XmlMapper<T> mapper, PropertyConstraint<? super T> constraint) {
        List<PropertyConstraint<? super T>> constraints;
        if (mapper instanceof ConstraintDecorator) {
            constraints = CollectionUtil.plus(((ConstraintDecorator<T>) mapper).constraints, constraint);
        } else {
            constraints = Collections.singletonList(constraint);
        }

        return new ConstraintDecorator<>(mapper, constraints);
    }
}
