---
title: Android
summary: These rules deal with the Android SDK, mostly related to best practices. To get better results, make sure that the auxclasspath is defined for type resolution to work.
permalink: pmd_rules_java_android.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/android.xml
---
## CallSuperFirst
**Since:** 4.2.5

**Priority:** Medium (3)

Super should be called at the start of the method

**Example(s):**
```
public class DummyActivity extends Activity {
	public void onCreate(Bundle bundle) {
     // missing call to super.onCreate(bundle)
		foo();
	}
}
```

## CallSuperLast
**Since:** 4.2.5

**Priority:** Medium (3)

Super should be called at the end of the method

**Example(s):**
```
public class DummyActivity extends Activity {
	public void onPause() {
		foo();
		// missing call to super.onPause()
	}
}
```

## DoNotHardCodeSDCard
**Since:** 4.2.6

**Priority:** Medium (3)

Use Environment.getExternalStorageDirectory() instead of "/sdcard"

**Example(s):**
```
public class MyActivity extends Activity {
	protected void foo() {
		String storageLocation = "/sdcard/mypackage";	// hard-coded, poor approach

		storageLocation = Environment.getExternalStorageDirectory() + "/mypackage"; // preferred approach
	}
}
```

