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

import static net.sourceforge.pmd.lang.rule.stat.StatisticalRule.MINIMUM_DESCRIPTOR;
import static net.sourceforge.pmd.lang.rule.stat.StatisticalRule.SIGMA_DESCRIPTOR;
import static net.sourceforge.pmd.lang.rule.stat.StatisticalRule.TOP_SCORE_DESCRIPTOR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.stat.StatisticalRule;

import junit.framework.AssertionFailedError;

/**
 * This class tests the Statistical Rules in PMD.
 *
 * <p>The idea is, that we fill up 999 datapoints into the Stat Rule, and then
 * throw random parameters at it.</p>
 *
 * <p>The three parameters which are checked are: sigma - # Sigmas over the mean.
 * topscore - Only the top 5 or so items. minimum - Only things of score 10 or
 * better</p>
 *
 * <p>When more than one parameter is lumped together, then we expect the one which
 * would return the fewest to determine what gets sent back.</p>
 *
 * <p>So, we throw each collection of parameters, where each one is a different
 * order into the system. We check the results off of what the smallest value
 * should be.</p>
 *
 * <p>If you are going to work with StatisticalRule any, please bump the
 * "NUM_TESTS" number up to something like 128. That way you are more likely to
 * identify problems. It is set low now to make building and running tests
 * easier (when we aren't touching the file.)</p>
 *
 * <p>Note also, that when verifying the Sigma, I wasn't quite able to determine
 * how many results it would return (it would vary from -2 to 2 of what I
 * expected.) That is what the delta parameter on the verify method takes. If
 * you can figure it out exactly, (without stealing code from the StatRule) then
 * feel free to change it and tighten the deltas.</p>
 */
public class StatisticalRuleTest {

    private static final int POINTS = 100;

    private DataPoint[] points = new DataPoint[POINTS];
    private MockStatisticalRule ruleUnderTest = null;
    // FIXME - why/when was this added. It was never set.
    private String testName = "";
    private Random random = new Random();

    public static final double MAX_MINIMUM = POINTS;
    public static final double NO_MINIMUM = -1.0;
    public static final double MAX_SIGMA = 5.0;
    public static final double NO_SIGMA = -1.0;
    public static final int MIN_TOPSCORE = 0;
    public static final int NO_TOPSCORE = -1;

    public static final double MEAN = 49.5;
    public static final double SIGMA = 29.0115;
    public static final int NUM_TESTS = 1;

    public static final double DELTA = 0.005;

    @Before
    public void setUp() {
        ruleUnderTest = new MockStatisticalRule();
        if (testName.endsWith("0")) {
            for (int i = 0; i < POINTS; i++) {
                points[i] = new DataPoint();
                points[i].setScore(1.0 * i);
                DummyNode s = new DummyNode(1);
                s.testingOnlySetBeginLine(i);
                s.testingOnlySetBeginColumn(1);
                points[i].setNode(s);
                points[i].setMessage("DataPoint[" + Integer.toString(i) + "]");

                ruleUnderTest.addDataPoint(points[i]);
            }
        } else if (testName.endsWith("1")) {
            for (int i = POINTS - 1; i >= 0; i--) {
                points[i] = new DataPoint();
                points[i].setScore(1.0 * i);
                DummyNode s = new DummyNode(1);
                s.testingOnlySetBeginLine(i);
                s.testingOnlySetBeginColumn(1);
                points[i].setNode(s);
                points[i].setMessage("DataPoint[" + Integer.toString(i) + "]");

                ruleUnderTest.addDataPoint(points[i]);
            }
        } else {
            List<DataPoint> lPoints = new ArrayList<>();
            for (int i = 0; i < POINTS; i++) {
                points[i] = new DataPoint();
                points[i].setScore(1.0 * i);
                DummyNode s = new DummyNode(1);
                s.testingOnlySetBeginLine(i);
                s.testingOnlySetBeginColumn(1);
                s.testingOnlySetBeginColumn(1);
                points[i].setNode(s);
                points[i].setMessage("DataPoint[" + Integer.toString(i) + "]");

                lPoints.add(points[i]);
            }

            Collections.shuffle(lPoints);
            for (int i = 0; i < POINTS; i++) {
                ruleUnderTest.addDataPoint(lPoints.get(i));
            }
        }

    }

