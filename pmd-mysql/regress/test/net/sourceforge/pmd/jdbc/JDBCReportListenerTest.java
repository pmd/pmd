/*
 * 
 */

package test.net.sourceforge.pmd.jdbc;

import com.mockobjects.sql.MockConnection;
import com.mockobjects.sql.MockDriver;
import com.mockobjects.sql.MockPreparedStatement;
import com.mockobjects.sql.MockSingleRowResultSet;
import junit.framework.TestCase;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.jdbc.JDBCReportListener;
import net.sourceforge.pmd.stat.Metric;
import test.net.sourceforge.pmd.MockRule;

import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class JDBCReportListenerTest extends TestCase {
    private MockDriver driver = null;
    private MockPreparedStatement violInsert = null;
    private MockPreparedStatement metInsert = null;

    public void setUp() {
	driver = new MockDriver();
	try {
	    DriverManager.registerDriver( driver );
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void tearDown() {
	try {
	    DriverManager.deregisterDriver( driver );
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private class MockInsertPMDRuns
	extends MockPreparedStatement
    {
	private int runId = 0;
	public MockInsertPMDRuns( int projectId, int runId ) {
	    //	    setExpectedQueryString("INSERT INTO PMD_RUNS (PROJECT_ID, RUN_DATE) VALUES (?, ?)");
	    addExpectedSetParameter( 1, projectId );
	    this.runId = runId;
	}

	public void setDate( int position, Date dateTime ) {
	    assertEquals("Date in wrong position.", 2, position);
	    assertTrue( "Didn't add the current date.",
			dateTime.getTime() > System.currentTimeMillis() - 5000);
	    assertTrue( "Set a future date.",
			dateTime.getTime() < System.currentTimeMillis() + 5000);
	}

	public ResultSet getGeneratedKeys() {
	    MockSingleRowResultSet RC = new MockSingleRowResultSet();
	    RC.addExpectedIndexedValues( new Object[] { new Integer(runId) });

	    return RC;
	}

    }

    private class MockPMDConx
	extends MockConnection
    {
	public MockPMDConx( int projectId, int runId ) {
	    super();
	    setupAddPreparedStatement( new MockInsertPMDRuns( projectId, runId ) );
	    violInsert = new MockPreparedStatement();
	    setupAddPreparedStatement( violInsert );
	    
	    metInsert = new MockPreparedStatement();
	    setupAddPreparedStatement( metInsert );
	}

	public PreparedStatement prepareStatement( String sql, int returnKeys )
	    throws SQLException
	{
	    assertEquals( "Not set to return generated keys.", 
			  returnKeys, Statement.RETURN_GENERATED_KEYS );
	    return prepareStatement( sql );
	}

    }

    public JDBCReportListener getIUT(int projectId) 
	throws SQLException
    {
	return new JDBCReportListener( "jdbc:mock:getIUT",
				       "iut_user", "iut_pw", projectId );
    }

    public void testLongConstructor() throws Throwable {
	MockConnection conx = new MockPMDConx(1, 5);
	driver.setupConnect( conx );

	new JDBCReportListener("jdbc:mock:testLongConstructor", "tlc_user", "tlc_pw", 1);
    }

    public void testPropConstructor() throws Throwable {
	MockConnection conx = new MockPMDConx(2, 8);
	driver.setupConnect( conx );

	Properties props = new Properties();
	props.put( JDBCReportListener.JDBC_URL, "jdbc:mock:testPropConstructor" );
	props.put( JDBCReportListener.JDBC_USER, "tpc_user" );
	props.put( JDBCReportListener.JDBC_PASSWORD, "tpc_pw" );
	props.put( JDBCReportListener.JDBC_PROJECTID, "2");

	new JDBCReportListener(props);
    }

    public void testSingleViolation() throws Throwable {
	MockConnection conx = new MockPMDConx( 16, 36 );
	driver.setupConnect( conx );

	violInsert.addExpectedSetParameter( 1, 36 ); // Run ID
	violInsert.addExpectedSetParameter( 2, "viol1" ); // Rule
	violInsert.addExpectedSetParameter( 3, "fileA" );      // File Name
	violInsert.addExpectedSetParameter( 4, 256 );          // Line Number
	violInsert.addExpectedSetParameter( 5, "packageB" );   // Package Name
	violInsert.addExpectedSetParameter( 6, "classC" );     // Class Name
	violInsert.addExpectedSetParameter( 7, "msg" );
	
	JDBCReportListener IUT = getIUT( 16 );

	RuleContext ctx = new RuleContext();
	ctx.setSourceCodeFilename( "fileA" );
	ctx.setPackageName( "packageB" );
	ctx.setClassName( "classC" );

	IUT.ruleViolationAdded( new RuleViolation( new MockRule("viol1", "Description", "msg" ), 
						   256, ctx ));
    }

    public void testMultiViolation() throws Throwable {
	MockConnection conx = new MockPMDConx( 16, 36 );
	driver.setupConnect( conx );

	JDBCReportListener IUT = getIUT( 16 );

	violInsert.addExpectedSetParameter( 1, 36 ); // Run ID
	violInsert.addExpectedSetParameter( 2, "viol1" ); // Rule
	violInsert.addExpectedSetParameter( 3, "fileA" );      // File Name
	violInsert.addExpectedSetParameter( 4, 256 );          // Line Number
	violInsert.addExpectedSetParameter( 5, "packageB" );   // Package Name
	violInsert.addExpectedSetParameter( 6, "classC" );     // Class Name
	violInsert.addExpectedSetParameter( 7, "msg" );
	
	RuleContext ctx = new RuleContext();
	ctx.setSourceCodeFilename( "fileA" );
	ctx.setPackageName( "packageB" );
	ctx.setClassName( "classC" );

	IUT.ruleViolationAdded( new RuleViolation( new MockRule("viol1", "Description", "msg" ), 
						   256, ctx ));

	violInsert.addExpectedSetParameter( 1, 36 ); // Run ID
	violInsert.addExpectedSetParameter( 2, "viol1a" ); // Rule
	violInsert.addExpectedSetParameter( 3, "fileAa" );      // File Name
	violInsert.addExpectedSetParameter( 4, 2561 );          // Line Number
	violInsert.addExpectedSetParameter( 5, "packageBa" );   // Package Name
	violInsert.addExpectedSetParameter( 6, "classCa" );     // Class Name
	violInsert.addExpectedSetParameter( 7, "msga" );
	
	ctx = new RuleContext();
	ctx.setSourceCodeFilename( "fileAa" );
	ctx.setPackageName( "packageBa" );
	ctx.setClassName( "classCa" );

	IUT.ruleViolationAdded( new RuleViolation( new MockRule("viol1a", "Descriptiona", "msga" ), 
						   2561, ctx ));
    }

    public void testSingleMetric() throws Throwable {
	MockConnection conx = new MockPMDConx( 18, 378 );
	driver.setupConnect( conx );

	metInsert.addExpectedSetParameter( 1, 378 );
	metInsert.addExpectedSetParameter( 2, "single" );
	metInsert.addExpectedSetParameter( 3, new Double( 1.0 ));
	metInsert.addExpectedSetParameter( 4, new Double( 6.0 ));
	metInsert.addExpectedSetParameter( 5, new Double( 3.0 ));
	metInsert.addExpectedSetParameter( 6, new Double( 1.5 ));

	JDBCReportListener IUT = getIUT( 18 );

	IUT.metricAdded( new Metric("single", 5, 30, 1.0, 6.0, 3.0, 1.5) );
    }

    public void testMultiMetric() throws Throwable {
	MockConnection conx = new MockPMDConx( 18, 378 );
	driver.setupConnect( conx );

	JDBCReportListener IUT = getIUT( 18 );

	metInsert.addExpectedSetParameter( 1, 378 );
	metInsert.addExpectedSetParameter( 2, "first" );
	metInsert.addExpectedSetParameter( 3, new Double( 1.0 ));
	metInsert.addExpectedSetParameter( 4, new Double( 6.0 ));
	metInsert.addExpectedSetParameter( 5, new Double( 3.0 ));
	metInsert.addExpectedSetParameter( 6, new Double( 1.5 ));

	IUT.metricAdded( new Metric("first", 5, 30, 1.0, 6.0, 3.0, 1.5) );

	metInsert.addExpectedSetParameter( 1, 378 );
	metInsert.addExpectedSetParameter( 2, "second" );
	metInsert.addExpectedSetParameter( 3, new Double( 1.01 ));
	metInsert.addExpectedSetParameter( 4, new Double( 6.01 ));
	metInsert.addExpectedSetParameter( 5, new Double( 3.01 ));
	metInsert.addExpectedSetParameter( 6, new Double( 1.51 ));

	IUT.metricAdded( new Metric("second", 7, 32.5, 1.01, 6.01, 3.01, 1.51) );
    }

}
