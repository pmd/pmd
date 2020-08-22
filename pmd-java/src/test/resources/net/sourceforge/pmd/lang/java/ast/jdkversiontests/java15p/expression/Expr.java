/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package com.example.expression;

/**
 * @see <a href="https://openjdk.java.net/jeps/360">JEP 360: Sealed Classes (Preview)</a>
 */
public sealed interface Expr
    permits ConstantExpr, PlusExpr, TimesExpr, NegExpr { }
