---
title: JavaBeans
summary: The JavaBeans Ruleset catches instances of bean rules not being followed.
permalink: pmd_rules_java_javabeans.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/javabeans.xml
keywords: JavaBeans, BeanMembersShouldSerialize, MissingSerialVersionUID
---
## BeanMembersShouldSerialize

**Since:** PMD 1.1

**Priority:** Medium (3)

If a class is a bean, or is referenced by a bean directly or indirectly it needs to be serializable. 
Member variables need to be marked as transient, static, or have accessor methods in the class. Marking 
variables as transient is the safest and easiest modification. Accessor methods should follow the Java 
naming conventions, i.e. for a variable named foo, getFoo() and setFoo() accessor methods should be provided.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.javabeans.BeanMembersShouldSerializeRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/javabeans/BeanMembersShouldSerializeRule.java)

**Example(s):**

``` java
private transient int someFoo;  // good, it's transient
private static int otherFoo;    // also OK
private int moreFoo;            // OK, has proper accessors, see below
private int badFoo;             // bad, should be marked transient

private void setMoreFoo(int moreFoo){
      this.moreFoo = moreFoo;
}

private int getMoreFoo(){
      return this.moreFoo;
}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|prefix||A variable prefix to skip, i.e., m_|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/javabeans.xml/BeanMembersShouldSerialize" />
```

## MissingSerialVersionUID

**Since:** PMD 3.0

**Priority:** Medium (3)

Serializable classes should provide a serialVersionUID field.

```
//ClassOrInterfaceDeclaration
 [
  count(ClassOrInterfaceBody/ClassOrInterfaceBodyDeclaration
   /FieldDeclaration/VariableDeclarator/VariableDeclaratorId[@Image='serialVersionUID']) = 0
and
  count(ImplementsList
   [ClassOrInterfaceType/@Image='Serializable'
   or ClassOrInterfaceType/@Image='java.io.Serializable']) =1
and
   @Abstract = 'false'
]
```

**Example(s):**

``` java
public class Foo implements java.io.Serializable {
    String name;
    // Define serialization id to avoid serialization related bugs
    // i.e., public static final long serialVersionUID = 4328743;
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/javabeans.xml/MissingSerialVersionUID" />
```

