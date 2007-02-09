package test.net.sourceforge.pmd.stat;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * tests for the net.sourceforge.pmd.stat package
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
@RunWith(Suite.class)
@SuiteClasses({MetricTest.class, StatisticalRuleTest.class})
public class StatTests {
}


/*
 * $Log$
 * Revision 1.4  2007/02/09 01:38:09  allancaplan
 * Moving to JUnit 4
 *
 * Revision 1.3  2006/02/10 14:26:27  tomcopeland
 * Huge reformatting checkin
 *
 * Revision 1.2  2006/02/10 14:15:22  tomcopeland
 * Latest source from Pieter, everything compiles and all the tests pass with the exception of a few missing rules in basic-jsp.xml
 *
 * Revision 1.1  2003/09/29 14:32:32  tomcopeland
 * Committed regression test suites, thanks to Boris Gruschko
 *
 */
