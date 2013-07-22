/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.symboltable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Keeps track of the types encountered in a ASTinput
 */
public class TypeSet {

    /**
     * TODO should Resolver provide a canResolve() and a resolve()?
     * Requiring 2 calls seems clunky... but so does this
     * throwing an exception for flow control...
     */
    public interface Resolver {
	Class<?> resolve(String name) throws ClassNotFoundException;
    }

    public static class ExplicitImportResolver implements Resolver {
	private Set<String> importStmts;

	public ExplicitImportResolver(Set<String> importStmts) {
	    this.importStmts = importStmts;
	}

	public Class<?> resolve(String name) throws ClassNotFoundException {
	    for (String importStmt : importStmts) {
		if (importStmt.endsWith(name)) {
		    return Class.forName(importStmt);
		}
	    }
	    throw new ClassNotFoundException("Type " + name + " not found");
	}
    }

    public static class CurrentPackageResolver implements Resolver {
	private String pkg;

	public CurrentPackageResolver(String pkg) {
	    this.pkg = pkg;
	}

	public Class<?> resolve(String name) throws ClassNotFoundException {
	    return Class.forName(pkg + name);
	}
    }

    // TODO cite the JLS section on implicit imports
    public static class ImplicitImportResolver implements Resolver {
	public Class<?> resolve(String name) throws ClassNotFoundException {
	    return Class.forName("java.lang." + name);
	}
    }

    public static class ImportOnDemandResolver implements Resolver {
	private Set<String> importStmts;

	public ImportOnDemandResolver(Set<String> importStmts) {
	    this.importStmts = importStmts;
	}

	public Class<?> resolve(String name) throws ClassNotFoundException {
	    for (String importStmt : importStmts) {
		if (importStmt.endsWith("*")) {
		    try {
			String importPkg = importStmt.substring(0, importStmt.indexOf('*') - 1);
			return Class.forName(importPkg + '.' + name);
		    } catch (ClassNotFoundException cnfe) {
		    }
		}
	    }
	    throw new ClassNotFoundException("Type " + name + " not found");
	}
    }

    public static class PrimitiveTypeResolver implements Resolver {
	private Map<String, Class<?>> primitiveTypes = new HashMap<String, Class<?>>();

	@SuppressWarnings("PMD.AvoidUsingShortType")
	public PrimitiveTypeResolver() {
	    primitiveTypes.put("int", int.class);
	    primitiveTypes.put("float", float.class);
	    primitiveTypes.put("double", double.class);
	    primitiveTypes.put("long", long.class);
	    primitiveTypes.put("boolean", boolean.class);
	    primitiveTypes.put("byte", byte.class);
	    primitiveTypes.put("short", short.class);
	    primitiveTypes.put("char", char.class);
	}

	public Class<?> resolve(String name) throws ClassNotFoundException {
	    if (!primitiveTypes.containsKey(name)) {
		throw new ClassNotFoundException();
	    }
	    return primitiveTypes.get(name);
	}
    }

    public static class VoidResolver implements Resolver {
	public Class<?> resolve(String name) throws ClassNotFoundException {
	    if (name.equals("void")) {
		return void.class;
	    }
	    throw new ClassNotFoundException();
	}
    }

    public static class FullyQualifiedNameResolver implements Resolver {
	public Class<?> resolve(String name) throws ClassNotFoundException {
	    return Class.forName(name);
	}
    }

    private String pkg;
    private Set<String> imports = new HashSet<String>();
    private List<Resolver> resolvers = new ArrayList<Resolver>();

    public void setASTinputPackage(String pkg) {
	this.pkg = pkg;
    }

    public String getASTinputPackage() {
	return pkg;
    }

    public void addImport(String importString) {
	imports.add(importString);
    }

    public int getImportsCount() {
	return imports.size();
    }

    public Class<?> findClass(String name) throws ClassNotFoundException {
	// we don't build the resolvers until now since we first want to get all the imports
	if (resolvers.isEmpty()) {
	    buildResolvers();
	}

	for (Resolver resolver : resolvers) {
	    try {
		return resolver.resolve(name);
	    } catch (ClassNotFoundException cnfe) {
	    }
	}

	throw new ClassNotFoundException("Type " + name + " not found");
    }

    private void buildResolvers() {
	resolvers.add(new PrimitiveTypeResolver());
	resolvers.add(new VoidResolver());
	resolvers.add(new ExplicitImportResolver(imports));
	resolvers.add(new CurrentPackageResolver(pkg));
	resolvers.add(new ImplicitImportResolver());
	resolvers.add(new ImportOnDemandResolver(imports));
	resolvers.add(new FullyQualifiedNameResolver());
    }

}
