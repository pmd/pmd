#include <iostream>
int main() {
  int a[50] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
  double b[14] = {
    157, // decimal literal
    0377, // octal literal
    36'000'000, // literal with digit separators
    0x3fff, // hexadecimal literal
    0X3FFF, // same hexadecimal literal
    328u, // unsigned value
    0x7FFFFFL, // long value
    0776745ul, // unsigned long value
    18.46, // double with number after decimal point
    38., // double without number after decimal point
    18.46e0, // double with exponent
    18.46e1, // double with exponent
    0B001101, // C++ 14 binary literal
    0b000001, // C++ 14 binary literal
  };
  int c[3][4] = {{0,1,2,3},{4,5,6,7},{8,9,10,11}}; // multi-dimensional array
  int d[3] = {a, a, a}; // identifiers should not be filtered out
  int e[1][3] = {{a, a, a}}; // identifiers in multi-dimensional array
  int f[1] = {main()}; // method invocations should not be filtered out
  int g[1][1] = {{main()}}; // method invocation in multi-dimensional array
  return 0;
}