    /**
     * This test verifies that the Stat rule creates a Metric, with the proper
     * values.
     */
    @Test
    public void testMetrics() {
        Report report = makeReport(ruleUnderTest);
        Iterator<Metric> metrics = report.metrics();

        assertTrue(metrics.hasNext());
        Metric m = metrics.next();

        assertEquals("net.sourceforge.pmd.stat.MockStatisticalRule", m.getMetricName());

        assertEquals(0.0, m.getLowValue(), 0.05);
        assertEquals(POINTS - 1.0, m.getHighValue(), 0.05);
        assertEquals(MEAN, m.getAverage(), 0.05);
        assertEquals(SIGMA, m.getStandardDeviation(), 0.05);
    }

    /**
     * This returns a Random value for Sigma which will return some values.
     */
    public double randomSigma() {
        return random.nextDouble() * 1.0;
    }

    /**
     * This returns a Random value for Sigma which value is greater than the
     * parameter.
     */
    public double randomSigma(int minimum) {
        double minSigma = ((POINTS - 1 - minimum) - MEAN) / SIGMA;

        if ((minSigma <= 0) || (minSigma > 2)) {
            return randomSigma();
        }

        return minSigma + (random.nextDouble() * (2 - minSigma));
    }

    /**
     * This returns the expected number of results when the Sigma rating is the
     * smallest.
     */
    public int expectedSigma(double sigma) {
        long expectedMin = Math.round(MEAN + (sigma * SIGMA));

        if (((POINTS - 1) - expectedMin) < 0) {
            return 0;
        }
        return (POINTS - 1) - (int) expectedMin;
    }

    /**
     * This generates a random minimum value for testing.
     */
    public double randomMinimum() {
        return random.nextDouble() * (POINTS - 1);
    }

    /**
     * This generates a random minimum value for which fewer results would be
     * returned.
     */
    public double randomMinimum(int minimum) {
        double diffTarget = 1.0 * (POINTS - 1 - minimum);
        return (random.nextDouble() * minimum) + diffTarget;
    }

    /**
     * This returns the expected number of reports.
     *
     * <p>If the Minimum comes in at 521.569 then we expect 522, 523, ... 999 will
     * pass.</p>
     */
    public int expectedMinimum(double minimum) {
        Double d = Double.valueOf(minimum);
        return POINTS - 1 - d.intValue();
    }

    @Test
    public void testExpectedMinimum() {
        for (int i = 0; i < POINTS - 1; i++) {
            assertEquals("Integer Min", POINTS - 1 - i, expectedMinimum(i * 1.0));
            assertEquals("Double Min", POINTS - 1 - i, expectedMinimum((i * 1.0) + 0.5));
        }
    }

    /**
     * This returns a random value for Top Score.
     */
    public int randomTopScore() {
        return random.nextInt(POINTS - 1);
    }

    /**
     * This will return a random value for the Top Score which will return more
     * than the minimum provided.
     */
    public int randomTopScore(double target) {
        if (target < 0) {
            return 0;
        }

        return random.nextInt(Double.valueOf(target).intValue());
    }

    /**
     * This will return the expected number of results with the given Top Score.
     */
    public int expectedTopScore(int target) {
        return target;
    }

    // Test Single Datapoint
    @Test
    public void testSingleDatapoint() {
        StatisticalRule rule = new MockStatisticalRule();

        DataPoint point = new DataPoint();
        point.setScore(POINTS + 1.0);
        DummyNode s = new DummyNode(1);
        s.testingOnlySetBeginLine(POINTS + 1);
        s.testingOnlySetBeginColumn(1);
        point.setNode(s);
        point.setMessage("SingleDataPoint");

        rule.setProperty(MINIMUM_DESCRIPTOR, (double) POINTS);

        rule.addDataPoint(point);

        Report report = makeReport(rule);

        assertEquals("Expecting only one result", 1, report.size());
    }

    // Okay, we have three properties we need to
    // test in Combination:
    // S = Sigma
    // T = Top Score
    // M = Minimum
    //
    // They are listed in decreasing order of what
    // to expect.
    //
    // Thus testSM() should have the Sigma less than
    // the minimum, so we expect the Minimum # of results.
    //

