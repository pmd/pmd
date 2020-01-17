/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
// Note: taken from https://github.com/forcedotcom/idecore/blob/3083815933c2d015d03417986f57bd25786d58ce/com.salesforce.ide.apex.core/src/apex/jorje/semantic/common/TestAccessEvaluator.java

/*
 * Copyright 2016 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import apex.jorje.semantic.compiler.Namespace;
import apex.jorje.semantic.compiler.StructuredVersion;
import apex.jorje.semantic.compiler.sfdc.AccessEvaluator;
import apex.jorje.semantic.compiler.sfdc.PlaceholderOrgPerm;
import apex.jorje.semantic.symbol.type.SObjectTypeInfo;
import apex.jorje.semantic.symbol.type.StandardTypeInfo;
import apex.jorje.semantic.symbol.type.StandardTypeInfoImpl;
import apex.jorje.semantic.symbol.type.TypeInfo;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.SetMultimap;

/**
 * For now everything returns false.
 * If you actually need to override something, it would be easier to probably mock and adjust what you needed.
 * Otherwise this is simply to create a concrete representation and not force a mockito init.
 *
 * @author jspagnola
 */
public class TestAccessEvaluator implements AccessEvaluator {

    private final SetMultimap<Namespace, StructuredVersion> validPageVersions;
    private final SetMultimap<SObjectTypeInfo, TypeInfo> visibleSetupEntitiesToTypes;
    private final Set<Namespace> accessibleSystemNamespaces;
    private final Set<PlaceholderOrgPerm> orgPerm;
    private final Set<AllowedPermGuard> allowedPermGuards;
    private final Set<Namespace> reservedNamespaces;
    private final Set<String> globalComponents;
    private final Set<Namespace> managedPackagesNotInstalled;
    private final Set<String> typesWithConnectApiDeserializers;
    private boolean hasInternalSfdc;
    private boolean isRunningTests;
    private boolean hasPrivateApi;
    private boolean isTrustedApplication;
    private boolean hasLocalizedTranslation;
    private boolean isSfdc;
    private boolean isReallyRunningTests;
    private boolean hasApexGenericTypes;
    private boolean hasRemoteActionPerm;
    private boolean hasPersonAccountApiAvailable;

    public TestAccessEvaluator() {
        validPageVersions = HashMultimap.create();
        visibleSetupEntitiesToTypes = HashMultimap.create();
        managedPackagesNotInstalled = new HashSet<>();
        accessibleSystemNamespaces = new HashSet<>();
        orgPerm = new HashSet<>();
        allowedPermGuards = new HashSet<>();
        reservedNamespaces = new HashSet<>();
        globalComponents = new HashSet<>();
        typesWithConnectApiDeserializers = new HashSet<>();
        hasRemoteActionPerm = true;
        hasPersonAccountApiAvailable = true;
    }

    @Override
    public boolean hasPermission(final PlaceholderOrgPerm orgPerm) {
        return this.orgPerm.contains(orgPerm);
    }

    @Override
    public boolean hasPermissionForPermGuard(final Namespace referencingNamespace, final String orgPerm) {
        return allowedPermGuards.contains(new AllowedPermGuard(referencingNamespace, orgPerm));
    }

    @Override
    public boolean hasPersonAccountApiAvailable() {
        return hasPersonAccountApiAvailable;
    }

    @Override
    public boolean hasPrivateApi() {
        return hasPrivateApi;
    }

    @Override
    public boolean hasLocalizedTranslation() {
        return hasLocalizedTranslation;
    }

    @Override
    public boolean hasInternalSfdc() {
        return hasInternalSfdc;
    }

    @Override
    public boolean isTrustedApplication(TypeInfo arg0) {
        return isTrustedApplication;
    }

    @Override
    public boolean isReservedNamespace(final Namespace namespace) {
        return reservedNamespaces.contains(namespace);
    }

    @Override
    public boolean isReservedNamespace(final Namespace namespace, final boolean excludePackages) {
        return reservedNamespaces.contains(namespace);
    }

    /**
     * See {@link #isAccessibleOrTrustedNamespace(Namespace)}
     */
    @Override
    public boolean isAccessibleSystemNamespace(final Namespace namespace) {
        return accessibleSystemNamespaces.contains(namespace);
    }

    /**
     * Okay so this check and its partner isAccessibleSystemNamespace are used slightly differently.
     * This is like a black list check, that prevents referencing code from seeing things in a reserved namespace.
     * The other check allows code to see certain things if the code's namespace is a reserved namespace.
     * <p>
     * Hence here we return true by default, and the {@link #isAccessibleSystemNamespace(Namespace)} returns false
     * by default.
     */
    @Override
    public boolean isAccessibleOrTrustedNamespace(final Namespace namespace) {
        return true;
    }

    @Override
    public boolean isRunningTests() {
        return isRunningTests;
    }

    @Override
    public boolean isReallyRunningTests() {
        return isReallyRunningTests;
    }

    @Override
    public boolean isSfdc() {
        return isSfdc;
    }

    @Override
    public boolean hasApexParameterizedTypes() {
        return hasApexGenericTypes;
    }

    @Override
    public boolean isValidPackageVersion(final Namespace namespace, final StructuredVersion version) {
        return validPageVersions.containsEntry(namespace, version);
    }

