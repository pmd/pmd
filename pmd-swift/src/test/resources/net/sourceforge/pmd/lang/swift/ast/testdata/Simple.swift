/// B-trees are search trees that provide an ordered key-value store with excellent performance characteristics.
public struct BTree<Key: Comparable, Payload> {
    public typealias Element = (Key, Payload)
    internal typealias Node = BTreeNode<Key, Payload>

    internal var root: Node

    internal init(_ root: Node) {
        self.root = root
    }

    /// Initialize a new b-tree with no elements.
    ///
    /// - Parameter order: The maximum number of children for tree nodes.
    public init(order: Int = Node.defaultOrder) {
        self.root = Node(order: order)
    }

    /// The order of this tree, i.e., the maximum number of children for tree nodes.
    public var order: Int { return root.order }
    /// The depth of this tree. Depth starts at 0 for a tree that has a single root node.
    public var depth: Int { return root.depth }
}
