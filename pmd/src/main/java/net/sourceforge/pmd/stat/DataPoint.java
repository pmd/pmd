/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.stat;

import java.util.Random;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * @author David Dixon-Peugh
 *         Aug 8, 2002 DataPoint.java
 */
public class DataPoint implements Comparable<DataPoint> {

    private Node node;
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

    public int compareTo(DataPoint rhs) {
        Double lhsScore = Double.valueOf(score);
        Double rhsScore = Double.valueOf(rhs.getScore());
        if (lhsScore.doubleValue() != rhsScore.doubleValue()) {
            return lhsScore.compareTo(rhsScore);
        }
        return random - rhs.random;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
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
