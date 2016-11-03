package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Created by waqas716 on 02/11/2016.
 */
public class FeatureEnvyRule extends AbstractJavaRule {
    private static final int FEW_THRESHOLD = 5;

    /**
     * One third is a low value. See: Lanza. Object-Oriented Metrics in
     * Practice. Page 17.
     */
    private static final double ONE_THIRD_THRESHOLD = 1.0 / 3.0;

    private static int ATFD = 0;
    private static int LAA = 0;
    private static int FDP = 0;

}
