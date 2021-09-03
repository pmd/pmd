// file for supporting swift 5.0 changes : https://github.com/apple/swift/blob/master/CHANGELOG.md#swift-5

// https://github.com/apple/swift-evolution/blob/master/proposals/0235-add-result.md
enum Result<Success, Failure: Error> {
    case success(Success)
    case failure(Failure)
}

// https://github.com/apple/swift-evolution/blob/master/proposals/0228-fix-expressiblebystringinterpolation.md
struct MyType {}
#if compiler(<5.0)
extension MyType : _ExpressibleByStringInterpolation { }
#else
extension MyType : ExpressibleByStringInterpolation { }
#endif

//
func foo(_ fn: @autoclosure () -> Int) {}
func bar(_ fn: @autoclosure () -> Int) {
  //foo(fn)   // Incorrect, `fn` can't be forwarded and has to be called
  foo(fn()) // Ok
}

// https://github.com/apple/swift-evolution/blob/master/proposals/0216-dynamic-callable.md
@dynamicCallable
struct ToyCallable {
  func dynamicallyCall(withArguments: [Int]) {}
  func dynamicallyCall(withKeywordArguments: KeyValuePairs<String, Int>) {}
}
let toy = ToyCallable()
toy(1, 2, 3) // desugars to `x.dynamicallyCall(withArguments: [1, 2, 3])`
toy(label: 1, 2) // desugars to `x.dynamicallyCall(withKeywordArguments: ["label": 1, "": 2])`

// https://github.com/apple/swift-evolution/blob/master/proposals/0227-identity-keypath.md
let id = \Int.self

var x = 2
print(x[keyPath: id]) // prints 2
x[keyPath: id] = 3
print(x[keyPath: id]) // prints 3

// https://www.swiftbysundell.com/articles/string-literals-in-swift/
let rawString = #"Press "Continue" to close this dialog."#
extension URL {
    func html(withTitle title: String) -> String {
        return ##"<a \href="\#(absoluteString)">\#(title)</a>"##
    }
}

let rawMultiString = ###"a\###"###
let rawMultiString2 = ###"""a\###
""hey""
"""###
