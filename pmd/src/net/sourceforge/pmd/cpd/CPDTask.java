package net.sourceforge.pmd.cpd;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * CPDTask
 * 
 * Runs the CPD utility via ant. The ant task looks like this:
 *
 * <project name="CPDProj" default="main" basedir=".">
 *  <taskdef name="cpd" classname="net.sourceforge.pmd.cpd.CPDTask" />
 *	 <target name="main">
 * 		<cpd tileSize="100" codeLocation="c:\jdk14\src\java" outputFile="c:\cpdrun.txt"/>
 *	</target>
 *</project>
 *
 * tileSize, codeLocation, and outputFile are required fields right now.
 *
 * TODO: 1.Think about forking process? 
 *       2. Perhaps come up with another renderer such as XML and 
 *          allow user to plug that render in via ant...
 *   
 * @since Mar 26, 2003  
 * @author aglover
 */
public class CPDTask extends Task {

	private int tileSize;
	private String codeLocation;
	private String outputFile;

	public CPDTask() {
		super();
	}

	/**
	 * Method actually runs CPD.
	 * 
	 */
	public void execute() throws BuildException{
    	try{	
    		validateFields();
	    	CPD cpd = new CPD(tileSize);
		    cpd.setCpdListener(new CPDNullListener());
	        cpd.addRecursively(this.codeLocation);
	        cpd.go();
	        Writer wrtr = new BufferedWriter(new FileWriter(this.outputFile));
	        wrtr.write(cpd.getReport());
	        wrtr.close();
    	}catch(IOException ex){
    		ex.printStackTrace();
    		throw new BuildException("IOException in task", ex);
    	}        
	}

	/**
	 * quick validate of 2 string fields. 
	 * TODO: must figure out if we can accept 0 for a tile size? 
	 */
	private void validateFields() throws BuildException{
		if((this.codeLocation == null) || (this.outputFile == null) ){
			throw new BuildException("Required Attribute missing.");
		}			
	}

	/**
	 * Sets the codeLocation.
	 * @param codeLocation The codeLocation to set
	 */
	public void setCodeLocation(String codeLocation) {
		this.codeLocation = codeLocation;
	}

	/**
	 * Sets the tileSize.
	 * @param tileSize The tileSize to set
	 */
	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}

	/**
	 * Sets the outputFile.
	 * @param outputFile The outputFile to set
	 */
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}
}
