/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.codestyle;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.inRange;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.ASTArgument;
import net.sourceforge.pmd.lang.plsql.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.plsql.ast.ASTBulkCollectIntoClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTDatatype;
import net.sourceforge.pmd.lang.plsql.ast.ASTDeclarativeSection;
import net.sourceforge.pmd.lang.plsql.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.plsql.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.plsql.ast.ASTFromClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTInput;
import net.sourceforge.pmd.lang.plsql.ast.ASTJoinClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTSelectList;
import net.sourceforge.pmd.lang.plsql.ast.ASTSubqueryOperation;
import net.sourceforge.pmd.lang.plsql.ast.ASTUnqualifiedID;
import net.sourceforge.pmd.lang.plsql.ast.ASTVariableOrConstantDeclarator;
import net.sourceforge.pmd.lang.plsql.rule.AbstractPLSQLRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;


public class CodeFormatRule extends AbstractPLSQLRule {

    private static final PropertyDescriptor<Integer> INDENTATION_PROPERTY = PropertyFactory.intProperty("indentation")
                                                                                           .desc("Indentation to be used for blocks").defaultValue(2).require(inRange(0, 32)).build();

    private int indentation = INDENTATION_PROPERTY.defaultValue();

    public CodeFormatRule() {
        definePropertyDescriptor(INDENTATION_PROPERTY);
    }

