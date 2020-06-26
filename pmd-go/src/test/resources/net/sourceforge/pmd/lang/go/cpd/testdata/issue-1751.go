func test(in *Value, param *Value) (*Value, *Error) {
	output := strings.Replace(in.String(), "\\", "\\\\", -1)
	output = strings.Replace(output, "\"", "\\\"", -1)
	output = strings.Replace(output, "'", "\\'", -1)
	return AsValue(output), nil
}
