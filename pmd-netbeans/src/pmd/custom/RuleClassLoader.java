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
 * 
 * TODO: should be rewritten to have its resources rather than asks for all rulesets every time
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
