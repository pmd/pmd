/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;

/**
 * Acts as a bridge between outer parts (e.g. symbol table) and the restricted
 * access internal API of this package.
 *
 * <p>Note: This is internal API.
 */
@InternalApi
public final class InternalApiBridge {


    private static final Set<String> PRIMITIVE_TYPES;

    static {
        PRIMITIVE_TYPES = new HashSet<>();
        PRIMITIVE_TYPES.add("boolean");
        PRIMITIVE_TYPES.add("char");
        PRIMITIVE_TYPES.add("byte");
        PRIMITIVE_TYPES.add("short");
        PRIMITIVE_TYPES.add("int");
        PRIMITIVE_TYPES.add("long");
        PRIMITIVE_TYPES.add("float");
        PRIMITIVE_TYPES.add("double");
    }

    private InternalApiBridge() {

    }

    @Deprecated
    public static ASTVariableDeclaratorId newVarId(String image) {
        ASTVariableDeclaratorId varid = new ASTVariableDeclaratorId(JavaParserImplTreeConstants.JJTVARIABLEDECLARATORID);
        varid.setImage(image);
        return varid;
    }


    /**
     * Creates a fake method name declaration for built-in methods from Java
     * like the Enum Method "valueOf".
     *
     * @param methodName     the method name
     * @param parameterTypes the reference types of each parameter of the method
     *
     * @return a method name declaration
     */
    public static ASTMethodDeclarator createBuiltInMethodDeclaration(final String methodName, final String... parameterTypes) {
        ASTMethodDeclaration methodDeclaration = new ASTMethodDeclaration(0);
        methodDeclaration.setPublic(true);

        ASTMethodDeclarator methodDeclarator = new ASTMethodDeclarator(0);
        methodDeclarator.setImage(methodName);

        ASTFormalParameters formalParameters = new ASTFormalParameters(0);
        methodDeclaration.addChild(methodDeclarator, 0);
        methodDeclarator.addChild(formalParameters, 0);

        /*
         * jjtAddChild resizes it's child node list according to known indexes.
         * Going backwards makes sure the first time it gets the right size avoiding copies.
         */
        for (int i = parameterTypes.length - 1; i >= 0; i--) {
            ASTFormalParameter formalParameter = new ASTFormalParameter(0);
            formalParameters.addChild(formalParameter, i);

            ASTVariableDeclaratorId variableDeclaratorId = new ASTVariableDeclaratorId(0);
            variableDeclaratorId.setImage("arg" + i);
            formalParameter.addChild(variableDeclaratorId, 1);

            ASTType type = new ASTType(0);
            formalParameter.addChild(type, 0);

            if (PRIMITIVE_TYPES.contains(parameterTypes[i])) {
                ASTPrimitiveType primitiveType = new ASTPrimitiveType(0);
                primitiveType.setImage(parameterTypes[i]);
                type.addChild(primitiveType, 0);
            } else {
                ASTReferenceType referenceType = new ASTReferenceType(0);
                type.addChild(referenceType, 0);

                // TODO : this could actually be a primitive array...
                ASTClassOrInterfaceType classOrInterfaceType = new ASTClassOrInterfaceType(0);
                classOrInterfaceType.setImage(parameterTypes[i]);
                referenceType.addChild(classOrInterfaceType, 0);
            }
        }

        return methodDeclarator;
    }


    @Deprecated
    public static ASTPrimaryPrefix newThisSuperPrefix(String image, boolean isThis) {
        ASTPrimaryPrefix prefix = new ASTPrimaryPrefix(JavaParserImplTreeConstants.JJTPRIMARYPREFIX);
        if (isThis) {
            prefix.setUsesThisModifier();
        } else {
            prefix.setUsesSuperModifier();
        }
        ASTName name = new ASTName(JavaParserImplTreeConstants.JJTNAME);
        name.setImage(image);
        prefix.addChild(name, 0);
        return prefix;
    }

    public static JavaccTokenDocument javaTokenDoc(String fullText) {
        return new JavaTokenDocument(fullText);
    }

}
