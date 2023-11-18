package org.example;

public class ClassB {
    public int method1(int a, int b, int c) {
        int d = (a + b + c + 1) * 10;
        int e = (a + b + c - 1) * 5;
        int f = (a + b + c);
        return d * e * f + d + e + f;
    }
}
