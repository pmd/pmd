package net.sourceforge.pmd.jdbc;

import net.sourceforge.pmd.ReportListener;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.stat.Metric;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * JDBCReportListener relies on several System Properties
 * in order to work properly.  They are:
 *
 * net.sourceforge.pmd.jdbc.url = JDBC URL of database to connect to.
 * net.sourceforge.pmd.jdbc.user = Username to log into database with.
 * net.sourceforge.pmd.jdbc.password = Password to use for logging into the database.
 * net.sourceforge.pmd.jdbc.project_id = Identifier of the project being run.
 * 
 * Naturally, you can just call the apropriate initializer if you don't
 * want to use the System Properties. . .
 *
 * It is up to the outside application to ensure that its JDBC driver
 * is registered with DriverManager.  I believe different drivers register
 * differently, so I don't really want to do this myself.
 */

public class JDBCReportListener 
    extends Object
    implements ReportListener {

    public static final String JDBC_URL       = "net.sourceforge.pmd.jdbc.url";
    public static final String JDBC_USER      = "net.sourceforge.pmd.jdbc.user";
    public static final String JDBC_PASSWORD  = "net.sourceforge.pmd.jdbc.password";
    public static final String JDBC_PROJECTID = "net.sourceforge.pmd.jdbc.projectid";

    private Connection conx;
    private PreparedStatement violStmt;
    private PreparedStatement metricStmt;

    private int runId = -1;

    public JDBCReportListener() 
	throws SQLException
    {
	if (System.getProperty( JDBC_PROJECTID ) != null) {
	    initialize( System.getProperty( JDBC_URL ), 
		        System.getProperty( JDBC_USER ),
		        System.getProperty( JDBC_PASSWORD ),
		        Integer.parseInt(System.getProperty( JDBC_PROJECTID )));
	} else {
	    initialize( System.getProperty( JDBC_URL ),
		        System.getProperty( JDBC_USER ),
		        System.getProperty( JDBC_PASSWORD ),
		        0 );
	}
    }

    public JDBCReportListener(Properties props) 
	throws SQLException 
    {
	initialize( props.getProperty( JDBC_URL ),
		    props.getProperty( JDBC_USER ),
		    props.getProperty( JDBC_PASSWORD ),
		    Integer.parseInt(props.getProperty( JDBC_PROJECTID )));
    }

    public JDBCReportListener( String url, 
			       String user, String password, int projectId ) 
	throws SQLException
    {
	initialize( url, user, password, projectId );
    }

    private void initialize( String url, String user, String password, int projectId ) 
	throws SQLException
    {
	PreparedStatement ins = null;
	ResultSet keys = null;

	try {
	    conx = DriverManager.getConnection( url, user, password );
	    
 	    ins = conx.prepareStatement("INSERT INTO PMD_RUNS (PROJECT_ID, RUN_DATE) VALUES (?, ?)",
 					Statement.RETURN_GENERATED_KEYS);
 	    ins.setInt(1, projectId);
 	    ins.setDate(2, new Date( System.currentTimeMillis() ));

 	    ins.executeUpdate();
	    
 	    keys = ins.getGeneratedKeys();
	    keys.next();
	    runId = keys.getInt(1);

	    violStmt = conx.prepareStatement("INSERT INTO PMD_VIOLATIONS " +
					     "(RUN_ID, RULE, FILENAME, " +
					     "LINE_NUMBER, PACKAGE, CLASS, DESCRIPTION) " +
					     "VALUES (?, ?, ?, ?, ?, ?, ?)" );
	    metricStmt = conx.prepareStatement("INSERT INTO PMD_METRICS " +
					       "(RUN_ID, METRIC, LOW, HIGH, AVERAGE, DEVIATION) " +
					       "VALUES (?, ?, ?, ?, ?, ?)" );

	} catch (SQLException ex) {
	    if (conx != null) { conx.close(); }
	    throw ex;
	} finally {
	    if (keys != null) { keys.close(); }
	    if (ins != null) { ins.close(); }
	}
    }

    public void ruleViolationAdded( RuleViolation ruleViolation ) {
	try {
	    violStmt.setInt( 1, runId );
	    violStmt.setString( 2, ruleViolation.getRule().getName() );
	    violStmt.setString( 3, ruleViolation.getFilename() );
	    violStmt.setInt( 4, ruleViolation.getLine() );
	    violStmt.setString( 5, ruleViolation.getPackageName() );
	    violStmt.setString( 6, ruleViolation.getClassName() );
	    violStmt.setString( 7, ruleViolation.getDescription() );

	    violStmt.execute();
	} catch (Exception e) {
	    throw new RuntimeException( e );
	}
    }
    public void metricAdded( Metric metric ) {
	try {
 	    metricStmt.setInt( 1, runId );
 	    metricStmt.setString( 2, metric.getMetricName() );
 	    metricStmt.setDouble( 3, metric.getLowValue() );
 	    metricStmt.setDouble( 4, metric.getHighValue() );
 	    metricStmt.setDouble( 5, metric.getAverage() );
 	    metricStmt.setDouble( 6, metric.getStandardDeviation() );

 	    metricStmt.execute();
	} catch (Exception e) {
	    throw new RuntimeException( e );
	}
    }
}