    /**
     * @return 'true' for everything EXCEPT namespaces you've added through {@link #addManagedPackageNotInstalled(Namespace)}
     */
    @Override
    public boolean isManagedPackageInstalled(final Namespace namespace) {
        return !managedPackagesNotInstalled.contains(namespace);
    }

    @Override
    public boolean isSetupEntityVisibleToType(final SObjectTypeInfo type, final TypeInfo referencingType) {
        final TypeInfo visibleReferencingType = Iterables.getFirst(visibleSetupEntitiesToTypes.get(type), null);
        return visibleReferencingType != null
            && visibleReferencingType.getBytecodeName().equals(referencingType.getBytecodeName());
    }

    @Override
    public boolean hasConnectDeserializer(final TypeInfo type) {
        return typesWithConnectApiDeserializers.contains(type.getApexName());
    }

    @Override
    public boolean hasRemoteAction(final TypeInfo type) {
        return false;
    }

    @Override
    public boolean hasRemoteActionPerm() {
        return hasRemoteActionPerm;
    }

    @Override
    public boolean isGlobalComponent(final TypeInfo type) {
        return globalComponents.contains(type.getApexName());
    }

    /**
     * Things isManagedPackageInstalled will say 'false' to.
     */
    public TestAccessEvaluator addManagedPackageNotInstalled(final Namespace namespace) {
        managedPackagesNotInstalled.add(namespace);
        return this;
    }

    public TestAccessEvaluator addReservedNamespace(final Namespace namespace) {
        reservedNamespaces.add(namespace);
        return this;
    }

    public TestAccessEvaluator addPermission(final PlaceholderOrgPerm orgPerm) {
        this.orgPerm.add(orgPerm);
        return this;
    }

    public TestAccessEvaluator setHasInternalSfdc(final boolean hasInternalSfdc) {
        this.hasInternalSfdc = hasInternalSfdc;
        return this;
    }

    public TestAccessEvaluator addValidPackageVersion(final Namespace namespace, final StructuredVersion version) {
        validPageVersions.put(namespace, version);
        return this;
    }

    public TestAccessEvaluator addSetupEntityVisibleToType(
        final SObjectTypeInfo type,
        final String typeName
    ) {
        final StandardTypeInfo typeInfo = StandardTypeInfoImpl.builder()
            .setApexName(typeName)
            .setBytecodeName(typeName)
            .buildResolved();
        visibleSetupEntitiesToTypes.put(type, typeInfo);
        return this;
    }

    public TestAccessEvaluator setIsRunningTests(final boolean isRunningTests) {
        this.isRunningTests = isRunningTests;
        return this;
    }

    public TestAccessEvaluator setHasPrivateApi(final boolean hasPrivateApi) {
        this.hasPrivateApi = hasPrivateApi;
        return this;
    }

    public TestAccessEvaluator setIsTrustedApplication(final boolean isTrustedApplication) {
        this.isTrustedApplication = isTrustedApplication;
        return this;
    }

    public TestAccessEvaluator setHasLocalizedTranslation(final boolean hasLocalizedTranslation) {
        this.hasLocalizedTranslation = hasLocalizedTranslation;
        return this;
    }

    public TestAccessEvaluator setIsSfdc(final boolean isSfdc) {
        this.isSfdc = isSfdc;
        return this;
    }

    public TestAccessEvaluator setIsReallyRunningTests(final boolean isReallyRunningTests) {
        this.isReallyRunningTests = isReallyRunningTests;
        return this;
    }

    public TestAccessEvaluator setAccessibleSystemNamespace(final Namespace namespace) {
        accessibleSystemNamespaces.add(namespace);
        return this;
    }

    public TestAccessEvaluator setHasApexGenericType(final boolean hasApexGenericTypes) {
        this.hasApexGenericTypes = hasApexGenericTypes;
        return this;
    }

    public TestAccessEvaluator allowPermGuard(final Namespace namespace, final String permGuard) {
        allowedPermGuards.add(new AllowedPermGuard(namespace, permGuard));
        return this;
    }

    /**
     * It appears that remote action is enabled by default in most orgs, at least test orgs.
     * So we will behave the same.
     */
    public TestAccessEvaluator setHasRemoteActionPerm(final boolean hasRemoteActionPerm) {
        this.hasRemoteActionPerm = hasRemoteActionPerm;
        return this;
    }

    public TestAccessEvaluator setTypeWithConnectApiDeserializer(final String typeName) {
        typesWithConnectApiDeserializers.add(typeName);
        return this;
    }

    public void setGlobalComponent(final String globalComponent) {
        globalComponents.add(globalComponent);
    }

    private static class AllowedPermGuard {
        private final Namespace referencingNamespace;
        private final String permGuard;

        AllowedPermGuard(final Namespace namespace, final String permGuard) {
            referencingNamespace = namespace;
            this.permGuard = permGuard;
        }

        @Override
        public int hashCode() {
            return Objects.hash(referencingNamespace, permGuard);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            final AllowedPermGuard other = (AllowedPermGuard) obj;
            return Objects.equals(referencingNamespace, other.referencingNamespace)
                && Objects.equals(permGuard, other.permGuard);
        }
    }

    @Override
    public boolean isSecondGenerationPackagingNamespace(Namespace namespace) {
        return false;
    }

    @Override
    public boolean useTestValueForAnonymousScriptLengthLimit() {
        return false;
    }

    @Override
    public boolean hasNamespaceGuardedAccess(Namespace namespace, String arg1) {
        return false;
    }
}
