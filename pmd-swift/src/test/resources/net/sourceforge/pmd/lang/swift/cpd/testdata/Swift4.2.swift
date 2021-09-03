// file for supporting swift 4.2 changes : https://github.com/apple/swift/blob/master/CHANGELOG.md#swift-42
// can be compiled with: swift pmd-swift/src/test/resources/net/sourceforge/pmd/cpd/Swift4.2.swift

let diceRoll = Int.random(in: 1 ... 6)
let randomUnit = Double.random(in: 0 ..< 1)
let randomBool = Bool.random()

// https://github.com/apple/swift-evolution/blob/master/proposals/0193-cross-module-inlining-and-specialization.md
public class C {
  public func f() {}
}

public class Cbis {
  @usableFromInline internal class D {
    @usableFromInline internal func f() {}

    @inlinable internal func g() {}
  }
}

// https://github.com/apple/swift-evolution/blob/master/proposals/0196-diagnostic-directives.md
#warning("this is incomplete")

#if MY_BUILD_CONFIG && MY_OTHER_BUILD_CONFIG
  #error("MY_BUILD_CONFIG and MY_OTHER_BUILD_CONFIG cannot both be set")
#endif
