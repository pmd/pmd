func main() {
	//string底层是一个byte数组，因此string也可以进行切片操作
	str := "hello world"//string是不可变的
	slice := str[4:]
	fmt.Println(slice)

	//若要对string进行修改需要将string修改为byte或rune的切片在操作
	//但是转为byte无法进行中文操作
	bytes := []byte(str)
	bytes[2] = 'x'
	str = string(bytes)
	fmt.Println(str)

	//转换成rune可以对中文进行操作
	runes := []rune(str)
	runes[0] = '哈'
	str = string(runes)
	fmt.Println(str)

}
