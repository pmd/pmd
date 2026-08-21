package nl.stokpop.kotlin

// DelegationSpecifier typeIs test: class extending a JDK type.
// typeIs('java.lang.Throwable') on the DelegationSpecifier node must match via subtype hierarchy.
class ServiceException : RuntimeException("service error")

// Direct match: typeIs('java.lang.RuntimeException') should also match.
class AnotherException : RuntimeException()
