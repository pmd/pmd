// Fixture for KotlinHasUnresolvedReferenceFunctionTest.
// com.example.external is not in this source directory, so the type-mapper
// cannot resolve it -- those imports should be flagged.
// app.local.LocalClass IS defined in LocalClass.kt, but resolving a type across a package
// boundary via import is a known limitation (unlike same-package or same-file resolution,
// which both work -- see LocalClass.kt), so this import is currently ALSO flagged as unresolved.

import com.example.external.MissingClass      // line 8: unresolved
import com.example.external.AnotherMissing    // line 9: unresolved
import app.local.LocalClass                   // line 10: unresolved (known limitation, not yet resolved cross-file)

fun use(m: MissingClass, l: LocalClass): String = l.value
