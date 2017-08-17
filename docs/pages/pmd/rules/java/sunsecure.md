---
title: Security Code Guidelines
summary: These rules check the security guidelines from Sun, published at http://java.sun.com/security/seccodeguide.html#gcg
permalink: pmd_rules_java_sunsecure.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/sunsecure.xml
keywords: Security Code Guidelines, MethodReturnsInternalArray, ArrayIsStoredDirectly
---
## ArrayIsStoredDirectly

**Since:** PMD 2.2

**Priority:** Medium (3)

Constructors and methods receiving arrays should clone objects and store the copy.
This prevents future changes from the user from affecting the original array.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.sunsecure.ArrayIsStoredDirectlyRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/sunsecure/ArrayIsStoredDirectlyRule.java)

**Example(s):**

``` java
public class Foo {
    private String [] x;
        public void foo (String [] param) {
        // Don't do this, make a copy of the array at least
        this.x=param;
    }
}
```

## MethodReturnsInternalArray

**Since:** PMD 2.2

**Priority:** Medium (3)

Exposing internal arrays to the caller violates object encapsulation since elements can be 
removed or replaced outside of the object that owns it. It is safer to return a copy of the array.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.sunsecure.MethodReturnsInternalArrayRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/sunsecure/MethodReturnsInternalArrayRule.java)

**Example(s):**

``` java
public class SecureSystem {
    UserData [] ud;
    public UserData [] getUserData() {
        // Don't return directly the internal array, return a copy
        return ud;
    }
}
```

