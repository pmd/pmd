/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

public final class ASTLanguageSpecification extends AbstractModelicaNode {
    private String externalLanguage;

    ASTLanguageSpecification(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptModelicaVisitor(ModelicaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    void setExternalLanguage(String externalLanguage) {
        this.externalLanguage = externalLanguage;
    }

    public String getExternalLanguage() {
        return externalLanguage;
    }
}
