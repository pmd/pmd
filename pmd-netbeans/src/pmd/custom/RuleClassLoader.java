/*
 *  RuleClassLoader.java
 *
 *  Created on 21. februar 2003, 00:31
 */
package pmd.custom;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.openide.ErrorManager;
import pmd.config.CustomRuleSetSettings;
import pmd.config.PMDOptionsSettings;

/**
 * Classloader implementation for PMD custom rulesets.
 */
public class RuleClassLoader extends ClassLoader {

	/**
	 * Creates a new classloader for PMD rules.
	 *
	 * @param parent the parent classloader to delegate to, not null.
	 */
	public RuleClassLoader(ClassLoader parent) {
		super(parent);
	}


	/**
	 * This implementation searches for a class with the given name in the classpath specified in the
	 * custom rulesets configuration.
	 *
	 * @param name the fully-qualified classname of the class to find, not null.
	 * @return the class, or null if not found.
	 */
	protected Class findClass(String name) {
		ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "Loading class " + name);

		String className = name.replace('.', '/');
		className += ".class";

		CustomRuleSetSettings settings = PMDOptionsSettings.getDefault().getRulesets();
		Iterator iterator = settings.getClassPath().iterator();
		try {
			while(iterator.hasNext()) {
				File jar = new File((String)iterator.next());
				JarFile jarFile = new JarFile(jar, false);
				JarEntry entry = jarFile.getJarEntry(className);
				ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, entry.toString());
				if(entry != null) {
					BufferedInputStream stream = new BufferedInputStream(jarFile.getInputStream(entry));
					byte buffer[] = new byte[stream.available()];
					stream.read(buffer, 0, buffer.length);
					return defineClass(name, buffer, 0, buffer.length);
				}
			}
		} catch(IOException e) {
			return null;
		}
		return null;
	}

}
