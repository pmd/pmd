/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
/**
 * Created on Aug 29, 2002
 */
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.ast.ASTStatement;
import net.sourceforge.pmd.ast.ASTSwitchLabel;
import net.sourceforge.pmd.ast.ASTSwitchStatement;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.stat.StatisticalRule;

/**
 * @author dpeugh
 *
 * Switch Density - This is the number of statements over the
 * number of cases within a switch.  The higher the value, the
 * more work each case is doing.
 *
 * Its my theory, that when the Switch Density is high, you should
 * start looking at Subclasses or State Pattern to alleviate the
 * problem.
 */
public class SwitchDensityRule extends StatisticalRule {
    private class SwitchDensity {
        private int labels = 0;
        private int stmts = 0;

        public SwitchDensity() {
        }

        public void addSwitchLabel() {
            labels++;
        }

        public void addStatement() {
            stmts++;
        }

        public void addStatements(int stmtCount) {
            stmts += stmtCount;
        }

        public int getStatementCount() {
            return stmts;
        }

        public double getDensity() {
            if (labels == 0) {
                return 0;
            }
            return 1.0 * (stmts / labels);
        }
    }

    public SwitchDensityRule() {
        super();
    }

    public Object visit(ASTSwitchStatement node, Object data) {
        SwitchDensity oldData = null;

        if (data instanceof SwitchDensity) {
            oldData = (SwitchDensity) data;
        }

        SwitchDensity density = new SwitchDensity();

        node.childrenAccept(this, density);

        DataPoint point = new DataPoint();
        point.setLineNumber(node.getBeginLine());
        point.setScore(density.getDensity());
        point.setRule(this);
        point.setMessage(getMessage());

        addDataPoint(point);

        if (data instanceof SwitchDensity) {
            ((SwitchDensity) data).addStatements(density.getStatementCount());
        }
        return oldData;
    }

    public Object visit(ASTStatement statement, Object data) {
        if (data instanceof SwitchDensity) {
            ((SwitchDensity) data).addStatement();
        }

        statement.childrenAccept(this, data);

        return data;
    }

    public Object visit(ASTSwitchLabel switchLabel, Object data) {
        if (data instanceof SwitchDensity) {
            ((SwitchDensity) data).addSwitchLabel();
        }

        switchLabel.childrenAccept(this, data);
        return data;
    }
}