    @Override
    public Object visit(ASTInput node, Object data) {
        indentation = getProperty(INDENTATION_PROPERTY);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTSelectList node, Object data) {
        Node parent = node.getParent();
        checkEachChildOnNextLine(data, node, parent.getBeginLine(), parent.getBeginColumn() + 7);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTBulkCollectIntoClause node, Object data) {
        Node parent = node.getParent();
        checkIndentation(data, node, parent.getBeginColumn() + indentation, "BULK COLLECT INTO");
        checkEachChildOnNextLine(data, node, node.getBeginLine(), parent.getBeginColumn() + 7);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTFromClause node, Object data) {
        checkIndentation(data, node, node.getParent().getBeginColumn() + indentation, "FROM");
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTJoinClause node, Object data) {
        // first child is the table reference
        Node tableReference = node.getChild(0);

        // remaining children are joins
        int lineNumber = tableReference.getBeginLine();
        for (int i = 1; i < node.getNumChildren(); i++) {
            lineNumber++;
            Node child = node.getChild(i);
            if (child.getBeginLine() != lineNumber) {
                addViolationWithMessage(data, child, child.getXPathNodeName() + " should be on line " + lineNumber);
            }
            List<ASTEqualityExpression> conditions = child.findDescendantsOfType(ASTEqualityExpression.class);

            if (conditions.size() == 1) {
                // one condition should be on the same line
                ASTEqualityExpression singleCondition = conditions.get(0);
                if (singleCondition.getBeginLine() != lineNumber) {
                    addViolationWithMessage(data, child,
                            "Join condition \"" + singleCondition.getImage() + "\" should be on line " + lineNumber);
                }
            } else {
                // each condition on a separate line
                for (ASTEqualityExpression singleCondition : conditions) {
                    lineNumber++;
                    if (singleCondition.getBeginLine() != lineNumber) {
                        addViolationWithMessage(data, child,
                                "Join condition \"" + singleCondition.getImage() + "\" should be on line "
                                        + lineNumber);
                    }
                }
            }
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTSubqueryOperation node, Object data) {
        // get previous sibling
        int thisIndex = node.getIndexInParent();
        Node prevSibling = node.getParent().getChild(thisIndex - 1);

        checkIndentation(data, node, prevSibling.getBeginColumn(), node.getImage());

        // it should also be on the next line
        if (node.getBeginLine() != prevSibling.getEndLine() + 1) {
            addViolationWithMessage(data, node,
                    node.getImage() + " should be on line " + (prevSibling.getEndLine() + 1));
        }

        return super.visit(node, data);
    }

    private int checkEachChildOnNextLine(Object data, Node parent, int firstLine, int indentation) {
        int currentLine = firstLine;
        for (int i = 0; i < parent.getNumChildren(); i++) {
            Node child = parent.getChild(i);
            String image = child.getImage();
            if (image == null && child.getNumChildren() > 0) {
                image = child.getChild(0).getImage();
            }
            if (child.getBeginLine() != currentLine) {
                addViolationWithMessage(data, child, image + " should be on line " + currentLine);
            } else if (i > 0 && child.getBeginColumn() != indentation) {
                addViolationWithMessage(data, child, image + " should begin at column " + indentation);
            }
            // next entry needs to be on the next line
            currentLine++;
        }
        return currentLine;
    }

    private void checkLineAndIndentation(Object data, Node node, int line, int indentation, String name) {
        if (node.getBeginLine() != line) {
            addViolationWithMessage(data, node, name + " should be on line " + line);
        } else if (node.getBeginColumn() != indentation) {
            addViolationWithMessage(data, node, name + " should begin at column " + indentation);
        }
    }

    private void checkIndentation(Object data, Node node, int indentation, String name) {
        if (node.getBeginColumn() != indentation) {
            addViolationWithMessage(data, node, name + " should begin at column " + indentation);
        }
    }

    @Override
    public Object visit(ASTFormalParameters node, Object data) {
        int parameterIndentation = node.getParent().getBeginColumn() + indentation;
        checkEachChildOnNextLine(data, node, node.getBeginLine() + 1, parameterIndentation);

        // check the data type alignment
        List<ASTFormalParameter> parameters = node.findChildrenOfType(ASTFormalParameter.class);
        if (parameters.size() > 1) {
            ASTDatatype first = parameters.get(0).getFirstChildOfType(ASTDatatype.class);
            for (int i = 1; first != null && i < parameters.size(); i++) {
                ASTDatatype nextType = parameters.get(i).getFirstChildOfType(ASTDatatype.class);
                if (nextType != null) {
                    checkIndentation(data, nextType, first.getBeginColumn(), "Type " + nextType.getImage());
                }
            }
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTDeclarativeSection node, Object data) {
        int variableIndentation = node.getNthParent(2).getBeginColumn() + 2 * indentation;
        int line = node.getBeginLine();

        List<ASTVariableOrConstantDeclarator> variables = node
                .findDescendantsOfType(ASTVariableOrConstantDeclarator.class);

        int datatypeIndentation = variableIndentation;
        if (!variables.isEmpty()) {
            ASTDatatype datatype = variables.get(0).getFirstChildOfType(ASTDatatype.class);
            if (datatype != null) {
                datatypeIndentation = datatype.getBeginColumn();
            }
        }

        for (ASTVariableOrConstantDeclarator variable : variables) {
            checkLineAndIndentation(data, variable, line, variableIndentation, variable.getImage());

            ASTDatatype datatype = variable.getFirstChildOfType(ASTDatatype.class);
            if (datatype != null) {
                checkIndentation(data, datatype, datatypeIndentation, "Type " + datatype.getImage());
            }

            line++;
        }

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTArgumentList node, Object data) {
        List<ASTArgument> arguments = node.findChildrenOfType(ASTArgument.class);

        if (node.getEndColumn() > 120) {
            addViolationWithMessage(data, node, "Line is too long, please split parameters on separate lines");
            return super.visit(node, data);
        }

        if (arguments.size() > 3) {
            // procedure calls with more than 3 parameters should use named parameters
            if (usesSimpleParameters(arguments)) {
                addViolationWithMessage(data, node,
                        "Procedure call with more than three parameters should use named parameters.");
            }

            // more than three parameters -> each parameter on a separate line
            int line = node.getBeginLine();
            int indentation = node.getBeginColumn();
            int longestParameterEndColumn = 0;
            for (ASTArgument argument : arguments) {
                checkLineAndIndentation(data, argument, line, indentation, "Parameter " + argument.getImage());
                line++;

                if (argument.getChild(0) instanceof ASTUnqualifiedID) {
                    if (argument.getChild(0).getEndColumn() > longestParameterEndColumn) {
                        longestParameterEndColumn = argument.getChild(0).getEndColumn();
                    }
                }
            }

            // now check for the indentation of the expressions
            int expectedBeginColumn = longestParameterEndColumn + 3 + "=> ".length();
            // take the indentation from the first one, if it is greater
            if (!arguments.isEmpty() && arguments.get(0).getNumChildren() == 2
                    && arguments.get(0).getChild(1).getBeginColumn() > expectedBeginColumn) {
                expectedBeginColumn = arguments.get(0).getChild(1).getBeginColumn();
            }
            for (ASTArgument argument : arguments) {
                if (argument.getNumChildren() == 2 && argument.getChild(0) instanceof ASTUnqualifiedID) {
                    Node expr = argument.getChild(1);
                    checkIndentation(data, expr, expectedBeginColumn, expr.getImage());
                }
            }

            // closing paranthesis should be on a new line
            Node primaryExpression = node.getNthParent(3);
            if (primaryExpression.getEndLine() != node.getEndLine() + 1) {
                addViolationWithMessage(data, primaryExpression, "Closing paranthesis should be on a new line.");
            }
        }

        return super.visit(node, data);
    }

    private boolean usesSimpleParameters(List<ASTArgument> arguments) {
        for (ASTArgument argument : arguments) {
            if (argument.getNumChildren() == 1) {
                return true;
            }
        }
        return false;
    }
}
