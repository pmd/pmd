/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.matchers.haveSize
import io.kotlintest.matchers.types.shouldBeSameInstanceAs
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.AbstractFunSpec

/**
 * @author Clément Fournier
 */
class GraphTest : AbstractFunSpec({

    test("Test unique graph") {


        val graph = Graph.UniqueGraph<String>()

        val v1 = graph.addLeaf("V1")
        val v2 = graph.addLeaf("V1")

        v1.shouldBeSameInstanceAs(v2)
    }

    test("Test tarjan ") {


        val graph = Graph.UniqueGraph<String>()

        val v1 = graph.addLeaf("a")
        val v2 = graph.addLeaf("b")
        val v3 = graph.addLeaf("c")
        val v4 = graph.addLeaf("d")


        graph.vertices shouldBe setOf(v1, v2, v3, v4)

        graph.addEdge(v1, v2)
        graph.addEdge(v2, v3)
        graph.addEdge(v3, v1)

        graph.addEdge(v2, v4)

        graph.mergeCycles()

        graph.vertices should haveSize(2)

        graph.vertices.shouldContain(v4)

        graph.vertices.toList()[0].data shouldBe setOf("a", "b", "c")
        graph.vertices.toList()[1].data shouldBe setOf("d")

    }

    test("Test self loop ") {


        val graph = Graph.UniqueGraph<String>()

        val v1 = graph.addLeaf("a")

        graph.vertices shouldBe setOf(v1)

        graph.addEdge(v1, v1)

    }

    test("Test toposort") {


        val graph = Graph.UniqueGraph<String>()

        val v1 = graph.addLeaf("a")
        val v2 = graph.addLeaf("b")
        val v3 = graph.addLeaf("c")
        val v4 = graph.addLeaf("d")


        graph.vertices shouldBe setOf(v1, v2, v3, v4)

        graph.addEdge(v1, v2)
        graph.addEdge(v2, v3)
        graph.addEdge(v3, v1)

        graph.addEdge(v4, v1)

        graph.mergeCycles()

        graph.vertices should haveSize(2)

        graph.topologicalSort() shouldBe listOf(setOf("a", "b", "c"), setOf("d"))

    }
})
