// file for supporting swift 5.6 changes : https://github.com/apple/swift/blob/master/CHANGELOG.md#swift-56

// https://github.com/apple/swift-evolution/blob/main/proposals/0315-placeholder-types.md
let dict: [_: String] = [0: "zero", 1: "one", 2: "two"]

// https://github.com/apple/swift-evolution/blob/main/proposals/0290-negative-availability.md
if #unavailable(iOS 15.0) {
    // Old functionality
} else {
    // iOS 15 functionality
}


