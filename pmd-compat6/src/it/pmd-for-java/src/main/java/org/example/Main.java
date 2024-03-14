package org.example;

public class Main {
    public static void main(String[] args) {
        String thisIsAUnusedLocalVar = "a";
        System.out.println("Hello world!");

        String thisIsASuppressedUnusedLocalVar = "b"; // NOPMD suppressed
    }
}
