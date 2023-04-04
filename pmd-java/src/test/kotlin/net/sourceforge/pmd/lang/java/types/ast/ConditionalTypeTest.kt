/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.ast

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import net.sourceforge.pmd.lang.java.types.*

/**
 * @author Clément Fournier
 */
class ConditionalTypeTest : FunSpec({}) {

    private val tested = mutableMapOf<TypePair, JTypeMirror>()

    private fun runTest(t1: JTypeMirror, t2: JTypeMirror, expected: JTypeMirror) {
        val key = TypePair(t1, t2)
        if (key in tested && tested[key] != expected)
            throw AssertionError("Already tested $t1 : $t2 against ${tested[key]}, doesn't match $expected")
        else if (key in tested) return

        tested[key] = expected

        withClue("$t1 : $t2 => $expected") {
            PolyResolution.computeStandaloneConditionalType(testTypeSystem, t1, t2) shouldBe expected
        }
    }


    private fun bnp(t: JTypeMirror, u: JTypeMirror) = TypeConversion.binaryNumericPromotion(t, u)

    init {

        val ts = testTypeSystem
        val prims = ts.primitiveGen
        val refTypes = ts.refTypeGen

        context("Tests for conditional expressions") {
            // we need a suspend fun

            test("Primitive Types") {
                prims.checkAll {
                    runTest(it, it, it)
                }
            }

            test("Primitive Types with left one boxed") {
                prims.checkAll {
                    runTest(it.box(), it, it)
                }
            }

            test("Primitive Types with right one boxed") {
                prims.checkAll {
                    runTest(it, it.box(), it)
                }
            }

            test("Primitive Types all boxed") {
                prims.checkAll {
                    runTest(it.box(), it.box(), it.box())
                }
            }

            test("Primitive Types with left one null") {
                prims.checkAll {
                    runTest(ts.NULL_TYPE, it, ts.lub(ts.NULL_TYPE, it.box()))
                }
            }

            test("Primitive Types with right one null") {
                prims.checkAll {
                    runTest(it, ts.NULL_TYPE, ts.lub(it.box(), ts.NULL_TYPE))
                }
            }

            test("Primitive Types with null and boxed") {
                prims.checkAll {
                    runTest(it.box(), ts.NULL_TYPE, it.box())
                }
            }

            test("Reference Types") {
                refTypes.checkAll {
                    runTest(it, it, it)
                }
            }

            test("Reference Types with left one null") {
                refTypes.checkAll {
                    runTest(ts.NULL_TYPE, it, it)
                }
            }

            test("Reference Types with right one null") {
                refTypes.checkAll {
                    runTest(it, ts.NULL_TYPE, it)
                }
            }

            val shortOnes =
                    listOf(ts.BYTE to ts.SHORT, ts.SHORT to ts.BYTE)
                            .flatMap { (a, b) ->
                                listOf(a to b, a.box() to b, a to b.box())
                            }


            test("Short Types (BYTE, SHORT)") {
                shortOnes.forEach { (a, b) -> runTest(a, b, ts.SHORT) }
            }

            val allPrims: List<JTypeMirror> = (prims.values + prims.values.map { it.box() })

            val bnpOnes = allPrims.zip(allPrims)
                    .filter { it !in shortOnes }
                    .filter { (a, b) -> a != b }
                    .filter { (a, b) -> a.isNumeric && b.isNumeric }

            test("Binary Numeric Promotion") {
                bnpOnes.forEach { (a, b) -> runTest(a, b, bnp(a, b)) }
            }

            test("Boolean") {
                (allPrims - ts.BOOLEAN - ts.BOOLEAN.box()).forEach {
                    runTest(it, ts.BOOLEAN, ts.lub(it.box(), ts.BOOLEAN.box()))
                    runTest(ts.BOOLEAN, it, ts.lub(it.box(), ts.BOOLEAN.box()))
                }
            }
        }
    }


