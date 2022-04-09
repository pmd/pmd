using System;
using System.Collections;
using System.Collections.Generic;
public class LongLists {
    List<byte> l = new List<byte> {
      0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    };
    byte[,] a = {1,2,3,4,5};
    byte[,] b = {{1,2},{3,4},{5,6}};
    int[,] c = {
      157, // decimal literal
      0377, // octal literal
      36_000_000, // literal with digit separators
      0x3fff, // hexadecimal literal
      0X3FFF, // same hexadecimal literal
      328u, // unsigned value
      0x7FFFFFL, // long value
      0776745ul, // unsigned long value
      18.46, // float
      18.46e0, // double with exponent
      18.46e1, // double with exponent
      0b000001, // binary literal
    };
}
