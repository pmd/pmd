// Samples from https://github.com/apple/swift-evolution/blob/main/proposals/0382-expression-macros.md#macro-expansion

// macro expansion expression with one argument
let _: (Int, String) = #addBlocker(x + y * z)

// macro expansion expressions within macro arguments
let _: (Int, String) = #addBlocker(#stringify(1 + 2))

// default built-in macros
let _: String = #fileID
let _: Int = #line
