/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.modelica.ast.ASTClassDefinition;
import net.sourceforge.pmd.lang.modelica.ast.InternalModelicaNodeApi;
import net.sourceforge.pmd.lang.modelica.ast.ModelicaClassSpecifierNode;
import net.sourceforge.pmd.lang.modelica.ast.ModelicaImportClause;
import net.sourceforge.pmd.lang.modelica.ast.Visibility;

/**
 * Internal representation of a declared Modelica class, see {@link ModelicaClassType} for public API.
 */
class ModelicaClassDeclaration extends AbstractModelicaDeclaration implements ModelicaClassType {
    private ModelicaClassScope ownScope;
    private boolean encapsulated;
    private boolean partial;
    private ModelicaClassSpecialization specialization;
    private String simpleName;
    private final List<ModelicaImportClause> imports = new ArrayList<>();
    private final List<CompositeName> extendedClasses = new ArrayList<>();
    private List<ModelicaClassScope> resolvedExtends;

    ModelicaClassDeclaration(ASTClassDefinition node) {
        encapsulated = node.isEncapsulated();
        partial = node.isPartial();
        specialization = node.getSpecialization();
        ModelicaClassSpecifierNode classNode = node.getClassSpecifier();
        simpleName = classNode.getSimpleClassName();
        InternalModelicaNodeApi.populateExtendsAndImports(classNode, this);
    }

    /**
     * To be called by a corresponding AST node describing itself
     */
    void addImport(Visibility visibility, ModelicaImportClause clause) {
        // TODO handle visibility
        imports.add(clause);
    }

    /**
     * To be called by a corresponding AST node describing itself
     */
    void addExtends(Visibility visibility, CompositeName extendedClass) {
        // TODO handle visibility
        assert resolvedExtends == null;
        extendedClasses.add(extendedClass);
    }

    private List<ModelicaClassScope> getResolvedExtends(ResolutionState lazyInitState) {
        if (resolvedExtends == null) {
            ResolutionContext ctx = lazyInitState.createContext();
            try {
                for (CompositeName name : extendedClasses) {
                    ctx.watchdogTick();
                    ((AbstractModelicaScope) ownScope.getParent()).resolveLexically(ctx, name);
                }
            } catch (Watchdog.CountdownException e) {
                ctx.markTtlExceeded();
            }
            resolvedExtends = new ArrayList<>();
            for (ModelicaType decl: ctx.getTypes().getBestCandidates()) {
                if (decl instanceof ModelicaClassDeclaration) {
                    resolvedExtends.add(((ModelicaClassDeclaration) decl).getClassScope());
                }
            }
        }
        return resolvedExtends;
    }

    @Override
    public <T extends ResolvableEntity> ResolutionResult<T> safeResolveComponent(Class<T> clazz, ResolutionState state, CompositeName name) {
        ResolutionContext result = state.createContext();
        try {
            lookupInInstanceScope(result, name);
        } catch (Watchdog.CountdownException e) {
            result.markTtlExceeded();
        }
        return result.get(clazz);
    }

    /**
     * Looks up the first part of composite name in imported classes (either qualified or unqualified)
     *
     * @param state     resolution parameters
     * @param firstName a name to resolve
     * @param qualified whether we are looking at qualified imports or unqualified ones
     * @return List of candidate resolutions
     * @throws Watchdog.CountdownException if too many lookup steps were performed
     */
    private ResolutionResult<ModelicaDeclaration> lookupImported(ResolutionState state, String firstName, boolean qualified) throws Watchdog.CountdownException {
        state.tick();

        ResolutionContext result = state.createContext();
        for (final ModelicaImportClause importClause: imports) {
            ResolutionContext subResult = state.createContext();
            if (InternalModelicaNodeApi.isQualifiedImport(importClause) == qualified) {
                InternalModelicaNodeApi.resolveImportedSimpleName(importClause, subResult, firstName);
            }
            result.accumulate(subResult.getDeclaration());
        }
        return result.getDeclaration();
    }

