/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionDefinition;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionException;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.StaticContext;
import net.sf.saxon.expr.StringLiteral;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.EmptyAtomicSequence;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.pattern.NodeKindTest;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BigDecimalValue;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.EmptySequence;
import net.sf.saxon.value.Int64Value;
import net.sf.saxon.value.SequenceExtent;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

/**
 * Converts PMD's {@link XPathFunctionDefinition} into Saxon's {@link ExtensionFunctionDefinition}.
 */
public class SaxonExtensionFunctionDefinitionAdapter extends ExtensionFunctionDefinition {
    private static final SequenceType SINGLE_ELEMENT_SEQUENCE_TYPE = NodeKindTest.ELEMENT.one();

    private final XPathFunctionDefinition definition;

    public SaxonExtensionFunctionDefinitionAdapter(XPathFunctionDefinition definition) {
        this.definition = definition;
    }

    private SequenceType convertToSequenceType(XPathFunctionDefinition.Type type) {
        switch (type) {
        case SINGLE_STRING: return SequenceType.SINGLE_STRING;
        case SINGLE_BOOLEAN: return SequenceType.SINGLE_BOOLEAN;
        case SINGLE_ELEMENT: return SINGLE_ELEMENT_SEQUENCE_TYPE;
        case SINGLE_INTEGER: return SequenceType.SINGLE_INTEGER;
        case STRING_SEQUENCE: return SequenceType.STRING_SEQUENCE;
        case OPTIONAL_STRING: return SequenceType.OPTIONAL_STRING;
        case OPTIONAL_DECIMAL: return SequenceType.OPTIONAL_DECIMAL;
        default:
            throw new UnsupportedOperationException("Type " + type + " is not supported");
        }
    }

    private SequenceType[] convertToSequenceTypes(XPathFunctionDefinition.Type[] types) {
        SequenceType[] result = new SequenceType[types.length];
        for (int i = 0; i < types.length; i++) {
            result[i] = convertToSequenceType(types[i]);
        }
        return result;
    }

    @Override
    public StructuredQName getFunctionQName() {
        QName qName = definition.getQName();
        return new StructuredQName(qName.getPrefix(), qName.getNamespaceURI(), qName.getLocalPart());
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return convertToSequenceTypes(definition.getArgumentTypes());
    }

    @Override
    public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
        return convertToSequenceType(definition.getResultType());
    }

    @Override
    public boolean dependsOnFocus() {
        return definition.dependsOnContext();
    }

    @Override
    public ExtensionFunctionCall makeCallExpression() {
        XPathFunctionDefinition.FunctionCall call = definition.makeCallExpression();
        return new ExtensionFunctionCall() {
            @Override
            public Expression rewrite(StaticContext context, Expression[] arguments) throws XPathException {
                Object[] convertedArguments = new Object[definition.getArgumentTypes().length];
                for (int i = 0; i < convertedArguments.length; i++) {
                    if (arguments[i] instanceof StringLiteral) {
                        convertedArguments[i] = ((StringLiteral) arguments[i]).getString().toString();
                    }
                }
                try {
                    call.staticInit(convertedArguments);
                } catch (XPathFunctionException e) {
                    XPathException xPathException = new XPathException(e);
                    xPathException.setIsStaticError(true);
                    throw xPathException;
                }
                return null;
            }

            @Override
            public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
                Node contextNode = null;
                if (definition.dependsOnContext()) {
                    contextNode = XPathElementToNodeHelper.itemToNode(context.getContextItem());
                }
                Object[] convertedArguments = new Object[definition.getArgumentTypes().length];
                for (int i = 0; i < convertedArguments.length; i++) {
                    switch (definition.getArgumentTypes()[i]) {
                        case SINGLE_STRING:
                            convertedArguments[i] = arguments[i].head().getStringValue();
                            break;
                        case SINGLE_ELEMENT:
                            convertedArguments[i] = arguments[i].head();
                            break;
                        default:
                            throw new UnsupportedOperationException("Don't know how to convert argument type " + definition.getArgumentTypes()[i]);
                    }
                }


                Object result = null;
                try {
                    result = call.call(contextNode, convertedArguments);
                } catch (XPathFunctionException e) {
                    throw new XPathException(e);
                }
                Sequence convertedResult = null;
                switch (definition.getResultType()) {
                    case SINGLE_BOOLEAN:
                        convertedResult = BooleanValue.get((Boolean) result);
                        break;
                    case SINGLE_INTEGER:
                        convertedResult = Int64Value.makeIntegerValue((Integer) result);
                        break;
                    case SINGLE_STRING:
                        convertedResult = new StringValue((String) result);
                        break;
                    case OPTIONAL_STRING:
                        convertedResult = result instanceof Optional && ((Optional<String>) result).isPresent()
                                ? new StringValue(((Optional<String>) result).get())
                                : EmptyAtomicSequence.getInstance();
                        break;
                    case STRING_SEQUENCE:
                        convertedResult = result instanceof List
                                ? new SequenceExtent.Of<>(((List<String>) result).stream().map(StringValue::new).collect(Collectors.toList()))
                                : EmptySequence.getInstance();
                        break;
                    case OPTIONAL_DECIMAL:
                        convertedResult = result instanceof Optional && ((Optional<Double>) result).isPresent()
                                ? new BigDecimalValue(((Optional<Double>) result).get())
                                : EmptySequence.getInstance();
                        break;
                    default:
                        throw new UnsupportedOperationException("Don't know how to convert result type " + definition.getResultType());
                }
                return convertedResult;
            }
        };
    }
}
