/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package com.example.geometry;

/**
 * @see <a href="https://openjdk.java.net/jeps/397">JEP 397: Sealed Classes (Second Preview)</a>
 */
public sealed class Rectangle extends Shape 
    permits TransparentRectangle, FilledRectangle { }