    /*


                byte                     short                     char                     int                         long                 float                     double                     boolean                 Byte                     Short                     Character                     Integer                     Long                 Float                     Double                     Boolean                 null                Object

    byte        *byte                  short                  bnp(byte,char)         byte | bnp(byte,int)        bnp(byte,long)         bnp(byte,float)         bnp(byte,double)            lub(Byte,Boolean)            byte                     short                    bnp(byte,Character)          bnp(byte,Integer)           bnp(byte,Long)       bnp(byte,Float)           bnp(byte,Double)         lub(Byte,Boolean)        lub(Byte,null)         lub(Byte,Object)
    Byte        *byte                  short                  bnp(Byte,char)         byte | bnp(Byte,int)        bnp(Byte,long)         bnp(Byte,float)         bnp(Byte,double)            lub(Byte,Boolean)            Byte                     short                    bnp(Byte,Character)          bnp(Byte,Integer)           bnp(Byte,Long)       bnp(Byte,Float)           bnp(Byte,Double)         lub(Byte,Boolean)        Byte                   lub(Byte,Object)
    short       short                  *short                 bnp(short,char)        short | bnp(short,int)      bnp(short,long)        bnp(short,float)        bnp(short,double)           lub(Short,Boolean)           short                    short                    bnp(short,Character)         bnp(short,Integer)          bnp(short,Long)      bnp(short,Float)          bnp(short,Double)        lub(Short,Boolean)       lub(Short,null)        lub(Short,Object)
    Short       short                  *short                 bnp(Short,char)        short | bnp(Short,int)      bnp(Short,long)        bnp(Short,float)        bnp(Short,double)           lub(Short,Boolean)           short                    Short                    bnp(Short,Character)         bnp(Short,Integer)          bnp(Short,Long)      bnp(Short,Float)          bnp(Short,Double)        lub(Short,Boolean)       Short                  lub(Short,Object)
    char        bnp(char,byte)         bnp(char,short)        *char                  char | bnp(char,int)        bnp(char,long)         bnp(char,float)         bnp(char,double)            lub(Character,Boolean)       bnp(char,Byte)           bnp(char,Short)          char                         bnp(char,Integer)           bnp(char,Long)       bnp(char,Float)           bnp(char,Double)         lub(Character,Boolean)   lub(Character,null)    lub(Character,Object)
    Character   bnp(Character,byte)    bnp(Character,short)   *char                  char | bnp(Character,int)   bnp(Character,long)    bnp(Character,float)    bnp(Character,double)       lub(Character,Boolean)       bnp(Character,Byte)      bnp(Character,Short)     Character                    bnp(Character,Integer)      bnp(Character,Long)  bnp(Character,Float)      bnp(Character,Double)    lub(Character,Boolean)   Character              lub(Character,Object)
    int         byte | bnp(int,byte)   short | bnp(int,short) char | bnp(int,char)   int                         bnp(int,long)          bnp(int,float)          bnp(int,double)             lub(Integer,Boolean)         byte | bnp(int,Byte)     short | bnp(int,Short)   char | bnp(int,Character)    int                         bnp(int,Long)        bnp(int,Float)            bnp(int,Double)          lub(Integer,Boolean)     lub(Integer,null)      lub(Integer,Object)
    Integer     bnp(Integer,byte)      bnp(Integer,short)     bnp(Integer,char)      int                         bnp(Integer,long)      bnp(Integer,float)      bnp(Integer,double)         lub(Integer,Boolean)         bnp(Integer,Byte)        bnp(Integer,Short)       bnp(Integer,Character)       Integer                     bnp(Integer,Long)    bnp(Integer,Float)        bnp(Integer,Double)      lub(Integer,Boolean)     Integer                lub(Integer,Object)
    long        bnp(long,byte)         bnp(long,short)        bnp(long,char)         bnp(long,int)               long                   bnp(long,float)         bnp(long,double)            lub(Long,Boolean)            bnp(long,Byte)           bnp(long,Short)          bnp(long,Character)          bnp(long,Integer)           long                 bnp(long,Float)           bnp(long,Double)         lub(Long,Boolean)        lub(Long,null)         lub(Long,Object)
    Long        bnp(Long,byte)         bnp(Long,short)        bnp(Long,char)         bnp(Long,int)               long                   bnp(Long,float)         bnp(Long,double)            lub(Long,Boolean)            bnp(Long,Byte)           bnp(Long,Short)          bnp(Long,Character)          bnp(Long,Integer)           Long                 bnp(Long,Float)           bnp(Long,Double)         lub(Long,Boolean)        Long                   lub(Long,Object)
    float       bnp(float,byte)        bnp(float,short)       bnp(float,char)        bnp(float,int)              bnp(float,long)        float                   bnp(float,double)           lub(Float,Boolean)           bnp(float,Byte)          bnp(float,Short)         bnp(float,Character)         bnp(float,Integer)          bnp(float,Long)      float                     bnp(float,Double)        lub(Float,Boolean)       lub(Float,null)        lub(Float,Object)
    Float       bnp(Float,byte)        bnp(Float,short)       bnp(Float,char)        bnp(Float,int)              bnp(Float,long)        float                   bnp(Float,double)           lub(Float,Boolean)           bnp(Float,Byte)          bnp(Float,Short)         bnp(Float,Character)         bnp(Float,Integer)          bnp(Float,Long)      Float                     bnp(Float,Double)        lub(Float,Boolean)       Float                  lub(Float,Object)
    double      bnp(double,byte)       bnp(double,short)      bnp(double,char)       bnp(double,int)             bnp(double,long)       bnp(double,float)       double                      lub(Double,Boolean)          bnp(double,Byte)         bnp(double,Short)        bnp(double,Character)        bnp(double,Integer)         bnp(double,Long)     bnp(double,Float)         double                   lub(Double,Boolean)      lub(Double,null)       lub(Double,Object)
    Double      bnp(Double,byte)       bnp(Double,short)      bnp(Double,char)       bnp(Double,int)             bnp(Double,long)       bnp(Double,float)       double                      lub(Double,Boolean)          bnp(Double,Byte)         bnp(Double,Short)        bnp(Double,Character)        bnp(Double,Integer)         bnp(Double,Long)     bnp(Double,Float)         Double                   lub(Double,Boolean)      Double                 lub(Double,Object)
    boolean     lub(Boolean,Byte)      lub(Boolean,Short)     lub(Boolean,Character) lub(Boolean,Integer)        lub(Boolean,Long)      lub(Boolean,Float)      lub(Boolean,Double)         boolean                      lub(Boolean,Byte)        lub(Boolean,Short)       lub(Boolean,Character)       lub(Boolean,Integer)        lub(Boolean,Long)    lub(Boolean,Float)        lub(Boolean,Double)      boolean                  lub(Boolean,null)      lub(Boolean,Object)
    Boolean     lub(Boolean,Byte)      lub(Boolean,Short)     lub(Boolean,Character) lub(Boolean,Integer)        lub(Boolean,Long)      lub(Boolean,Float)      lub(Boolean,Double)         boolean                      lub(Boolean,Byte)        lub(Boolean,Short)       lub(Boolean,Character)       lub(Boolean,Integer)        lub(Boolean,Long)    lub(Boolean,Float)        lub(Boolean,Double)      Boolean                  Boolean                lub(Boolean,Object)
    null        lub(null,Byte)         lub(null,Short)        lub(null,Character)    lub(null,Integer)           lub(null,Long)         lub(null,Float)         lub(null,Double)            lub(null,Boolean)            Byte                     Short                    Character                    Integer                     Long                 Float                     Double                   Boolean                  null                   lub(null,Object)
    Object      lub(Object,Byte)       lub(Object,Short)      lub(Object,Character)  lub(Object,Integer)         lub(Object,Long)       lub(Object,Float)       lub(Object,Double)          lub(Object,Boolean)          lub(Object,Byte)         lub(Object,Short)        lub(Object,Character)        lub(Object,Integer)         lub(Object,Long)     lub(Object,Float)         lub(Object,Double)       lub(Object,Boolean)      Object                 Object


    The following tables summarize the rules above by giving the type of a conditional
    expression for all possible types of its second and third operands. bnp(..) means
    to apply binary numeric promotion. The form "T | bnp(..)" is used where one operand
    is a constant expression of type int and may be representable in type T, where binary
    numeric promotion is used if the operand is not representable in type T. The operand
    type Object means any reference type other than the null type and the eight wrapper
    classes Boolean, Byte, Short, Character, Integer, Long, Float, Double.


     */

}
