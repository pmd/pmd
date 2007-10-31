/*
 * Created on 7 mai 2006
 *
 * Copyright (c) 2006, PMD for Eclipse Development Team
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

package net.sourceforge.pmd.runtime;

import java.io.IOException;

import net.sourceforge.pmd.runtime.preferences.IPreferences;
import net.sourceforge.pmd.runtime.preferences.IPreferencesFactory;
import net.sourceforge.pmd.runtime.preferences.IPreferencesManager;
import net.sourceforge.pmd.runtime.preferences.impl.PreferencesFactoryImpl;
import net.sourceforge.pmd.runtime.properties.IProjectProperties;
import net.sourceforge.pmd.runtime.properties.IProjectPropertiesManager;
import net.sourceforge.pmd.runtime.properties.IPropertiesFactory;
import net.sourceforge.pmd.runtime.properties.PropertiesException;
import net.sourceforge.pmd.runtime.properties.impl.PropertiesFactoryImpl;
import net.sourceforge.pmd.runtime.writer.IAstWriter;
import net.sourceforge.pmd.runtime.writer.IRuleSetWriter;
import net.sourceforge.pmd.runtime.writer.impl.WriterFactoryImpl;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Plugin class for the PMD for Eclipse Runtime plugin. This is the entry point to get access to that plugin features, such as
 * preferences and project proerties.
 * 
 * @author Herlin
 * @version $Revision$ $Log$
 * @version $Revision: $ Revision 1.1  2006/05/22 21:37:36  phherlin
 * @version $Revision: $ Refactor the plug-in architecture to better support future evolutions
 * @version $Revision: $
 */

public class PMDRuntimePlugin extends AbstractUIPlugin {
    public static final String PLUGIN_ID = "net.sourceforge.pmd.runtime";
    public static final String ROOT_LOG_ID = "net.sourceforge.pmd";
    private static final String PMD_ECLIPSE_APPENDER_NAME = "PMDEclipseAppender";
    private static PMDRuntimePlugin plugin;
    private IPreferencesFactory preferencesFactory = new PreferencesFactoryImpl();
    private IPropertiesFactory propertiesFactory = new PropertiesFactoryImpl();
    
    /**
     * The constructor.
     */
    public PMDRuntimePlugin() {
        plugin = this;
    }

    /**
     * This method is called upon plug-in activation
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        configureLogs();
    }

    /**
     * This method is called when the plug-in is stopped
     */
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        plugin = null;
    }

    /**
     * Returns the shared instance.
     */
    public static PMDRuntimePlugin getDefault() {
        return plugin;
    }

    /**
     * Load the PMD plugin preferences
     */
    public IPreferences loadPreferences() {
        return getPreferencesManager().loadPreferences();
    }
    
    /**
     * @return the plugin preferences manager
     */
    public IPreferencesManager getPreferencesManager() {
        return this.preferencesFactory.getPreferencesManager();
    }
    
    /**
     * @return the plugin project properties manager
     */
    public IProjectPropertiesManager getPropertiesManager() {
        return this.propertiesFactory.getProjectPropertiesManager();
    }
    
    /**
     * @param project a workspace project
     * @return the PMD properties for that project
     */
    public IProjectProperties loadProjectProperties(IProject project) throws PropertiesException {
        return getPropertiesManager().loadProjectProperties(project);
    }

    /**
     * Helper method to log error
     * 
     * @see IStatus
     */
    public void logError(String message, Throwable t) {
        getLog().log(new Status(IStatus.ERROR, getBundle().getSymbolicName(), 0, message + ": " + t.getMessage(), t));
    }

    /**
     * Helper method to log information
     * 
     * @see IStatus
     */
    public void logInformation(String message) {
        getLog().log(new Status(IStatus.INFO, getBundle().getSymbolicName(), 0, message, null));
    }
    
    /**
     * @return an instance of an AST writer
     */
    public IAstWriter getAstWriter() {
        return new WriterFactoryImpl().getAstWriter();
    }
    
    /**
     * @return an instance of a ruleset writer
     */
    public IRuleSetWriter getRuleSetWriter() {
        return new WriterFactoryImpl().getRuleSetWriter();
    }
    
    /**
     * Apply the log preferencs
     */
    public void applyLogPreferences(IPreferences preferences) {
        Logger log = Logger.getLogger(ROOT_LOG_ID);
        log.setLevel(preferences.getLogLevel());
        RollingFileAppender appender = (RollingFileAppender) log.getAppender(PMD_ECLIPSE_APPENDER_NAME);
        if (!appender.getFile().equals(preferences.getLogFileName())) {
            appender.setFile(preferences.getLogFileName());
            appender.activateOptions();
        }
    }

    /**
     * Configure the logging
     *
     */
    private void configureLogs() {
        try {
            IPreferences preferences = loadPreferences();

            Layout layout = new PatternLayout("%d{yyyy/MM/dd HH:mm:ss,SSS} %-5p %-32c{1} %m%n");
            
            RollingFileAppender appender = new RollingFileAppender(layout, preferences.getLogFileName());
            appender.setName(PMD_ECLIPSE_APPENDER_NAME);
            appender.setMaxBackupIndex(1);
            appender.setMaxFileSize("10MB");
            
            Logger.getRootLogger().addAppender(new ConsoleAppender(layout));
            Logger.getRootLogger().setLevel(Level.WARN);
            Logger.getRootLogger().setAdditivity(false);
            
            Logger.getLogger(ROOT_LOG_ID).addAppender(appender);            
            Logger.getLogger(ROOT_LOG_ID).setLevel(preferences.getLogLevel());
            Logger.getLogger(ROOT_LOG_ID).setAdditivity(false);

        } catch (IOException e) {
            logError("IO Exception when configuring logging.", e);
        }
    }

}
