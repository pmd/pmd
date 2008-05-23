package net.sourceforge.pmd.lang.java.rule.coupling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;

/**
 * The loose package coupling Rule can be used to ensure coupling outside of
 * a package hierarchy is minimized to all but an allowed set of classes from
 * within the package hierarchy.
 * <p>
 * For example, supposed you have the following package hierarchy:
 * <ul>
 * 	<li><code>org.sample</code></li>
 * 	<li><code>org.sample.impl</code></li>
 * 	<li><code>org.sample.util</code></li>
 * </ul>
 * And the allowed class <code>org.sample.SampleInterface</code>.
 * <p>
 * This rule can be used to ensure that all classes within the
 * <code>org.sample</em> package and its sub-packages are not used outside of
 * the <code>org.sample</code> package hierarchy.  Further, the only allowed
 * usage outside of a class in the <code>org.sample</code> hierarchy would be
 * via <code>org.sample.SampleInterface</code>.
 */
public class LoosePackageCouplingRule extends AbstractJavaRule {

    private static final PropertyDescriptor PACKAGES_DESCRIPTOR = new StringProperty("packages", "Restricted Packages",
	    new String[] {}, 1.0f, ',');

    private static final PropertyDescriptor CLASSES_DESCRIPTOR = new StringProperty("classes", "Allowed Classes",
	    new String[] {}, 2.0f, ',');

    // The package of this source file
    private String thisPackage;

    // The restricted packages
    private List<String> restrictedPackages;

    public LoosePackageCouplingRule() {
	addRuleChainVisit(ASTCompilationUnit.class);
	addRuleChainVisit(ASTPackageDeclaration.class);
	addRuleChainVisit(ASTImportDeclaration.class);
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
	this.thisPackage = "";

	// Sort the restricted packages in reverse order.  This will ensure the
	// child packages are in the list before their parent packages.
	this.restrictedPackages = new ArrayList<String>(Arrays.asList(super.getStringProperties(PACKAGES_DESCRIPTOR)));
	Collections.sort(restrictedPackages, Collections.reverseOrder());

	return data;
    }

    @Override
    public Object visit(ASTPackageDeclaration node, Object data) {
	this.thisPackage = node.getPackageNameImage();
	return data;
    }

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {

	String importPackage = node.getPackageName();

	// Check each restricted package
	for (String pkg : getRestrictedPackages()) {
	    // Is this import restricted?  Use the deepest sub-package which restricts this import.
	    if (isContainingPackage(pkg, importPackage)) {
		// Is this source in a sub-package of restricted package?
		if (pkg.equals(thisPackage) || isContainingPackage(pkg, thisPackage)) {
		    // Valid usage
		    break;
		} else {
		    // On demand imports automatically fail because they include everything
		    if (node.isImportOnDemand()) {
			addViolation(data, node, new Object[] { node.getImportedName(), pkg });
			break;
		    } else {
			if (!isAllowedClass(node)) {
			    addViolation(data, node, new Object[] { node.getImportedName(), pkg });
			    break;
			}
		    }
		}
	    }
	}
	return data;
    }

    protected List<String> getRestrictedPackages() {
	return restrictedPackages;
    }

    // Is 1st package a containing package of the 2nd package?
    protected boolean isContainingPackage(String pkg1, String pkg2) {
	return pkg1.equals(pkg2)
		|| (pkg1.length() < pkg2.length() && pkg2.startsWith(pkg1) && pkg2.charAt(pkg1.length()) == '.');
    }

    protected boolean isAllowedClass(ASTImportDeclaration node) {
	String importedName = node.getImportedName();
	for (String clazz : getStringProperties(CLASSES_DESCRIPTOR)) {
	    if (importedName.equals(clazz)) {
		return true;
	    }

	}
	return false;
    }
}
