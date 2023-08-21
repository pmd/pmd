/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package com.example.geometry;

/**
 * @see <a href="https://openjdk.java.net/jeps/409">JEP 409: Sealed Classes</a>
 */
public sealed class Shape
    permits Circle, Rectangle, Square { }

