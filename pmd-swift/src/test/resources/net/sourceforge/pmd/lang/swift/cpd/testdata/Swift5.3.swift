// file for supporting swift 5.3 changes : https://github.com/apple/swift/blob/master/CHANGELOG.md#swift-53

// https://github.com/apple/swift/issues/54108
struct Container {
  static let defaultKey = 0

  var dictionary = [defaultKey:0]

  mutating func incrementValue(at key: Int) {
    let defaultValue = dictionary[Container.defaultKey]!
    dictionary[key, default: defaultValue] += 1
  }
}
