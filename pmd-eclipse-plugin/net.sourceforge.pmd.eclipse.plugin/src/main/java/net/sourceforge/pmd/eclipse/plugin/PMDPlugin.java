package net.sourceforge.pmd.eclipse.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.eclipse.core.IRuleSetManager;
import net.sourceforge.pmd.eclipse.core.ext.RuleSetsExtensionProcessor;
import net.sourceforge.pmd.eclipse.core.impl.RuleSetManagerImpl;
import net.sourceforge.pmd.eclipse.runtime.cmd.JavaProjectClassLoader;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferences;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferencesFactory;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferencesManager;
import net.sourceforge.pmd.eclipse.runtime.preferences.impl.PreferencesFactoryImpl;
import net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties;
import net.sourceforge.pmd.eclipse.runtime.properties.IProjectPropertiesManager;
import net.sourceforge.pmd.eclipse.runtime.properties.IPropertiesFactory;
import net.sourceforge.pmd.eclipse.runtime.properties.PropertiesException;
import net.sourceforge.pmd.eclipse.runtime.properties.impl.PropertiesFactoryImpl;
import net.sourceforge.pmd.eclipse.runtime.writer.IAstWriter;
import net.sourceforge.pmd.eclipse.runtime.writer.IRuleSetWriter;
import net.sourceforge.pmd.eclipse.runtime.writer.impl.WriterFactoryImpl;
import net.sourceforge.pmd.eclipse.ui.RuleLabelDecorator;
import net.sourceforge.pmd.eclipse.ui.ShapePainter;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.eclipse.ui.nls.StringTable;
import net.sourceforge.pmd.eclipse.util.ResourceManager;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class PMDPlugin extends AbstractUIPlugin {

	private static File pluginFolder;

	private FileChangeReviewer changeReviewer;
	
	private Map<RGB, Color> coloursByRGB = new HashMap<RGB, Color>();

	public static final String PLUGIN_ID = "net.sourceforge.pmd.eclipse.plugin";

	private static Map<IProject, IJavaProject> JavaProjectsByIProject = new HashMap<IProject, IJavaProject>();
	
	// The shared instance
	private static PMDPlugin plugin;

	public static String VERSION = "unknown";

	private static final Integer[] priorityValues = new Integer[] {
        Integer.valueOf(1),
        Integer.valueOf(2),
        Integer.valueOf(3),
        Integer.valueOf(4),
        Integer.valueOf(5)
	    };
    
	/**
	 * The constructor
	 */
	public PMDPlugin() {
	}
	
	public Color colorFor(RGB rgb) {
		
		Color color = coloursByRGB.get(rgb);
		if (color != null) return color;
		
		color = new Color(null, rgb.red, rgb.green, rgb.blue);
		coloursByRGB.put( rgb, color );
		
		return color;
	}

	public static void setJavaClassLoader(PMDConfiguration config, IJavaProject javaProject) {

		IPreferences preferences = getDefault().loadPreferences();
		if (preferences.isProjectBuildPathEnabled()) {
			config.setClassLoader(new JavaProjectClassLoader(config.getClassLoader(), javaProject));
		}
	}
	
	/**
	 * Return the Java language version for the resources found within the specified
	 * project or null if it isn't a Java project or a Java version we don't support 
	 * yet.
	 * 
	 * @param project
	 * @return
	 */
	public static LanguageVersion javaVersionFor(IProject project) {

		IJavaProject jProject = JavaProjectsByIProject.get(project);
		if (jProject == null) {
			jProject = JavaCore.create(project);
			JavaProjectsByIProject.put(project, jProject);
		}
		
		if (jProject.exists()) {
			String compilerCompliance = jProject.getOption(JavaCore.COMPILER_COMPLIANCE, true);
			return Language.JAVA.getVersion(compilerCompliance);
		}

		return null;
	}
	
	private void disposeResources() {
		
		disposeAll(coloursByRGB.values());
	}
	
	public static void disposeAll(Collection<Color> colors) {
		for (Color color : colors) color.dispose();
	}
	
	public static File getPluginFolder() {

		if (pluginFolder == null) {
			URL url = Platform.getBundle(PLUGIN_ID).getEntry("/");
			try {
				url = FileLocator.resolve(url);
			}
			catch(IOException ex) {
				ex.printStackTrace();
			}
			pluginFolder = new File(url.getPath());
		}

		return pluginFolder;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		IPreferences prefs = loadPreferences();
        configureLogs(prefs);
        registerStandardRuleSets();
        registerAdditionalRuleSets();
        fileChangeListenerEnabled(prefs.isCheckAfterSaveEnabled());

        VERSION = context.getBundle().getHeaders().get("Bundle-Version");
	}
		
	public void fileChangeListenerEnabled(boolean flag) {
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		
		if (flag) {	
			if (changeReviewer == null) changeReviewer = new FileChangeReviewer();
			workspace.addResourceChangeListener(changeReviewer);
			} else {
				if (changeReviewer != null) {
					workspace.removeResourceChangeListener(changeReviewer);
					changeReviewer = null;
					}
			}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		
		fileChangeListenerEnabled(false);
		
		plugin = null;
		disposeResources();
		ShapePainter.disposeAll();
		ResourceManager.dispose();
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static PMDPlugin getDefault() {
		return plugin;
	}

    private static final Logger log = Logger.getLogger(PMDPlugin.class);

    private StringTable stringTable; // NOPMD by Herlin on 11/10/06 00:22
  
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("net.sourceforge.pmd.eclipse.plugin", path);
	}

    /**
     * Get an image corresponding to the severity
     */
    public Image getImage(String key, String iconPath) {
        ImageRegistry registry = getImageRegistry();
        Image image = registry.get(key);
        if (image == null) {
            ImageDescriptor descriptor = getImageDescriptor(iconPath);
            if (descriptor != null) {
                registry.put(key, descriptor);
                image = registry.get(key);
            }
        }

        return image;
    }

    /**
     * Helper method to log error
     *
     * @see IStatus
     */
    public void logError(String message, Throwable t) {
        getLog().log(new Status(IStatus.ERROR, getBundle().getSymbolicName(), 0, message + t.getMessage(), t));
        if (log != null) {
            log.error(message, t);
        }
    }

    /**
     * Helper method to log error
     *
     * @see IStatus
     */
    public void logError(IStatus status) {
        getLog().log(status);
        if (log != null) {
            log.error(status.getMessage(), status.getException());
        }
    }

    /**
     * Helper method to display error
     */
    public void showError(final String message, final Throwable t) {
        logError(message, t);
        Display.getDefault().syncExec(new Runnable() {

            public void run() {
            	String errTitle = getStringTable().getString(StringKeys.ERROR_TITLE);
                MessageDialog.openError(Display.getCurrent().getActiveShell(), errTitle, message + String.valueOf(t));
            }
        });
    }

    /**
     * Helper method to display a non-logged user error
     */
    public void showUserError(final String message) {

        Display.getDefault().syncExec(new Runnable() {

            public void run() {
            	String errTitle = getStringTable().getString(StringKeys.ERROR_TITLE);
                MessageDialog.openError(Display.getCurrent().getActiveShell(), errTitle, message);
            }
        });
    }
    
    /**
     * @return an instance of the string table
     */
    public StringTable getStringTable() {
        if (stringTable == null) {
            stringTable = new StringTable();
        }

        return stringTable;
    }

    /**
     * @return the priority values
     * @deprecated
     */
    public Integer[] getPriorityValues() {
        return priorityValues;
    }

    public static final String ROOT_LOG_ID = "net.sourceforge.pmd";
    private static final String PMD_ECLIPSE_APPENDER_NAME = "PMDEclipseAppender";
    private IPreferencesFactory preferencesFactory = new PreferencesFactoryImpl();
    private IPropertiesFactory propertiesFactory = new PropertiesFactoryImpl();

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
        return preferencesFactory.getPreferencesManager();
    }

    /**
     * @return the plugin project properties manager
     */
    public IProjectPropertiesManager getPropertiesManager() {
        return propertiesFactory.getProjectPropertiesManager();
    }

    /**
     * @param project a workspace project
     * @return the PMD properties for that project
     */
    public IProjectProperties loadProjectProperties(IProject project) throws PropertiesException {
        return getPropertiesManager().loadProjectProperties(project);
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
     * Apply the log preferences
     */
    public void applyLogPreferences(IPreferences preferences) {
        Logger log = Logger.getLogger(ROOT_LOG_ID);
        log.setLevel(preferences.getLogLevel());
        RollingFileAppender appender = (RollingFileAppender) log.getAppender(PMD_ECLIPSE_APPENDER_NAME);
        if (appender == null) {
            configureLogs(preferences);
        } else if (!appender.getFile().equals(preferences.getLogFileName())) {
            appender.setFile(preferences.getLogFileName());
            appender.activateOptions();
        }
    }

    /**
     * Configure the logging
     *
     */
    private void configureLogs(IPreferences preferences) {
        try {
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

    private final IRuleSetManager ruleSetManager = new RuleSetManagerImpl(); // NOPMD:SingularField

    /**
     * @return the ruleset manager instance
     */
    public final IRuleSetManager getRuleSetManager() {
        return ruleSetManager;
    }

    /**
     * Logs inside the Eclipse environment
     *
     * @param severity the severity of the log (IStatus code)
     * @param message the message to log
     * @param t a possible throwable, may be null
     */
    public final void log(final int severity, final String message, final Throwable t) {
        final Bundle bundle = getBundle();
        if (bundle != null) {
            getLog().log(new Status(severity, bundle.getSymbolicName(), 0, message, t));
        }

        // TODO : when bundle is not created yet (ie at startup), we cannot log ; find a way to log.
    }

    /**
     * Registering the standard rulesets
     *
     */
    private void registerStandardRuleSets() {

        final RuleSetFactory factory = new RuleSetFactory();
        try {
            Iterator<RuleSet> iterator = factory.getRegisteredRuleSets();
            final IRuleSetManager manager = getRuleSetManager();
            RuleSet ruleSet;
            while (iterator.hasNext()) {
            	ruleSet = iterator.next();
            	manager.registerRuleSet(ruleSet);
            	manager.registerDefaultRuleSet(ruleSet);
            	}
        } catch (RuleSetNotFoundException e) {
            log(IStatus.WARNING, "Problem getting all registered PMD RuleSets", e);
        }
    }

    /**
     * Register additional rulesets that may be provided by a fragment. Find
     * extension points implementation and call them
     *
     */
    private void registerAdditionalRuleSets() {
        try {
            final RuleSetsExtensionProcessor processor = new RuleSetsExtensionProcessor(getRuleSetManager());
            processor.process();
        } catch (CoreException e) {
            log(IStatus.ERROR, "Error when processing RuleSets extensions", e);
        }
    }
    
    public RuleLabelDecorator ruleLabelDecorator() {
    	IDecoratorManager mgr = getWorkbench().getDecoratorManager();
    															// TODO don't use a raw string...urgh
    	return (RuleLabelDecorator) mgr.getBaseLabelProvider("net.sourceforge.pmd.eclipse.plugin.RuleLabelDecorator");
    }
    
    public void changedFiles(Collection<IFile> changedFiles) {
    	
    	RuleLabelDecorator rld = ruleLabelDecorator();
    	if (rld == null) return;
    	
    	Collection<IResource> withParents = new HashSet<IResource>(changedFiles.size() * 2);
    	withParents.addAll(changedFiles);
    	for (IFile file : changedFiles) {
    		IResource parent = file.getParent();
    		while (parent != null) {
    			withParents.add(parent);
    			parent = parent.getParent();
    		}
    	}
    	
    	rld.changed( withParents );
    }
	
	private void addFilesTo(IResource resource, Collection<IResource> allKids) {
		
		if (resource instanceof IFile) {
			allKids.add(resource);
			return;
		}
		
		if (resource instanceof IFolder) {
			IFolder folder = (IFolder)resource;
			IResource[] kids = null;
			try {
				kids = folder.members();
			} catch (CoreException e) {
				e.printStackTrace();
			}
			addKids(allKids, kids);	
			
			allKids.add(folder);
			return;
		}
		
		if (resource instanceof IProject) {
			IProject project = (IProject)resource;
			IResource[] kids = null;
			try {
				kids = project.members();
			} catch (CoreException e) {
				e.printStackTrace();
			}
			addKids(allKids, kids);	
			allKids.add(project);
			return;
		}
	}

	private void addKids(Collection<IResource> allKids, IResource[] kids) {
		
		if (kids == null) return;
		
		for (IResource irc : kids) {
			if (irc instanceof IFile) {
				allKids.add(irc);
				continue;
			}
			if (irc instanceof IFolder) {
				addFilesTo(irc, allKids);
			}
		}
	}
	
	public void removedMarkersIn(IResource resource) {
		
		RuleLabelDecorator decorator = ruleLabelDecorator();
		if (decorator == null) return;
		
		Collection<IResource> changes = new ArrayList<IResource>();
		
		addFilesTo(resource, changes);
		
		decorator.changed(changes);
	}

}

