/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath.internal;

import net.sf.saxon.expr.AttributeGetter;
import net.sf.saxon.expr.ContextItemExpression;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.StaticProperty;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.expr.parser.PathMap;
import net.sf.saxon.expr.parser.RebindingMap;
import net.sf.saxon.om.AxisInfo;
import net.sf.saxon.om.FingerprintedQName;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.pattern.NameTest;
import net.sf.saxon.trace.ExpressionPresenter;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.BuiltInAtomicType;
import net.sf.saxon.type.ItemType;
import net.sf.saxon.type.Type;
import net.sf.saxon.value.EmptySequence;


/**
 *
 */
public class TypedAttributeGetter extends Expression {

    private final FingerprintedQName attributeName;

    public TypedAttributeGetter(FingerprintedQName attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public ItemType getItemType() {
        return BuiltInAtomicType.ANY_ATOMIC;
    }

    @Override
    public int computeCardinality() {
        return StaticProperty.ALLOWS_ZERO_OR_MORE;
    }

    @Override
    public int getIntrinsicDependencies() {
        return StaticProperty.DEPENDS_ON_CONTEXT_ITEM;
    }

    @Override
    public TypedAttributeGetter copy(RebindingMap rebindings) {
        return new TypedAttributeGetter(attributeName);
    }

    @Override
    public PathMap.PathMapNodeSet addToPathMap(PathMap pathMap, PathMap.PathMapNodeSet pathMapNodeSet) {
        if (pathMapNodeSet == null) {
            ContextItemExpression cie = new ContextItemExpression();
            pathMapNodeSet = new PathMap.PathMapNodeSet(pathMap.makeNewRoot(cie));
        }
        return pathMapNodeSet.createArc(AxisInfo.ATTRIBUTE, new NameTest(Type.ATTRIBUTE, attributeName, getConfiguration().getNamePool()));
    }

    @Override
    public int getImplementationMethod() {
        return EVALUATE_METHOD;
    }

    @Override
    public SequenceIterator iterate(XPathContext context) throws XPathException {
        Item item = context.getContextItem();
        if (item instanceof AstNodeWrapper) {
            // fast path
            Sequence typed = ((AstNodeWrapper) item).getTypedAttributeValue(attributeName.getURI(), attributeName.getLocalPart());
            return typed == null ? EmptySequence.getInstance().iterate() : typed.iterate();
        }

        return super.iterate(context);
    }

    @Override
    public Item evaluateItem(XPathContext context) throws XPathException {
        return new AttributeGetter(attributeName).evaluateItem(context);
    }

    @Override
    public String toShortString() {
        return "@" + attributeName.getDisplayName();
    }

    @Override
    public String toString() {
        return "typedAttr(@" + attributeName.getDisplayName() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        return obj instanceof TypedAttributeGetter
            && ((TypedAttributeGetter) obj).attributeName.equals(attributeName);
    }

    @Override
    public int hashCode() {
        return 83571 ^ attributeName.hashCode();
    }

    @Override
    public void export(ExpressionPresenter out) throws XPathException {
        out.startElement("tAttVal", this);
        out.emitAttribute("name", attributeName.getStructuredQName().getEQName());
        out.endElement();
    }
}


