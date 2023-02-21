// file for supporting swift 5.5 changes : https://github.com/apple/swift/blob/master/CHANGELOG.md#swift-55

// https://github.com/apple/swift-evolution/blob/main/proposals/0313-actor-isolation-control.md
actor MyActor {
  func f() { }
}

func g(actor: isolated MyActor) {
  actor.f()   // okay, this code is always executing on "actor"
}

func h(actor: MyActor) async {
  g(actor: actor)        // error, call must be asynchronous
  await g(actor: actor)  // okay, hops to "actor" before calling g
}

// https://github.com/apple/swift/issues/57081
struct Box<T> {
  // previously interpreted as a return type of Box<T>, ignoring the <Int> part;
  // now we diagnose an error with a fix-it suggesting replacing `Self` with `Box`
  static func makeBox() -> Self<Int> {...}
}

// https://github.com/apple/swift-evolution/blob/main/proposals/0311-task-locals.md
struct TraceID {
  @TaskLocal
  static var current: TraceID?
}

func printTraceID() {
  if let traceID = TraceID.current {
    print("\(traceID)")
  } else {
    print("nil")
  }
}

func run() async {
  printTraceID()    // prints: nil
  TraceID.$current.withValue("1234-5678") {
    printTraceID()  // prints: 1234-5678
    inner()         // prints: 1234-5678
  }
  printTraceID()    // prints: nil
}

func inner() {
  // if called from a context in which the task-local value
  // was bound, it will print it (or 'nil' otherwise)
  printTraceID()
}

// https://github.com/apple/swift-evolution/blob/main/proposals/0316-global-actors.md

@globalActor
struct DatabaseActor {
  actor ActorType { }

  static let shared: ActorType = ActorType()
}

@DatabaseActor func queryDB(query: Query) throws -> QueryResult

func runQuery(queryString: String) async throws -> QueryResult {
  let query = try Query(parsing: queryString)
  return try await queryDB(query: query) // 'await' because this implicitly hops to DatabaseActor.shared
}

// https://github.com/apple/swift-evolution/blob/main/proposals/0313-actor-isolation-control.md
actor Account: Hashable {
  let idNumber: Int
  var balance: Double

  nonisolated func hash(into hasher: inout Hasher) { // okay, non-isolated satisfies synchronous requirement
    hasher.combine(idNumber) // okay, can reference idNumber from outside the let
    hasher.combine(balance) // error: cannot synchronously access actor-isolated property
  }
}

// https://github.com/apple/swift-evolution/blob/main/proposals/0300-continuation.md
struct MyValue {
}

struct MyStruct {
  subscript(a: MyValue.Type) -> Int { get { ... } }
}

func test(obj: MyStruct) {
  let _ = obj[MyValue]
}

// https://github.com/apple/swift-evolution/blob/main/proposals/0310-effectful-readonly-properties.md
class BankAccount: FinancialAccount {
  var manager: AccountManager?

  var lastTransaction: Transaction {
    get async throws {
      guard manager != nil else { throw BankError.notInYourFavor }
      return await manager!.getLastTransaction()
    }
  }

  subscript(_ day: Date) -> [Transaction] {
    get async {
      return await manager?.getTransactions(onDay: day) ?? []
    }
  }
}

protocol FinancialAccount {
  associatedtype T
  var lastTransaction: T { get async throws }
  subscript(_ day: Date) -> [T] { get async }
}

extension BankAccount {
  func meetsTransactionLimit(_ limit: Amount) async -> Bool {
    return try! await self.lastTransaction.amount < limit
    //                    ^~~~~~~~~~~~~~~~ this access is async & throws
  }                
}

  
func hadWithdrawalOn(_ day: Date, from acct: BankAccount) async -> Bool {
  return await !acct[day].allSatisfy { $0.amount >= Amount.zero }
  //            ^~~~~~~~~ this access is async
}

// https://github.com/apple/swift-evolution/blob/main/proposals/0306-actors.md

actor Counter {
  var value = 0

  func increment() {
    value = value + 1
  }
}

func useCounter(counter: Counter) async {
  print(await counter.value) // interaction must be async
  await counter.increment()  // interaction must be async
}
