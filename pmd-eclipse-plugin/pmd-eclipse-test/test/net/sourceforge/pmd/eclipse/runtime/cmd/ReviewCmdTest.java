/*
 * Created on 12 avr. 2005
 * 
 * Copyright (c) 2005, PMD for Eclipse Development Team All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. * Redistributions
 * in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. * The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowledgement: "This product includes software developed in part by
 * support from the Defense Advanced Research Project Agency (DARPA)" *
 * Neither the name of "PMD for Eclipse Development Team" nor the names of
 * its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.sourceforge.pmd.eclipse.runtime.cmd;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import name.herlin.command.CommandException;
import name.herlin.command.UnsetInputPropertiesException;
import net.sourceforge.pmd.eclipse.EclipseUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This tests the PMD Processor command
 * 
 * @author Philippe Herlin
 * 
 */
public class ReviewCmdTest {
  private IProject testProject;

  /**
   * @see junit.framework.TestCase#setUp()
   */
  @Before
  public void setUp() throws Exception {

    // 1. Create a Java project
    this.testProject = EclipseUtils.createJavaProject("PMDTestProject");
    Assert.assertTrue("A test project cannot be created; the tests cannot be performed.",
        this.testProject != null && this.testProject.exists() && this.testProject.isAccessible());

    // 2. Create a test source file inside that project
    final IFile testFile = EclipseUtils.createTestSourceFile(this.testProject);
    final InputStream is = EclipseUtils.getResourceStream(this.testProject, "/Test.java");
    Assert.assertNotNull("Cannot find the test source file", is);
    is.close();

  }

  /**
   * @see junit.framework.TestCase#tearDown()
   */
  @After
  public void tearDown() throws Exception {
    try {
      if (this.testProject != null) {
        if (this.testProject.exists() && this.testProject.isAccessible()) {
          EclipseUtils.removePMDNature(this.testProject);
          //                this.testProject.refreshLocal(IResource.DEPTH_INFINITE, null);
          //                Thread.sleep(500);
          //                this.testProject.delete(true, true, null);
          //                this.testProject = null;
        }
      }
    }
    catch (final Exception e) {
      System.out.println("Exception " + e.getClass().getName() + " when tearing down. Ignored.");
    }
  }

  /**
   * Test the basic usage of the processor command
   * 
   */
  @Test
  public void testReviewCmdBasic() throws CommandException, CoreException {
    final ReviewCodeCmd cmd = new ReviewCodeCmd();
    cmd.addResource(this.testProject);
    cmd.performExecute();
    cmd.join();
    final Map<IFile, Set<MarkerInfo2>> markers = cmd.getMarkers();

    // We do not test PMD, only a non-empty report is enough
    Assert.assertNotNull(markers);
    Assert.assertTrue("Report size = " + markers.size(), markers.size() > 0);
  }

  /**
   * The ReviewCodeCmd must also work on a ResourceDelta
   * 
   * @throws CommandException
   */
  @Test
  public void testReviewCmdDelta() throws CommandException {
    // Don't know how to test that yet
    // How to instantiate a ResourceDelta ?
    // Let's comment for now
  }

  /**
   * Normally a null resource and a null resource delta is not acceptable.
   * 
   * @throws CommandException
   */
  @Test
  public void testReviewCmdNullResource() throws CommandException {
    try {
      final ReviewCodeCmd cmd = new ReviewCodeCmd();
      cmd.addResource(null);
      cmd.setResourceDelta(null);
      cmd.performExecute();
      Assert.fail("An Exception must be thrown");
    }
    catch (final UnsetInputPropertiesException e) {
      Assert.fail("An IllegalArgumentException must have been thrown before");
    }
    catch (final IllegalArgumentException e) {
      ; // cool, success
    }
  }
}