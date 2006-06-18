/*
 * Created on 14 avr. 2005
 *
 * Copyright (c) 2005, PMD for Eclipse Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The end-user documentation included with the redistribution, if
 *       any, must include the following acknowledgement:
 *       "This product includes software developed in part by support from
 *        the Defense Advanced Research Project Agency (DARPA)"
 *     * Neither the name of "PMD for Eclipse Development Team" nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.sourceforge.pmd.runtime.cmd;

import java.io.InputStream;

import junit.framework.TestCase;
import name.herlin.command.CommandException;
import name.herlin.command.UnsetInputPropertiesException;
import net.sourceforge.pmd.eclipse.EclipseUtils;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.runtime.cmd.RenderReportCmd;
import net.sourceforge.pmd.runtime.cmd.ReviewCodeCmd;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;


/**
 * Test the report rendering
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2006/06/18 22:29:51  phherlin
 * Begin refactoring the unit tests for the plugin
 * Revision 1.2 2005/12/30 16:29:15 phherlin
 * Implement a new preferences model and review some tests
 * 
 * Revision 1.1 2005/06/15 21:14:57 phherlin Create the project for the Eclipse
 * plugin unit tests
 * 
 * 
 */
public class RenderReportCmdTest extends TestCase {
    private IProject testProject;

    /**
     * Default constructor
     * 
     * @param name
     */
    public RenderReportCmdTest(String name) {
        super(name);
    }

    /**
     * Test the basic usage of the report rendering command
     * 
     */
    public void testRenderReportCmdBasic() throws CommandException, CoreException {
        ReviewCodeCmd reviewCmd = new ReviewCodeCmd();
        reviewCmd.addResource(this.testProject);
        reviewCmd.performExecute();

        RenderReportCmd cmd = new RenderReportCmd();
        cmd.setProject(this.testProject);
        cmd.setRenderer(new HTMLRenderer());
        cmd.setReportName(PMDRuntimeConstants.HTML_REPORT_NAME);
        cmd.performExecute();
        cmd.join();

        IFolder reportFolder = this.testProject.getFolder(PMDRuntimeConstants.REPORT_FOLDER);
        assertTrue(reportFolder.exists());

        IFile reportFile = reportFolder.getFile(PMDRuntimeConstants.HTML_REPORT_NAME);
        assertTrue(reportFile.exists());

        this.testProject.deleteMarkers(PMDRuntimeConstants.PMD_MARKER, true, IResource.DEPTH_INFINITE);

        if (reportFile.exists()) {
            reportFile.delete(true, false, null);
        }

        if (reportFolder.exists()) {
            reportFolder.delete(true, false, null);
        }
    }

    /**
     * Test robustness #1
     * 
     * @throws CommandException
     */
    public void testRenderReportCmdNullArg1() throws CommandException {
        try {
            RenderReportCmd cmd = new RenderReportCmd();
            cmd.setProject(null);
            cmd.setRenderer(new HTMLRenderer());
            cmd.setReportName(PMDRuntimeConstants.HTML_REPORT_NAME);
            cmd.performExecute();
            fail();
        } catch (UnsetInputPropertiesException e) {
            // yes cool
        }
    }

    /**
     * Test robustness #2
     * 
     * @throws CommandException
     */
    public void testRenderReportCmdNullArg2() throws CommandException {
        try {
            RenderReportCmd cmd = new RenderReportCmd();
            cmd.setProject(this.testProject);
            cmd.setRenderer(null);
            cmd.setReportName(PMDRuntimeConstants.HTML_REPORT_NAME);
            cmd.performExecute();
            fail();
        } catch (UnsetInputPropertiesException e) {
            // yes cool
        }
    }

    /**
     * Test robustness #3
     * 
     * @throws CommandException
     */
    public void testRenderReportCmdNullArg3() throws CommandException {
        try {
            RenderReportCmd cmd = new RenderReportCmd();
            cmd.setProject(this.testProject);
            cmd.setRenderer(new HTMLRenderer());
            cmd.setReportName(null);
            cmd.performExecute();
            fail();
        } catch (UnsetInputPropertiesException e) {
            // yes cool
        }
    }

    /**
     * Test robustness #4
     * 
     * @throws CommandException
     */
    public void testRenderReportCmdNullArg4() throws CommandException {
        try {
            RenderReportCmd cmd = new RenderReportCmd();
            cmd.setProject(null);
            cmd.setRenderer(null);
            cmd.setReportName(PMDRuntimeConstants.HTML_REPORT_NAME);
            cmd.performExecute();
            fail();
        } catch (UnsetInputPropertiesException e) {
            // yes cool
        }
    }

    /**
     * Test robustness #5
     * 
     * @throws CommandException
     */
    public void testRenderReportCmdNullArg5() throws CommandException {
        try {
            RenderReportCmd cmd = new RenderReportCmd();
            cmd.setProject(null);
            cmd.setRenderer(new HTMLRenderer());
            cmd.setReportName(null);
            cmd.performExecute();
            fail();
        } catch (UnsetInputPropertiesException e) {
            // yes cool
        }
    }

    /**
     * Test robustness #6
     * 
     * @throws CommandException
     */
    public void testRenderReportCmdNullArg6() throws CommandException {
        try {
            RenderReportCmd cmd = new RenderReportCmd();
            cmd.setProject(this.testProject);
            cmd.setRenderer(null);
            cmd.setReportName(null);
            cmd.performExecute();
            fail();
        } catch (UnsetInputPropertiesException e) {
            // yes cool
        }
    }

    /**
     * Test robustness #7
     * 
     * @throws CommandException
     */
    public void testRenderReportCmdNullArg7() throws CommandException {
        try {
            RenderReportCmd cmd = new RenderReportCmd();
            cmd.setProject(null);
            cmd.setRenderer(null);
            cmd.setReportName(null);
            cmd.performExecute();
            fail();
        } catch (UnsetInputPropertiesException e) {
            // yes cool
        }
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        // 1. Create a Java project
        this.testProject = EclipseUtils.createJavaProject("PMDTestProject");
        assertTrue("A test project cannot be created; the tests cannot be performed.", (this.testProject != null)
                && this.testProject.exists() && this.testProject.isAccessible());

        // 2. Create a test source file inside that project
        EclipseUtils.createTestSourceFile(this.testProject);
        InputStream is = EclipseUtils.getResourceStream(this.testProject, "/Test.java");
        assertNotNull("Cannot find the test source file", is);
        is.close();

    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        if (this.testProject != null) {
            if (this.testProject.exists() && this.testProject.isAccessible()) {
                EclipseUtils.removePMDNature(this.testProject);
                // this.testProject.refreshLocal(IResource.DEPTH_INFINITE,
                // null);
                // Thread.sleep(500);
                // this.testProject.delete(true, true, null);
                // this.testProject = null;
            }
        }

        super.tearDown();
    }
}