    @Test
    public void testS() {
        verifyResults(MAX_SIGMA, NO_MINIMUM, NO_TOPSCORE, 0, 2);

        for (int i = 0; i < NUM_TESTS; i++) {
            double sigma = randomSigma();
            verifyResults(sigma, -1.0, -1, expectedSigma(sigma), 2);
        }
    }

    @Test
    public void testS1() {
        testS();
    }

    @Test
    public void testS2() {
        testS();
    }

    @Test
    public void testS3() {
        testS();
    }

    @Test
    public void testS4() {
        testS();
    }

    @Test
    public void testS5() {
        testS();
    }

    @Test
    public void testT() {
        verifyResults(NO_SIGMA, NO_MINIMUM, MIN_TOPSCORE, 0, 0);

        for (int i = 0; i < NUM_TESTS; i++) {
            int topScore = randomTopScore();
            verifyResults(-1.0, -1.0, topScore, expectedTopScore(topScore), 0);
        }
    }

    @Test
    public void testT1() {
        testT();
    }

    @Test
    public void testT2() {
        testT();
    }

    @Test
    public void testT3() {
        testT();
    }

    @Test
    public void testT4() {
        testT();
    }

    @Test
    public void testT5() {
        testT();
    }

    @Test
    public void testM() {
        verifyResults(NO_SIGMA, MAX_MINIMUM, NO_TOPSCORE, 0, 0);

        for (int i = 0; i < NUM_TESTS; i++) {
            double minimum = randomMinimum();
            verifyResults(-1.0, minimum, -1, expectedMinimum(minimum), 0);
        }
    }

    @Test
    public void testM1() {
        testM();
    }

    @Test
    public void testM2() {
        testM();
    }

    @Test
    public void testM3() {
        testM();
    }

    @Test
    public void testM4() {
        testM();
    }

    @Test
    public void testM5() {
        testM();
    }

    @Test
    public void testST() {
        verifyResults(randomSigma(), NO_MINIMUM, MIN_TOPSCORE, 0, 0);

        for (int i = 0; i < NUM_TESTS; i++) {
            double sigma = randomSigma();
            int topScore = randomTopScore(expectedSigma(sigma));

            verifyResults(sigma, NO_MINIMUM, topScore, expectedTopScore(topScore), 0);
        }
    }

    @Test
    public void testST1() {
        testST();
    }

    @Test
    public void testST2() {
        testST();
    }

    @Test
    public void testST3() {
        testST();
    }

    @Test
    public void testST4() {
        testST();
    }

    @Test
    public void testST5() {
        testST();
    }

    @Test
    public void testTS() {
        verifyResults(MAX_SIGMA, NO_MINIMUM, randomTopScore(), 0, 0);

        for (int i = 0; i < NUM_TESTS; i++) {
            int topScore = randomTopScore();
            double sigma = randomSigma(expectedTopScore(topScore));

            verifyResults(sigma, -1.0, topScore, expectedSigma(sigma), 2);
        }
    }

    @Test
    public void testTS1() {
        testTS();
    }

    @Test
    public void testTS2() {
        testTS();
    }

    @Test
    public void testTS3() {
        testTS();
    }

    @Test
    public void testTS4() {
        testTS();
    }

    @Test
    public void testTS5() {
        testTS();
    }

    @Test
    public void testSM() {
        verifyResults(randomSigma(), MAX_MINIMUM, NO_TOPSCORE, 0, 0);
        for (int i = 0; i < NUM_TESTS; i++) {
            double sigma = randomSigma();
            double minimum = randomMinimum(expectedSigma(sigma));

            verifyResults(sigma, minimum, -1, expectedMinimum(minimum), 0);
        }

    }

    @Test
    public void testSM1() {
        testSM();
    }

    @Test
    public void testSM2() {
        testSM();
    }

    @Test
    public void testSM3() {
        testSM();
    }

    @Test
    public void testSM4() {
        testSM();
    }

    @Test
    public void testSM5() {
        testSM();
    }

