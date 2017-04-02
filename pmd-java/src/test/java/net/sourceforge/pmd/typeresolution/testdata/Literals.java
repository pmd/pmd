/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

public class Literals {
    String s = "s";
    boolean boolean1 = false;
    boolean boolean2 = true;
    Object obj = null;
    byte byte1 = 0;
    byte byte2 = 0x0F;
    byte byte3 = -007;
    short short1 = 0;
    short short2 = 0x0F;
    short short3 = -007;
    char char1 = 0;
    char char2 = 0x0F;
    char char3 = 007;
    char char4 = 'a';
    int int1 = 0;
    int int2 = 0x0F;
    int int3 = -007;
    int int4 = 'a';
    long long1 = 0;
    long long2 = 0x0F;
    long long3 = -007;
    long long4 = 0L;
    long long5 = 0x0Fl; // SUPPRESS CHECKSTYLE this explicitly tests lowercase l
    long long6 = -007L;
    long long7 = 'a';
    float float1 = 0.0f;
    float float2 = -10e+01f;
    float float3 = 0x08.08p3f;
    float float4 = 0xFF;
    float float5 = 'a';
    double double1 = 0.0;
    double double2 = -10e+01;
    double double3 = 0x08.08p3;
    double double4 = 0xFF;
    double double5 = 'a';
}
