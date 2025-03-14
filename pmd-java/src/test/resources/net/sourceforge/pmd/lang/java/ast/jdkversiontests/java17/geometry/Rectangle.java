/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package com.example.geometry;

/**
 * @see <a href="https://openjdk.java.net/jeps/409">JEP 409: Sealed Classes</a>
 */
public sealed class Rectangle extends Shape 
    permits TransparentRectangle, FilledRectangle { }

