#nullable enable
using System;
using System.Collections.Generic;

class CSharp7And8Additions
{
    private static void Literals()
    {
        int x = 30_000_000; // digit separators
        int b = 0b00101000; // boolean literal
    }
    
    private static unsafe void DisplaySize<T>() where T : unmanaged // unmanaged keyword
    {
        Console.WriteLine($"{typeof(T)} is unmanaged and its size is {sizeof(T)} bytes");
    }

    private static void Operators()
    {
        List<int>? l = null;
        (l ??= new List<int>()).Add(5); // null-coalescing assignment operator 
        
        var array = new int[] { 1, 2, 3, 4, 5 };
        var slice1 = array[2..^3]; // range operator
    }
}
