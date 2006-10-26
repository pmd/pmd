/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.typeresolution;

import net.sourceforge.pmd.typeresolution.visitors.PMDASMVisitor;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PMDASMClassLoader extends ClassLoader {

	public PMDASMClassLoader() {
	}

	public synchronized Class loadClass(String name) throws ClassNotFoundException {
		return defineClass(name);
	}

	private Map importedClasses = new HashMap();

	private Set dontBother = new HashSet();

	public Map getImportedClasses(String className) {
		Map ret = (Map) importedClasses.get(className);
		return ret == null ? new HashMap() : ret;
	}

	private Class defineClass(String name) throws ClassNotFoundException {

		if (dontBother.contains(name)) {
			throw new ClassNotFoundException(name);
		}
		try {
			if (name.startsWith("java.")) {
				return Class.forName(name);
			}
			if (importedClasses.containsKey(name)) {
				if (super.findLoadedClass(name) != null) {
					return super.findLoadedClass(name);
				}
			}
			ClassReader reader = new ClassReader(getResourceAsStream(name.replace('.', '/') + ".class"));
			PMDASMVisitor asmVisitor = new PMDASMVisitor();
			reader.accept(asmVisitor, 0);

			List inner = asmVisitor.getInnerClasses();
			if (inner != null && !inner.isEmpty()) {
				for (int ix = 0; ix < inner.size(); ix++) {
					String str = (String) inner.get(ix);
					ClassReader innerReader = new ClassReader(getResourceAsStream(str.replace('.', '/') + ".class"));
					innerReader.accept(asmVisitor, 0);
				}
			}
			importedClasses.put(name, asmVisitor.getPackages());
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			reader.accept(writer, 0);

			byte[] byteCode = writer.toByteArray();
			return defineClass(name, byteCode, 0, byteCode.length);
		} catch (ClassNotFoundException e) {
			dontBother.add(name);
			throw e;
		} catch (IOException e) {
			dontBother.add(name);
			throw new ClassNotFoundException(name, e);
		}
	}
}