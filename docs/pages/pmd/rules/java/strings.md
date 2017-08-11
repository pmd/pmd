---
title: String and StringBuffer
summary: These rules deal with different issues that can arise with manipulation of the String, StringBuffer, or StringBuilder instances.
permalink: pmd_rules_java_strings.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/strings.xml
---
## AppendCharacterWithChar

**Since:** 3.5

**Priority:** Medium (3)

Avoid concatenating characters as strings in StringBuffer/StringBuilder.append methods.

**Example(s):**

```
StringBuffer sb = new StringBuffer();
sb.append("a");		 // avoid this

StringBuffer sb = new StringBuffer();
sb.append('a');		// use this instead
```

## AvoidDuplicateLiterals

**Since:** 1.0

**Priority:** Medium (3)

Code containing duplicate String literals can usually be improved by declaring the String as a constant field.

**Example(s):**

```
private void bar() {
     buz("Howdy");
     buz("Howdy");
     buz("Howdy");
     buz("Howdy");
 }
 private void buz(String x) {}
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|exceptionfile||File containing strings to skip (one string per line), only used if ignore list is not set|
|separator|,|Ignore list separator|
|exceptionList||Strings to ignore|
|maxDuplicateLiterals|4|Max duplicate literals|
|minimumLength|3|Minimum string length to check|
|skipAnnotations|false|Skip literals within annotations|

## AvoidStringBufferField

**Since:** 4.2

**Priority:** Medium (3)

StringBuffers/StringBuilders can grow considerably, and so may become a source of memory leaks
if held within objects with long lifetimes.

**Example(s):**

```
public class Foo {
	private StringBuffer buffer;	// potential memory leak as an instance variable;
}
```

## ConsecutiveAppendsShouldReuse

**Since:** 5.1

**Priority:** Medium (3)

Consecutive calls to StringBuffer/StringBuilder .append should be chained, reusing the target object. This can improve the performance
by producing a smaller bytecode, reducing overhead and improving inlining. A complete analysis can be found [here](https://github.com/pmd/pmd/issues/202#issuecomment-274349067)

**Example(s):**

```
String foo = " ";

StringBuffer buf = new StringBuffer();
buf.append("Hello"); // poor
buf.append(foo);
buf.append("World");

StringBuffer buf = new StringBuffer();
buf.append("Hello").append(foo).append("World"); // good
```

## ConsecutiveLiteralAppends

**Since:** 3.5

**Priority:** Medium (3)

Consecutively calling StringBuffer/StringBuilder.append with String literals

**Example(s):**

```
StringBuffer buf = new StringBuffer();
buf.append("Hello").append(" ").append("World"); // poor
buf.append("Hello World");        				 // good
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|threshold|1|Max consecutive appends|

## InefficientEmptyStringCheck

**Since:** 3.6

**Priority:** Medium (3)

String.trim().length() is an inefficient way to check if a String is really empty, as it
creates a new String object just to check its size. Consider creating a static function that
loops through a string, checking Character.isWhitespace() on each character and returning
false if a non-whitespace character is found. You can refer to Apache's StringUtils#isBlank (in commons-lang)
or Spring's StringUtils#hasText (in the Springs framework) for existing implementations.

**Example(s):**

```
public void bar(String string) {
	if (string != null && string.trim().size() > 0) {
		doSomething();
	}
}
```

## InefficientStringBuffering

**Since:** 3.4

**Priority:** Medium (3)

Avoid concatenating non-literals in a StringBuffer constructor or append() since intermediate buffers will
need to be be created and destroyed by the JVM.

**Example(s):**

```
// Avoid this, two buffers are actually being created here
StringBuffer sb = new StringBuffer("tmp = "+System.getProperty("java.io.tmpdir"));
    
    // do this instead
StringBuffer sb = new StringBuffer("tmp = ");
sb.append(System.getProperty("java.io.tmpdir"));
```

## InsufficientStringBufferDeclaration

**Since:** 3.6

**Priority:** Medium (3)

