package net.sourceforge.pmd.cpd;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * CPDTask
 * 
 * Runs the CPD utility via ant. The ant task looks like this:
 *
 * <project name="CPDProj" default="main" basedir=".">
 *  <taskdef name="cpd" classname="net.sourceforge.pmd.cpd.CPDTask" />
 *	 <target name="main">
 * 		<cpd minimumTokenCount="100" outputFile="c:\cpdrun.txt" verbose=true>
 *          <fileset dir="/path/to/my/src">
 *              <include name="*.java"/>
 *          </fileset>
 *      </cpd>
 *	</target>
 *</project>
 *
 * Required: minimumTokenCount, outputFile, and at least one file
 * Optional: verbose
 *
 * TODO: 1.Think about forking process? 
 *       2. Perhaps come up with another renderer such as XML and 
 *          allow user to plug that render in via ant...
 *   
 * @since Mar 26, 2003  
 * @author aglover
 */
public class CPDTask extends Task {

    private boolean verbose;
	private int minimumTokenCount;
	private String outputFile;
    private List filesets = new ArrayList();

	public void execute() throws BuildException{
    	try{	
    		validateFields();
	    	CPD cpd = new CPD(minimumTokenCount);
            for (Iterator i = filesets.iterator(); i.hasNext();) {
                FileSet fs = (FileSet) i.next();
                DirectoryScanner ds = fs.getDirectoryScanner(project);
                String[] srcFiles = ds.getIncludedFiles();
                for (int j = 0; j < srcFiles.length; j++) {
                    File file = new File(ds.getBasedir() + System.getProperty("file.separator") + srcFiles[j]);
                    printIfVerbose("Tokenizing " + file.getAbsoluteFile().toString());
                    cpd.add(file);
                }
            }
            printIfVerbose("Starting to analyze code ");
            long start = System.currentTimeMillis();
	        cpd.go();
            long stop = System.currentTimeMillis();
            printIfVerbose("That took " + (stop-start) + " milliseconds");
            if (!cpd.getMatches().hasNext()) {
                printIfVerbose("No duplicates over " + minimumTokenCount + " tokens found");
                Writer writer = new BufferedWriter(new FileWriter(outputFile));
                writer.write("No problems found");
                writer.close();
            } else {
                printIfVerbose("Duplicates found; putting a report in " + outputFile);
                Writer writer = new BufferedWriter(new FileWriter(outputFile));
                writer.write(new SimpleRenderer().render(cpd.getMatches()));
                writer.close();
            }
    	}catch(IOException ex){
    		ex.printStackTrace();
    		throw new BuildException("IOException in task", ex);
    	}        
	}

	private void validateFields() throws BuildException{
		if(minimumTokenCount == 0){
			throw new BuildException("minimumTokenCount is required and must be greater than zero");
		} else if(outputFile == null) {
            throw new BuildException("outputFile is a required attribute");
        } else if (filesets.isEmpty()) {
            throw new BuildException("Must include at least one FileSet");
        }

	}

    public void addFileset(FileSet set) {
        filesets.add(set);
    }

	public void setMinimumTokenCount(int minimumTokenCount) {
		this.minimumTokenCount = minimumTokenCount;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    private void printIfVerbose(String in) {
        if (verbose)
            System.out.println(in);
    }
}
