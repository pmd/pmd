package com.pmd.test;

public class GitHubBug1780OuterClass {
    public GitHubBug1780OuterClass() {
        System.out.println("Inner Class AdapterClass");
    }
    public class InnerClass {
        public InnerClass() {
            System.out.println("Inner Class Constructor");
        }
    }
    private static class StaticInnerClass extends InnerClass {
        public StaticInnerClass() {
            new GitHubBug1780OuterClass().super();
            System.out.println("StaticInnerClass Constructor");
        }
    }
    public static void main(String args[]) {
        new GitHubBug1780OuterClass.StaticInnerClass();
    }
}