Failing to pre-size a StringBuffer or StringBuilder properly could cause it to re-size many times
during runtime. This rule attempts to determine the total number the characters that are actually 
passed into StringBuffer.append(), but represents a best guess "worst case" scenario. An empty
StringBuffer/StringBuilder constructor initializes the object to 16 characters. This default
is assumed if the length of the constructor can not be determined.

**Example(s):**

```
StringBuffer bad = new StringBuffer();
bad.append("This is a long string that will exceed the default 16 characters");
        
StringBuffer good = new StringBuffer(41);
good.append("This is a long string, which is pre-sized");
```

## StringBufferInstantiationWithChar

**Since:** 3.9

**Priority:** Medium Low (4)

Individual character values provided as initialization arguments will be converted into integers.
This can lead to internal buffer sizes that are larger than expected. Some examples:

new StringBuffer() 		//  16
new StringBuffer(6)		//  6
new StringBuffer("hello world")  // 11 + 16 = 27
new StringBuffer('A')	//  chr(A) = 65
new StringBuffer("A")   //  1 + 16 = 17 

new StringBuilder() 		//  16
new StringBuilder(6)		//  6
new StringBuilder("hello world")  // 11 + 16 = 27
new StringBuilder('C')	 //  chr(C) = 67
new StringBuilder("A")   //  1 + 16 = 17

**Example(s):**

```
// misleading instantiation, these buffers
	// are actually sized to 99 characters long
StringBuffer  sb1 = new StringBuffer('c');   
StringBuilder sb2 = new StringBuilder('c');
  
// in these forms, just single characters are allocated
StringBuffer  sb3 = new StringBuffer("c");
StringBuilder sb4 = new StringBuilder("c");
```

## StringInstantiation

**Since:** 1.0

**Priority:** Medium High (2)

Avoid instantiating String objects; this is usually unnecessary since they are immutable and can be safely shared.

**Example(s):**

```
private String bar = new String("bar"); // just do a String bar = "bar";
```

## StringToString

**Since:** 1.0

**Priority:** Medium (3)

Avoid calling toString() on objects already known to be string instances; this is unnecessary.

**Example(s):**

```
private String baz() {
    String bar = "howdy";
    return bar.toString();
}
```

## UnnecessaryCaseChange

**Since:** 3.3

**Priority:** Medium (3)

Using equalsIgnoreCase() is faster than using toUpperCase/toLowerCase().equals()

**Example(s):**

```
boolean answer1 = buz.toUpperCase().equals("baz");	 		// should be buz.equalsIgnoreCase("baz")
    
boolean answer2 = buz.toUpperCase().equalsIgnoreCase("baz");	 // another unnecessary toUpperCase()
```

## UseEqualsToCompareStrings

**Since:** 4.1

**Priority:** Medium (3)

Using '==' or '!=' to compare strings only works if intern version is used on both sides.
Use the equals() method instead.

**Example(s):**

```
public boolean test(String s) {
    if (s == "one") return true; 		// unreliable
    if ("two".equals(s)) return true; 	// better
    return false;
}
```

## UseIndexOfChar

**Since:** 3.5

**Priority:** Medium (3)

Use String.indexOf(char) when checking for the index of a single character; it executes faster.

**Example(s):**

```
String s = "hello world";
  // avoid this
if (s.indexOf("d") {}
  // instead do this
if (s.indexOf('d') {}
```

## UselessStringValueOf

**Since:** 3.8

**Priority:** Medium (3)

No need to call String.valueOf to append to a string; just use the valueOf() argument directly.

**Example(s):**

```
public String convert(int i) {
	String s;
	s = "a" + String.valueOf(i);	// not required
	s = "a" + i; 					// preferred approach
	return s;
}
```

## UseStringBufferLength

**Since:** 3.4

**Priority:** Medium (3)

Use StringBuffer.length() to determine StringBuffer length rather than using StringBuffer.toString().equals("")
or StringBuffer.toString().length() == ...

**Example(s):**

```
StringBuffer sb = new StringBuffer();
    
if (sb.toString().equals("")) {}	    // inefficient 
    
if (sb.length() == 0) {}	    		// preferred
```

