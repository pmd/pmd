/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.stat;

import net.sourceforge.pmd.ast.SimpleNode;

import java.util.Random;

/**
 * @author David Dixon-Peugh
 *         Aug 8, 2002 DataPoint.java
 */
public class DataPoint implements java.lang.Comparable {

    private SimpleNode node;
    private int random;
    private double score;
    private String message;

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

    public SimpleNode getNode() {
        return node;
    }

    public void setNode(SimpleNode node) {
        this.node = node;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