    @Test
    public void testMS() {
        verifyResults(MAX_SIGMA, randomMinimum(), NO_TOPSCORE, 0, 0);
        for (int i = 0; i < NUM_TESTS; i++) {
            double minimum = randomMinimum();
            double sigma = randomSigma(expectedMinimum(minimum));

            verifyResults(sigma, minimum, -1, expectedSigma(sigma), 2);
        }
    }

    @Test
    public void testMS1() {
        testMS();
    }

    @Test
    public void testMS2() {
        testMS();
    }

    @Test
    public void testMS3() {
        testMS();
    }

    @Test
    public void testMS4() {
        testMS();
    }

    @Test
    public void testMS5() {
        testMS();
    }

    @Test
    public void testTM() {
        verifyResults(NO_SIGMA, MAX_MINIMUM, randomTopScore(), 0, 0);
        for (int i = 0; i < NUM_TESTS; i++) {
            int topScore = randomTopScore();
            double minimum = randomMinimum(expectedTopScore(topScore));

            verifyResults(NO_SIGMA, minimum, topScore, expectedMinimum(minimum), 0);
        }
    }

    @Test
    public void testTM1() {
        testTM();
    }

    @Test
    public void testTM2() {
        testTM();
    }

    @Test
    public void testTM3() {
        testTM();
    }

    @Test
    public void testTM4() {
        testTM();
    }

    @Test
    public void testTM5() {
        testTM();
    }

    @Test
    public void testMT() {
        verifyResults(NO_SIGMA, randomMinimum(), MIN_TOPSCORE, 0, 0);
        for (int i = 0; i < NUM_TESTS; i++) {
            double minimum = randomMinimum();
            int topScore = randomTopScore(expectedMinimum(minimum));

            verifyResults(NO_SIGMA, minimum, topScore, expectedTopScore(topScore), 0);
        }
    }

    @Test
    public void testMT1() {
        testMT();
    }

    @Test
    public void testMT2() {
        testMT();
    }

    @Test
    public void testMT3() {
        testMT();
    }

    @Test
    public void testMT4() {
        testMT();
    }

    @Test
    public void testMT5() {
        testMT();
    }

    @Test
    public void testSTM() {
        double sigma = randomSigma();
        verifyResults(sigma, MAX_MINIMUM, randomTopScore(expectedSigma(sigma)), 0, 0);

        for (int i = 0; i < NUM_TESTS; i++) {
            sigma = randomSigma();
            int topScore = randomTopScore(expectedSigma(sigma));
            double minimum = randomMinimum(expectedTopScore(topScore));

            verifyResults(sigma, minimum, topScore, expectedMinimum(minimum), 0);
        }
    }

    @Test
    public void testSTM1() {
        testSTM();
    }

    @Test
    public void testSTM2() {
        testSTM();
    }

    @Test
    public void testSTM3() {
        testSTM();
    }

    @Test
    public void testSTM4() {
        testSTM();
    }

    @Test
    public void testSTM5() {
        testSTM();
    }

    @Test
    public void testSMT() {
        double sigma = randomSigma();
        verifyResults(sigma, randomMinimum(expectedSigma(sigma)), MIN_TOPSCORE, 0, 0);

        for (int i = 0; i < NUM_TESTS; i++) {
            sigma = randomSigma();
            double minimum = randomMinimum(expectedSigma(sigma));
            int topScore = randomTopScore(expectedMinimum(minimum));

            verifyResults(sigma, minimum, topScore, expectedTopScore(topScore), 0);
        }
    }

    @Test
    public void testSMT1() {
        testSMT();
    }

    @Test
    public void testSMT2() {
        testSMT();
    }

    @Test
    public void testSMT3() {
        testSMT();
    }

    @Test
    public void testSMT4() {
        testSMT();
    }

    @Test
    public void testSMT5() {
        testSMT();
    }

    @Test
    // because of random failures during continuous integration,
    // tests are disabled in regress mode until somebody figures out
    // what the tests are supposed to measure and why they sometime fail
    @Ignore("random failures during continuous integration")
    public void testTSM() {
        int topScore = randomTopScore();
        verifyResults(randomSigma(expectedTopScore(topScore)), MAX_MINIMUM, topScore, 0, 0);

        for (int i = 0; i < NUM_TESTS; i++) {
            topScore = randomTopScore();
            double sigma = randomSigma(expectedTopScore(topScore));
            double minimum = randomMinimum(expectedSigma(sigma));

            verifyResults(sigma, minimum, topScore, expectedMinimum(minimum), 0);
        }
    }

