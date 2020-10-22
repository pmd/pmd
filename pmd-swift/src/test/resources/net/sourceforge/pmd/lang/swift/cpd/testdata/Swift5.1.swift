// file for supporting swift 5.1 changes : https://github.com/apple/swift/blob/master/CHANGELOG.md#swift-5

// https://github.com/apple/swift-evolution/blob/master/proposals/0258-property-wrappers.md#property-wrapper-types-in-the-wild
// https://developer.apple.com/documentation/combine/published
// Publishing a property with the @Published attribute creates a publisher of this type. You access the publisher with the $ operator, as shown here:
import Combine
class Weather {
    @Published var temperature: Double
    init(temperature: Double) {
        self.temperature = temperature
    }
}

let weather = Weather(temperature: 20)
let cancellable = weather.$temperature
    .sink() {
        print ("Temperature now: \($0)")
}
weather.temperature = 25

// Prints:
// Temperature now: 20.0
// Temperature now: 25.0

// https://github.com/apple/swift-evolution/blob/master/proposals/0244-opaque-result-types.md
func makeMeACollection() -> some Collection {
  return [1, 2, 3]
}

// https://github.com/apple/swift-evolution/blob/master/proposals/0252-keypath-dynamic-member-lookup.md
@dynamicMemberLookup
struct Lens<T> {
  let getter: () -> T
  let setter: (T) -> Void

  var value: T {
    get {
      return getter()
    }
    set {
      setter(newValue)
    }
  }

  subscript<U>(dynamicMember keyPath: WritableKeyPath<T, U>) -> Lens<U> {
    return Lens<U>(
        getter: { self.value[keyPath: keyPath] },
        setter: { self.value[keyPath: keyPath] = $0 })
  }
}

// https://github.com/apple/swift-evolution/blob/master/proposals/0242-default-values-memberwise.md
struct Dog {
  var name = "Generic dog name"
  var age = 0

  // The synthesized memberwise initializer
  init(name: String = "Generic dog name", age: Int = 0) {}
}

let sparky = Dog(name: "Sparky") // Dog(name: "Sparky", age: 0)


// https://bugs.swift.org/browse/SR-7799
enum Foo { case zero, one }

let foo: Foo? = .zero

switch foo {
  case .zero: break
  case .one: break
  case .none: break
}
