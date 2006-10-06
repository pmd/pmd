/*
 *  Copyright (c) 2002-2006, the pmd-netbeans team
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.SourceType;

import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.queries.SourceLevelQuery;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.LineCookie;
import org.openide.cookies.SourceCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Cancellable;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.OutputWriter;

import pmd.config.ConfigUtils;
import pmd.scan.EditorChangeListener;

/**
 * Action that runs PMD on the currently selected Java file or set of Java files.
 * This is called both by NetBeans when the action is manually invoked by the user
 * ({@link #performAction}),
 * and by the real-time scanner when a Java file needs to be scanned
 * ({@link #checkCookies}).
 *
 * Important side effect of this class is that it initializes
 * EditorChangeListener so this has to be loaded during startup to 
 * enable real-time scanning.
 */
public class RunPMDAction extends CookieAction {
    
    /**
     * Overridden to log that the action is being initialized, and to register an editor change listener for
     * scanning.
     */
    protected void initialize() {
        super.initialize();
        EditorChangeListener.initialize();
        putValue("noIconInMenu", Boolean.TRUE);
    }
    /**
     * Gets the name of this action
     *
     * @return the name of this action
     */
    public String getName() {
        return NbBundle.getMessage( RunPMDAction.class, "LBL_Action" );
    }
    
    
    /**
     * Gets the filename of the icon associated with this action
     *
     * @return the name of the icon
     */
    protected String iconResource() {
        return "pmd/resources/PMDOptionsSettingsIcon.gif";
    }
    
    
    /**
     * Returns default help
     *
     * @return HelpCtx.DEFAULT_HELP
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    
    /**
     * Returns the cookies that can use this action
     *
     * @return an array of the two elements DataFolder.class and SourceCookie.class
     */
    protected Class[] cookieClasses() {
        return new Class[]{DataFolder.class, SourceCookie.class};
    }
    
    
    /**
     * Returns the mode of this action
     *
     * @return the mode of this action
     * @see org.openide.util.actions.CookieAction#MODE_ALL
     */
    protected int mode() {
        return MODE_ALL;
    }
    
    
    /**
     * Runs PMD on the given list of DataObjects, with no callback.
     * This just calls {@link #checkCookies(List, RunPMDCallback)} with a default callback that displays
     * progress in the status bar.
     *
     * @param dataobjects the list of data objects to run PMD on. Elements are instanceof
     *                    {@link DataObject}.
     * @return the list of rule violations found in the run, not null. Elements are instanceof {@link Fault}.
     * @throws IOException on failure to read one of the files or to write to the output window.
     */
    public static List/*<Fault>*/ performScan( List/*<DataObject>*/ dataobjects ) throws IOException {
        assert dataobjects != null: "Cannot pass null to RunPMDAction.checkCookies()";
        SourceLevelQuery sourceLevelQuery =
                (SourceLevelQuery) Lookup.getDefault().lookup(SourceLevelQuery.class);
        RuleSet set = constructRuleSets();
        ArrayList/*<Fault>*/ list = new ArrayList( 100 );
        
        CancelCallback cancel = new CancelCallback();
        ProgressHandle prgHdl = ProgressHandleFactory.createHandle("PMD check", cancel); // PENDING action to show output
        prgHdl.start(dataobjects.size());
        try {
            for( int i = 0; i < dataobjects.size(); i++ ) {
                if (cancel.isCancelled())
                    break;
                DataObject dataobject = ( DataObject )dataobjects.get( i );
                prgHdl.progress(dataobject.getName(), i); // TODO: I18N 'name', x of y
                FileObject fobj = dataobject.getPrimaryFile();
                ClassPath cp = ClassPath.getClassPath( fobj, ClassPath.SOURCE );
                if (cp == null) {
                    // not on any classpath, ignore
                    continue;
                }
                String name = cp.getResourceName( fobj, '.', false );
                
                //The file is not a java file
                if (!shouldCheck(dataobject)) {
                    continue;
                }
                
                String sourceLevel = sourceLevelQuery.getSourceLevel(fobj);
                
                // choose the correct PMD to use according to the source level
                PMD pmd = new PMD();
                if ("1.5".equals(sourceLevel)) {
                    pmd.setJavaVersion(SourceType.JAVA_15);
                } else if ("1.3".equals(sourceLevel)) {
                    pmd.setJavaVersion(SourceType.JAVA_13);
                } else {
                    // default to JDK 1.4 if we don't know any better...
                    pmd.setJavaVersion(SourceType.JAVA_14);
                }
                
                Reader reader;
                try {
                    reader = getSourceReader( dataobject );
                } catch( IOException ioe) {
                    Fault fault = new Fault( 1, name, "IOException reading file for class " + name + ": " + ioe.toString());
                    ErrorManager.getDefault().notify( ioe );
                    list.add( fault );
                    FaultRegistry.getInstance().registerFault( fault, dataobject );
                    continue;
                }
                
                RuleContext ctx = new RuleContext();
                Report report = new Report();
                ctx.setReport( report );
                ctx.setSourceCodeFilename( name );
                try {
                    pmd.processFile( reader, set, ctx );
                } catch( PMDException e ) {
                    // want to log only short info about failure and stack only when -J-Dpmd=-1 or similar flag is on
                    ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL+1, "PMD threw exception " + e.toString());
                    ErrorManager err = ErrorManager.getDefault().getInstance("pmd");
                    if (err.isLoggable(err.INFORMATIONAL)) {
                        err.notify(ErrorManager.INFORMATIONAL, e); // NOI18N
                    }
                }
                
                
                
                Iterator/*<RuleViolation>*/ iterator = ctx.getReport().iterator();
                while( iterator.hasNext() ) {
                    RuleViolation violation = ( RuleViolation )iterator.next();
                    
                    
                    StringBuffer buffer = new StringBuffer();
                    buffer.append( violation.getRule().getName() ).append( ", " );
                    buffer.append( violation.getDescription() );
                    Fault fault = new Fault( violation.getBeginLine(),
                            violation.getFilename(),
                            buffer.toString() );
                    list.add( fault );
                    FaultRegistry.getInstance().registerFault( fault, dataobject );
                }
            }
        } finally {
            prgHdl.finish();
        }
        Collections.sort( list );
        return list;
    }
    
    // package private for testing purposes
    static boolean shouldCheck(DataObject dobj) {
        if (!dobj.getPrimaryFile().hasExt( "java" )
        || dobj.getCookie( LineCookie.class ) == null) {
            return false;
        }
        return true;
    }
    
    
    /**
     * Performs the action this action is set up to do on the specified nodes
     *
     * @param node the nodes that the action is involved on
     */
    protected void performAction( Node[] node ) {
        FaultRegistry.getInstance().clearRegistry();
        OutputWriter out = null;
        try {
            StatusDisplayer.getDefault().setStatusText("PMD checking for rule violations");
            List list = getDataObjects(node);
            final List violations = performScan(list);
            
            if(violations.isEmpty()) {
                StatusDisplayer.getDefault().setStatusText("PMD found no rule violations");  
            } 
            else {
                StatusDisplayer.getDefault().setStatusText("PMD found rule violations");
                final OutputWindow wnd = OutputWindow.getInstance();
                wnd.setViolations((Fault[]) violations.toArray(new Fault[violations.size()]));
                
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        wnd.setDisplayName("PMD Output: found " + violations.size() + " violations");
                        wnd.open();
                        wnd.requestActive();
                    }
                });
            }
        } catch(IOException e) {
            ErrorManager.getDefault().notify(e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    
    /**
     * Constructs the ruleset.
     *
     * @return the constructed ruleset
     * @see pmd.config.PMDOptionsSettings#getRulesets()
     */
    private static RuleSet constructRuleSets() {
        RuleSet rules = new RuleSet();
        List list = ConfigUtils.getRuleList();
        Iterator iterator = list.iterator();
        while( iterator.hasNext() ) {
            rules.addRule( ( Rule )iterator.next() );
        }
        return rules;
    }
    
    
    /**
     * Get the reader for the specified dataobject
     *
     * @param dataobject the dataobject to read
     * @return a reader for the dataobject
     * @exception IOException if the object can't be read
     */
    private static Reader getSourceReader( DataObject dataobject ) throws IOException {
        Reader reader = null;
        EditorCookie editor = ( EditorCookie )dataobject.getCookie( EditorCookie.class );
        if (editor != null) {
            StyledDocument doc = editor.getDocument();
            if (doc != null) {
                try {
                    reader = new StringReader(doc.getText(0, doc.getLength()));
                } catch (BadLocationException ex) {
                    // OK, fallback to read from FO
                }
            }
        }
        if (reader == null) {
            Iterator iterator = dataobject.files().iterator();
            FileObject file = ( FileObject )iterator.next();
            reader = new BufferedReader( new InputStreamReader( file.getInputStream() ) );
        }
        return reader;
    }
    
    
    /**
     * Gets the data objects associated with the given nodes.
     *
     * @param node the nodes to get data objects for
     * @return a list of the data objects. Each element is instanceof DataObject.
     */
    private List getDataObjects( Node[] node ) {
        ArrayList list = new ArrayList();
        for( int i = 0; i < node.length; i++ ) {
            DataObject data = (DataObject)node[i].getCookie( DataObject.class );
            
            //Checks to see if it's a java source file
            if( data.getPrimaryFile().hasExt( "java" ) ) {
                list.add( data );
            }
            //Or if it's a folder
            else {
                DataFolder folder = ( DataFolder )node[i].getCookie( DataFolder.class );
                Enumeration enumeration = folder.children( true );
                while( enumeration.hasMoreElements() ) {
                    DataObject dataobject = ( DataObject )enumeration.nextElement();
                    if( dataobject.getPrimaryFile().hasExt( "java" ) ) {
                        list.add( dataobject );
                    }
                }
            }
        }
        return list;
    }
    
    protected boolean asynchronous() {
        // PENDING need to rewriet to synchronous action
        return true;
    }
    
    private static class CancelCallback implements Cancellable {
        private boolean cancelled = false;
        
        public CancelCallback() {}
        
        public boolean cancel() {
            cancelled = true;
            return true;
        }
        
        public boolean isCancelled() {
            return cancelled;
        }
    }
    
}