    /**
     * Look up composite name inside this instance scope (and not above).
     * This method itself implements corresponding part of "5.3.1 Simple Name Lookup" of MLS 3.4.
     *
     * @param result an object to place results to
     * @param name   a name to look up
     * @throws Watchdog.CountdownException in too many lookup steps were performed
     */
    void lookupInInstanceScope(ResolutionContext result, CompositeName name) throws Watchdog.CountdownException {
        if (name.isEmpty()) {
            result.addCandidate(this);
            return;
        }

        String firstName = name.getHead();
        CompositeName furtherParts = name.getTail();

        result.watchdogTick();

        // Otherwise, lookup...
        // ... among declared names of the class
        for (ModelicaDeclaration decl: ownScope.getDirectlyDeclared(firstName)) {
            lookupInInstanceScopeFurtherParts(result, decl, furtherParts);
        }
        result.markHidingPoint();
        // ... and from inherited, too
        for (ModelicaClassScope extendedClass: getResolvedExtends(result.getState())) {
            for (ModelicaDeclaration inheritedDecl: extendedClass.getDirectlyDeclared(firstName)) {
                lookupInInstanceScopeFurtherParts(result, inheritedDecl, furtherParts);
            }
        }
        result.markHidingPoint();
        // ... using qualified imports
        ResolutionResult<ModelicaDeclaration> qualifiedImports = lookupImported(result.getState(), firstName, true);
        for (ModelicaDeclaration importedDecl: qualifiedImports.getBestCandidates()) {
            lookupInInstanceScopeFurtherParts(result, importedDecl, furtherParts);
        }
        result.markHidingPoint();
        for (ModelicaDeclaration importedDecl: qualifiedImports.getHiddenCandidates()) {
            lookupInInstanceScopeFurtherParts(result, importedDecl, furtherParts);
        }
        result.markHidingPoint();
        // ... then using unqualified imports
        ResolutionResult<ModelicaDeclaration> unqualifiedImports = lookupImported(result.getState(), firstName, false);
        for (ModelicaDeclaration importedDecl: unqualifiedImports.getBestCandidates()) {
            lookupInInstanceScopeFurtherParts(result, importedDecl, furtherParts);
        }
        result.markHidingPoint();
        for (ModelicaDeclaration importedDecl: unqualifiedImports.getHiddenCandidates()) {
            lookupInInstanceScopeFurtherParts(result, importedDecl, furtherParts);
        }
    }

    /**
     * Recurse into the first resolved element of composite name
     *
     * This method itself implements the "5.3.2 Composite Name Lookup" of MLS 3.4 with the first step
     * being made by `lookupInInstanceScope`.
     *
     * @param result             an object to place results to
     * @param resolvedSimpleName a declaration found when resolving the very first part of composite name
     * @param furtherParts       an unresolved "tail" of a composite name
     * @throws Watchdog.CountdownException if too many resolution steps were performed
     */
    private void lookupInInstanceScopeFurtherParts(ResolutionContext result, ModelicaDeclaration resolvedSimpleName, CompositeName furtherParts) throws Watchdog.CountdownException {
        result.watchdogTick();

        if (furtherParts.isEmpty()) {
            result.addCandidate(resolvedSimpleName);
            return;
        }

        if (resolvedSimpleName instanceof ModelicaComponentDeclaration) {
            ModelicaComponentDeclaration component = (ModelicaComponentDeclaration) resolvedSimpleName;
            if (result.getState().needRecurseInto(component)) {
                ResolutionResult<ModelicaType> componentTypes = component.getTypeCandidates();
                for (ModelicaType tpe : componentTypes.getBestCandidates()) {
                    if (tpe instanceof ModelicaClassDeclaration) {
                        ((ModelicaClassDeclaration) tpe).lookupInInstanceScope(result, furtherParts);
                    }
                }
                result.markHidingPoint();
                for (ModelicaType tpe : componentTypes.getHiddenCandidates()) {
                    if (tpe instanceof ModelicaClassDeclaration) {
                        ((ModelicaClassDeclaration) tpe).lookupInInstanceScope(result, furtherParts);
                    }
                }
            }
        } else if (resolvedSimpleName instanceof ModelicaClassDeclaration) {
            ModelicaClassDeclaration classDecl = (ModelicaClassDeclaration) resolvedSimpleName;
            classDecl.lookupInInstanceScope(result, furtherParts);
        } else {
            throw new IllegalArgumentException("Can recurse into class or component only");
        }
    }

    void setOwnScope(ModelicaClassScope scope) {
        ownScope = scope;
    }

    @Override
    public ModelicaClassSpecialization getSpecialization() {
        return specialization;
    }

    @Override
    public boolean isConnectorLike() {
        return specialization == ModelicaClassSpecialization.CONNECTOR || specialization == ModelicaClassSpecialization.EXPANDABLE_CONNECTOR;
    }

    @Override
    public boolean isEncapsulated() {
        return encapsulated;
    }

    @Override
    public boolean isPartial() {
        return partial;
    }

    @Override
    public ModelicaScope getContainingScope() {
        return ownScope.getParent();
    }

    @Override
    public ModelicaClassScope getClassScope() {
        return ownScope;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (encapsulated) {
            sb.append("encapsulated ");
        }
        if (partial) {
            sb.append("partial ");
        }
        sb.append(specialization.toString());
        sb.append(' ');
        sb.append(simpleName);
        return sb.toString();
    }

    @Override
    void resolveFurtherNameComponents(ResolutionContext result, CompositeName name) throws Watchdog.CountdownException {
        lookupInInstanceScope(result, name);
    }

    @Override
    public String getSimpleDeclarationName() {
        return simpleName;
    }

    @Override
    public String getSimpleTypeName() {
        return simpleName;
    }

    @Override
    public String getFullTypeName() {
        return ownScope.getFullyQualifiedClassName();
    }

    @Override
    public String getDescriptiveName() {
        return getFullTypeName();
    }
}
