/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.stat;

import java.util.Random;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Datapoint used for rules that deal with metrics.
 * @author David Dixon-Peugh
 * @since Aug 8, 2002
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

    /**
     * Compares this data point with the given datapoint.
     * @param rhs the other data point
     * @return 0 if equal; a value less than 0 if this point's score is smaller than the other data point;
     * a value greater than 0 if this point's score is greater than the other data point.
     */
    public int compareTo(DataPoint rhs) {
        if (score != rhs.getScore()) {
            return Double.compare(score, rhs.getScore());
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
