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
 */
package net.sourceforge.pmd.stat;

import net.sourceforge.pmd.Rule;

import java.util.Random;

/**
 * @author David Dixon-Peugh
 * Aug 8, 2002 DataPoint.java
 */
public class DataPoint implements java.lang.Comparable {
    private int lineNumber;
    private int random;
    private double score;
    private String message;
    private Rule rule;

    /**
     * Constructor for DataPoint.
     */
    public DataPoint() {
        super();
        // Random number is so that the TreeSet doesn't
        // whack things with the same score.
        Random rand = new Random();
        random = rand.nextInt(11061973);
    }

    public int compareTo(Object object) {

        DataPoint rhs = (DataPoint) object;

        Double lhsScore = new Double(score);
        Double rhsScore = new Double(rhs.getScore());

        if (lhsScore.doubleValue() != rhsScore.doubleValue()) {
            return lhsScore.compareTo(rhsScore);
        }

        Integer lhsRand = new Integer(random);
        Integer rhsRand = new Integer(rhs.random);

        return lhsRand.compareTo(rhsRand);
    }

    /**
     * Returns the lineNumber.
     * @return int
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Sets the lineNumber.
     * @param lineNumber The lineNumber to set
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Returns the message.
     * @return String
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the rule.
     * @return Rule
     */
    public Rule getRule() {
        return rule;
    }

    /**
     * Sets the message.
     * @param message The message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Sets the rule.
     * @param rule The rule to set
     */
    public void setRule(Rule rule) {
        this.rule = rule;
    }

    /**
     * Returns the score.
     * @return double
     */
    public double getScore() {
        return score;
    }

    /**
     * Sets the score.
     * @param score The score to set
     */
    public void setScore(double score) {
        this.score = score;
    }

    /**
     * Sets the score.
     * @param score The score to set
     */
    public void setScore(int score) {
        this.score = (double) score;
    }

}
