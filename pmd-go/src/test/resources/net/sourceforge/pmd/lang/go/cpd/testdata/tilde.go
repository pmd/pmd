package main

import "fmt"

// https://github.com/pmd/pmd/issues/6235
func sanitizeOptionalValue[T ~string](value *T) *T {
	if value == nil || *value == "" {
		return nil
	}

	return value
}

type Number interface {
	~int | ~int64 | ~float64
}

func Sum[K comparable, V Number](m map[K]V) V {
	var s V
	for _, v := range m {
		s += v
	}
	return s
}

func main() {
	str := "Hello, world!\n"
	sanitized := sanitizeOptionalValue(&str)
	fmt.Printf(*sanitized)
	fmt.Println(Sum[string, int](map[string]int{"a": 1, "b": 2}))
}
