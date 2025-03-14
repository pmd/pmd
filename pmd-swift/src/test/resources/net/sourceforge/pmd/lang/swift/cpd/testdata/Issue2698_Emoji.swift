// see https://github.com/pmd/pmd/issues/2698

if let userToken = userToken {
     print("👤 | User > Token: \(userToken)")
}

var baseCommand = #"curl "\#(url.absoluteURL)""#

