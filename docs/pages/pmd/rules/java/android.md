---
title: Android
summary: These rules deal with the Android SDK, mostly related to best practices. To get better results, make sure that the auxclasspath is defined for type resolution to work.
permalink: pmd_rules_java_android.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/android.xml
keywords: Android, CallSuperFirst, CallSuperLast, DoNotHardCodeSDCard
---
## CallSuperFirst

**Since:** PMD 4.2.5

**Priority:** Medium (3)

Super should be called at the start of the method

```
//MethodDeclaration[MethodDeclarator[
  @Image='onCreate' or
  @Image='onConfigurationChanged' or
  @Image='onPostCreate' or
  @Image='onPostResume' or
  @Image='onRestart' or
  @Image='onRestoreInstanceState' or
  @Image='onResume' or
  @Image='onStart'
  ]]
    /Block[not(
      (BlockStatement[1]/Statement/StatementExpression/PrimaryExpression[./PrimaryPrefix[@SuperModifier='true']]/PrimarySuffix[@Image= ancestor::MethodDeclaration/MethodDeclarator/@Image]))]
[ancestor::ClassOrInterfaceDeclaration[ExtendsList/ClassOrInterfaceType[
  typeof(@Image, 'android.app.Activity', 'Activity') or
  typeof(@Image, 'android.app.Application', 'Application') or
  typeof(@Image, 'android.app.Service', 'Service')
]]]
```

**Example(s):**

``` java
public class DummyActivity extends Activity {
    public void onCreate(Bundle bundle) {
        // missing call to super.onCreate(bundle)
        foo();
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/android.xml/CallSuperFirst" />
```

## CallSuperLast

**Since:** PMD 4.2.5

**Priority:** Medium (3)

Super should be called at the end of the method

```
//MethodDeclaration[MethodDeclarator[
  @Image='finish' or
  @Image='onDestroy' or
  @Image='onPause' or
  @Image='onSaveInstanceState' or
  @Image='onStop' or
  @Image='onTerminate'
  ]]
   /Block/BlockStatement[last()]
    [not(Statement/StatementExpression/PrimaryExpression[./PrimaryPrefix[@SuperModifier='true']]/PrimarySuffix[@Image= ancestor::MethodDeclaration/MethodDeclarator/@Image])]
[ancestor::ClassOrInterfaceDeclaration[ExtendsList/ClassOrInterfaceType[
  typeof(@Image, 'android.app.Activity', 'Activity') or
  typeof(@Image, 'android.app.Application', 'Application') or
  typeof(@Image, 'android.app.Service', 'Service')
]]]
```

**Example(s):**

``` java
public class DummyActivity extends Activity {
    public void onPause() {
        foo();
        // missing call to super.onPause()
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/android.xml/CallSuperLast" />
```

## DoNotHardCodeSDCard

**Since:** PMD 4.2.6

**Priority:** Medium (3)

Use Environment.getExternalStorageDirectory() instead of "/sdcard"

```
//Literal[starts-with(@Image,'"/sdcard')]
```

**Example(s):**

``` java
public class MyActivity extends Activity {
    protected void foo() {
        String storageLocation = "/sdcard/mypackage";   // hard-coded, poor approach

       storageLocation = Environment.getExternalStorageDirectory() + "/mypackage"; // preferred approach
    }
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/android.xml/DoNotHardCodeSDCard" />
```

