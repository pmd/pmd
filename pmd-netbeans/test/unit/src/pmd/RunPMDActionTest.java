/*
 *  Copyright (c) 2002-2003, the pmd-netbeans team
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 *  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGE.
 */
package pmd;

import java.io.PrintStream;
import junit.framework.*;
import java.util.Collections;
import java.util.List;
import org.netbeans.junit.NbTestCase;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.XMLFileSystem;
import org.openide.loaders.DataObject;

/**
 * @author radim
 */
public class RunPMDActionTest extends NbTestCase {
    
    public RunPMDActionTest(String testName) {
        super(testName);
    }

    /**
     * Test of getHelpCtx method, of class pmd.RunPMDAction.
     */
    public void testGetHelpCtx() {
        pmd.RunPMDAction instance = (RunPMDAction)RunPMDAction.get(RunPMDAction.class);
        assertNotNull("There is no help context associated to RunPMDAction", instance.getHelpCtx());
    }

    /**
     * Test of checkCookies method, of class pmd.RunPMDAction.
     */
    public void testCheckCookies() throws Exception {
        clearWorkDir();
        
//        List dataobjects = null;
//        List expResult = null;
        List result;
        
        // try on empty list
        
        result = pmd.RunPMDAction.performScan(Collections.<DataObject>emptyList());
//        assertEquals(expResult, result);
        
        FileObject dir = FileUtil.toFileObject(getWorkDir());
        assertNotNull("Cannot find FileObject for work dir", dir);
        FileObject f1;
        f1 = dir.createData("MANIFEST.MF");
        assertNotNull("Cannot create file in work dir", f1);
        DataObject d1 = DataObject.find(f1);
        assertNotNull("Cannot find a data object", d1);
        result = pmd.RunPMDAction.performScan(Collections.singletonList(d1));
        assertEquals("There should be no error for MANIFEST.MF file", 0, result.size());
        
        f1 = dir.createData("PMDSample.java");
        assertNotNull("Cannot create file in work dir", f1);
        FileLock l = null;
        try {
            l = f1.lock();
            PrintStream ps = new PrintStream (f1.getOutputStream(l));
            ps.print("public class PMDSample { PMDSample () {} }");
            ps.close();
        }
        finally {
            if (l != null) {
                l.releaseLock();
            }
        }
        d1 = DataObject.find(f1);
        assertNotNull("Cannot find a data object", d1);
        result = pmd.RunPMDAction.performScan(Collections.singletonList(d1));
        assertEquals("There should be no error for PMDSample.java file", 0, result.size());
        
    }
    
    public void testShouldCheck() throws Exception {
        clearWorkDir();
        
        FileObject dir = FileUtil.toFileObject(getWorkDir());
        assertNotNull("Cannot find FileObject for work dir", dir);
        FileObject f1;
        f1 = dir.createData("MANIFEST.MF");
        assertNotNull("Cannot create file in work dir", f1);
        DataObject d1 = DataObject.find(f1);
        assertNotNull("Cannot find a data object", d1);
        assertFalse("MANIFEST.MF file should not be checked", RunPMDAction.shouldCheck(d1));
        
        f1 = dir.createData("PMDSample.java");
        assertNotNull("Cannot create file in work dir", f1);
        FileLock l = null;
        try {
            l = f1.lock();
            PrintStream ps = new PrintStream (f1.getOutputStream(l));
            ps.print("public class PMDSample { PMDSample () {} }");
            ps.close();
        }
        finally {
            if (l != null) {
                l.releaseLock();
            }
        }
        d1 = DataObject.find(f1);
        assertTrue("Java file should be checked", RunPMDAction.shouldCheck(d1));
        FileSystem fs = new XMLFileSystem(RunPMDActionTest.class.getResource("testfs.xml"));
        f1 = fs.findResource("pkg/Sample.java");
        assertFalse("expecting R/O file on XMLFileSystem", f1.canWrite());
        d1 = DataObject.find(f1);
        assertTrue("read only Java file "+d1+" should be checked too", RunPMDAction.shouldCheck(d1));
    }

    /**
     * Test of asynchronous method, of class pmd.RunPMDAction.
     */
    public void testAsynchronous() {
        pmd.RunPMDAction instance = (RunPMDAction)RunPMDAction.get(RunPMDAction.class);
        assertTrue("RunPMDAction should be asynchronous", instance.asynchronous());
    }
    
}
