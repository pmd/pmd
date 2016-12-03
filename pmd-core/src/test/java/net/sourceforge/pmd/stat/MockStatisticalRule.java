/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
/**
 * <copyright>
 *  Copyright 1997-2002 BBNT Solutions, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency (DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published by
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
 *
 * Created on Aug 26, 2002
 */

package net.sourceforge.pmd.stat;

import java.util.List;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.stat.StatisticalRule;
import net.sourceforge.pmd.lang.rule.stat.StatisticalRuleHelper;

public class MockStatisticalRule extends FooRule implements StatisticalRule {

    private StatisticalRuleHelper helper;

    public MockStatisticalRule() {
        helper = new StatisticalRuleHelper(this);
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public void apply(List<? extends Node> nodes, RuleContext ctx) {
        super.apply(nodes, ctx);
        helper.apply(ctx);
    }

    @Override
    public void addDataPoint(DataPoint point) {
        helper.addDataPoint(point);
    }

    @Override
    public Object[] getViolationParameters(DataPoint point) {
        return null;
    }
}
