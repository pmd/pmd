/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * Tests parsing after supporting String Templates. "}" is ambiguous.
 */

@MyAnnotation(a = { "a" }, b = "b") // "}" might be recognized as STRING_TEMPLATE_END, but it is not
class AnnotationValueInitializers { }

@MyAnnotation(a = { "a" }, b = "#b") // "}" might be recognized as STRING_TEMPLATE_END, but it is not
class AnnotationValueInitializers2 { }
