package net.sourceforge.pmd.runtime.cmd;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 * This is a ClassLoader for the Build Path of an IJavaProject.
 */
public class JavaProjectClassLoader extends URLClassLoader {
	private static final Logger log = Logger.getLogger(ReviewCodeCmd.class);

	private Set javaProjects = new HashSet();
	private IWorkspaceRoot workspaceRoot;

	public JavaProjectClassLoader(ClassLoader parent, IJavaProject javaProject) {
		super(new URL[0], parent);
		workspaceRoot = javaProject.getProject().getWorkspace().getRoot();
		addURLs(javaProject, false);
		
		// No longer need these things, drop references
		javaProjects = null;
		workspaceRoot = null;
	}

	private void addURLs(IJavaProject javaProject, boolean exportsOnly) {
		if (!javaProjects.contains(javaProject)) {
			javaProjects.add(javaProject);

			try {
				// Add default output location
				addURL(javaProject.getOutputLocation());

				// Add each classpath entry
				IClasspathEntry[] classpathEntries = javaProject.getResolvedClasspath(true);
				for (int i = 0; i < classpathEntries.length; i++) {
					IClasspathEntry classpathEntry = classpathEntries[i];
					if (classpathEntry.isExported() || !exportsOnly) {
						switch (classpathEntry.getEntryKind()) {

						// Recurse on projects
						case IClasspathEntry.CPE_PROJECT:
							IProject project = javaProject.getProject().getWorkspace().getRoot().getProject(
									classpathEntry.getPath().toString());
							IJavaProject javaProj = JavaCore.create(project);
							if (javaProj != null) {
								addURLs(javaProj, true);
							}
							break;

						// Library
						case IClasspathEntry.CPE_LIBRARY:
							addURL(classpathEntry);
							break;

						// Only Source entries with custom output location need to be added
						case IClasspathEntry.CPE_SOURCE:
							IPath outputLocation = classpathEntry.getOutputLocation();
							if (outputLocation != null) {
								addURL(outputLocation);
							}
							break;

						// Variable and Container entries should not be happening, because we've asked for resolved entries.
						case IClasspathEntry.CPE_VARIABLE:
						case IClasspathEntry.CPE_CONTAINER:
							break;
						}
					}
				}
			} catch (JavaModelException e) {
				log.debug("MalformedURLException occurred: " + e.getLocalizedMessage(), e);
			}
		}
	}

	private void addURL(IClasspathEntry classpathEntry) {
		addURL(classpathEntry.getPath());
	}

	private void addURL(IPath path) {
		try {
			if (workspaceRoot.exists(path)) {
				// path = workspaceRoot.getFileForLocation(path).getFullPath();
				//path = workspaceRoot.getFile(path).getFullPath();
				path = workspaceRoot.getLocation().append(path);
			}
			URL url = path.toFile().getAbsoluteFile().toURI().toURL();
			addURL(url);
		} catch (MalformedURLException e) {
			log.debug("MalformedURLException occurred: " + e.getLocalizedMessage(), e);
		}
	}
}
