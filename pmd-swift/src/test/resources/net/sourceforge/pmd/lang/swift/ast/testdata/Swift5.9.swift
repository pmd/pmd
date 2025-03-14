import SwiftUI

struct ContentView: View {
    var body: some View {
        TabBarView()
    }
}

#Preview {
    ContentView()
}

// Macro Expansion: https://github.com/apple/swift-evolution/blob/main/proposals/0382-expression-macros.md#macro-expansion
let _: Font = #fontLiteral(name: "SF Mono", size: 14, weight: .regular)

// Parameter packs
func all<each Wrapped>(_ optional: repeat (each Wrapped)?) -> (repeat each Wrapped)?

func useAll() {
    if let (int, double, string, bool) = all(optionalInt, optionalDouble, optionalString, optionalBool) {
        print(int, double, string, bool)
    }
    else {
        print("got a nil")
    }
}

// return value on if/else if/else
statusBar.text = if !hasConnection { "Disconnected" }
                 else if let error = lastError { error.localizedDescription }
                 else { "Ready" }

// https://docs.swift.org/swift-book/documentation/the-swift-programming-language/macros/
@attached(member)
@attached(conformance)
public macro OptionSet<RawType>() = #externalMacro(module: "SwiftMacros", type: "OptionSetMacro")

import SwiftUI

#Preview {
    Text()
    .padding()
}
