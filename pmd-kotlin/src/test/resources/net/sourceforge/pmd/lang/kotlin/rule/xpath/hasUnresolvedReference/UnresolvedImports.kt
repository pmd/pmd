// Fixture for KotlinHasUnresolvedReferenceFunctionTest.
// com.example.external is not in this source directory, so the type-mapper
// cannot resolve it -- those imports should be flagged.
// app.local.LocalClass IS defined in LocalClass.kt in this directory,
// so that import should NOT be flagged as unresolved.

import com.example.external.MissingClass      // line 7: unresolved
import com.example.external.AnotherMissing    // line 8: unresolved
import app.local.LocalClass                   // line 9: resolved (source in same dir)

fun use(m: MissingClass, l: LocalClass): String = l.value
