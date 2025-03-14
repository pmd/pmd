// file for supporting swift 5.2 changes : https://github.com/apple/swift/blob/master/CHANGELOG.md#swift-52

// https://github.com/apple/swift-evolution/blob/master/proposals/0253-callable.md
struct Adder {
	var base: Int
	func callAsFunction(_ x: Int) -> Int {
		return x + base
	}
}
var adder = Adder(base: 3)
adder(10) // returns 13, same as `adder.callAsFunction(10)`

// https://github.com/apple/swift-evolution/blob/master/proposals/0249-key-path-literal-function-expressions.md
struct User {
	let email: String
	let isAdmin: Bool
}

users.map(\.email) // this is equivalent to: users.map { $0[keyPath: \User.email] }

// https://bugs.swift.org/browse/SR-6118
struct Subscriptable {
	subscript(x: Int, y: Int = 0) {
		...
	}
}

let s = Subscriptable()
print(s[0])