    @Test
    @Ignore("random failures during continuous integration")
    public void testTSM1() {
        testTSM();
    }

    @Test
    @Ignore("random failures during continuous integration")
    public void testTSM2() {
        testTSM();
    }

    @Test
    @Ignore("random failures during continuous integration")
    public void testTSM3() {
        testTSM();
    }

    @Test
    @Ignore("random failures during continuous integration")
    public void testTSM4() {
        testTSM();
    }

    @Test
    @Ignore("random failures during continuous integration")
    public void testTSM5() {
        testTSM();
    }

    @Test
    public void testTMS() {
        int topScore = randomTopScore();
        verifyResults(MAX_SIGMA, randomMinimum(expectedTopScore(topScore)), topScore, 0, 0);

        for (int i = 0; i < NUM_TESTS; i++) {
            topScore = randomTopScore();
            double minimum = randomMinimum(expectedTopScore(topScore));
            double sigma = randomSigma(expectedMinimum(minimum));

            verifyResults(sigma, minimum, topScore, expectedSigma(sigma), 2);
        }
    }

    @Test
    public void testTMS1() {
        testTMS();
    }

    @Test
    public void testTMS2() {
        testTMS();
    }

    @Test
    public void testTMS3() {
        testTMS();
    }

    @Test
    public void testTMS4() {
        testTMS();
    }

    @Test
    public void testTMS5() {
        testTMS();
    }

    /**
     * Verifies what happens when you pass these parameters into the thing.
     * DELTA is the amount of error allowed. Usually DELTA is only used for
     * Sigma, as we really can't calculate it exactly.
     */

    public void verifyResults(double sigma, double minimum, int topScore, int expected, int delta) {
        try {
            setUp();
            if (sigma >= 0) {
                ruleUnderTest.setProperty(SIGMA_DESCRIPTOR, sigma);
            }

            if (minimum >= 0) {
                ruleUnderTest.setProperty(MINIMUM_DESCRIPTOR, minimum);
            }

            if (topScore >= 0) {
                ruleUnderTest.setProperty(TOP_SCORE_DESCRIPTOR, topScore);
            }

            Report report = makeReport(ruleUnderTest);
            if (delta == 0) {
                assertEquals(
                        "Unexpected number of results: sigma= " + Double.toString(sigma) + " min= "
                                + Double.toString(minimum) + " topscore= " + Integer.toString(topScore),
                        expected, report.size());
            } else {
                String assertStr = "Unexpected number of results: sigma= " + Double.toString(sigma) + " min= "
                        + Double.toString(minimum) + " topscore= " + Integer.toString(topScore) + " expected= "
                        + Integer.toString(expected) + " +/- " + Integer.toString(delta) + " actual-result= "
                        + report.size();

                assertTrue(assertStr, report.size() >= (expected - delta));
                assertTrue(assertStr, report.size() <= (expected + delta));
            }
        } catch (AssertionFailedError afe) {
            System.err.println("******** " + testName + " ***********");
            if (sigma != NO_SIGMA) {
                System.err.println(
                        "SIGMA: " + Double.toString(sigma) + " EXPECT: " + Integer.toString(expectedSigma(sigma)));
            }

            if (minimum != NO_MINIMUM) {
                System.err.println(
                        "MIN: " + Double.toString(minimum) + " EXPECT: " + Integer.toString(expectedMinimum(minimum)));
            }

            if (topScore != NO_TOPSCORE) {
                System.err.println("TOP: " + Integer.toString(topScore) + " EXPECT: "
                        + Integer.toString(expectedTopScore(topScore)));
            }

            throw afe;

        }
    }

    public Report makeReport(Rule rule) {
        List<Node> list = new ArrayList<>();
        Report report = new Report();

        RuleContext ctx = new RuleContext();
        ctx.setReport(report);
        ctx.setSourceCodeFile(new File(testName));
        ctx.setLanguageVersion(LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getDefaultVersion());

        rule.apply(list, ctx);

        return report;
    }
}
