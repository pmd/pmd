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
import java.util.logging.LogRecord;
import junit.framework.*;
import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.junit.NbTestCase;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.spi.java.queries.SourceLevelQueryImplementation;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;

/**
 * @author radim
 */
public class SourceLevelTest extends NbTestCase {
    
    /*
    static {
        System.setProperty("org.openide.util.Lookup", Lkp.class.getName());
    }
    
    public static final class Lkp extends org.openide.util.lookup.AbstractLookup {
        public Lkp() {
            this(new org.openide.util.lookup.InstanceContent());
        }
        
        private Lkp(org.openide.util.lookup.InstanceContent ic) {
            super(ic);
            ic.add(new TestSourceLevelQueryImpl());
        }
    }
     */
    
    public SourceLevelTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        // uncomment MockService registration and delete META-INF/services file when dropping 5.5 support
//        MockServices.setServices(TestSourceLevelQueryImpl.class);
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(SourceLevelTest.class);
        
        return suite;
    }

    /**
     * Test of checkCookies method, of class pmd.RunPMDAction.
     */
    public void testSourceLevels() throws Exception {
        clearWorkDir();
        CountingHandler counter = new CountingHandler();
        Logger.getLogger("pmd").addHandler(counter);
        Logger.getLogger("pmd").setLevel(Level.FINE);
        
        List result;
        
        FileObject dir = FileUtil.toFileObject(getWorkDir());
        ClassPath cp = ClassPathSupport.createClassPath(new FileObject[] { dir });
        TestClassPathProvider.testCP = cp;
        assertNotNull("Cannot find FileObject for work dir", dir);
        FileObject f1;
        DataObject d1;
        
        TestSourceLevelQueryImpl.level = "1.4";
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
        
        TestSourceLevelQueryImpl.level = "1.5";
        try {
            l = f1.lock();
            PrintStream ps = new PrintStream (f1.getOutputStream(l));
            ps.print("public class PMDSample { PMDSample () { " +
                    "   new java.util.ArrayList<Boolean>();" +
                    "  }" +
                    "}");
            ps.close();
        }
        finally {
            if (l != null) {
                l.releaseLock();
            }
        }
        result = pmd.RunPMDAction.performScan(Collections.singletonList(d1));
        assertEquals("There should be no error for PMDSample.java file", 0, result.size());
        assertEquals("No error logged during scanning", 0, counter.count);

        TestSourceLevelQueryImpl.level = "1.6";
        result = pmd.RunPMDAction.performScan(Collections.singletonList(d1));
        assertEquals("There should be no error for PMDSample.java file", 0, result.size());
        assertEquals("No error logged during scanning", 0, counter.count);
    }

    public static class TestSourceLevelQueryImpl implements SourceLevelQueryImplementation {
        
        static String level;
        
        public TestSourceLevelQueryImpl () {
            
        }
    
        public String getSourceLevel(FileObject arg0) {
//            System.out.println("TestSourceLevelQueryImpl "+arg0.toString()+", "+level);
            return level;
        }
    }
    
    public static class TestClassPathProvider implements ClassPathProvider {
        
        static ClassPath testCP;
        
        public TestClassPathProvider () {
        }
    
        public ClassPath findClassPath(FileObject file, String type) {
            if (testCP == null || !ClassPath.SOURCE.equals(type))
                return null;
            
            if (FileUtil.isParentOf(testCP.getRoots()[0], file)) {
                return testCP;
            }
            return null;
        }
}
    
    public static class CountingHandler extends Handler {

        int count;
    
        public void publish(LogRecord arg0) {
            System.out.println("Log "+arg0);
            count++;
        }

        public void flush() {
        }

        public void close() throws SecurityException {
        }
}
}
