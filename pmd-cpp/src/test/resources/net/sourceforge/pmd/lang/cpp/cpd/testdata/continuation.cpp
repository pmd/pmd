#include <iostream>

// here is a multiline macro
#define MULTI \
	1

// here is a multiline variable called ab
static int a\
b = 5;


int main()
{
  // string continuation without backslash
  std::cout << "1 Hello, "
               "world!\n";
  // string continuation with backslash inside string
  std::cout << "2 Hello, \
world!\n";
  // string continuation with double backslash inside string
  // -> compiler warning (invalid escape sequence \w), but still compiles
  std::cout << "3 Hello, \\
world!\n";
  // string continuation with backslash outside of string
  std::cout << "4 Hello, " \
              "world!\n";
  std::cout << "ab=" << ab << "\n";
  return 0;
}
