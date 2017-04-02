// Copied from https://github.com/zeebo404/btree
package net.sourceforge.pmd.cpd
/**
 * User: Eric
 * Date: 4/30/2015
 */
class BTree<K, V> extends BTreeNode<K> {

	static def instance

	BlockManager<V> manager

	BTree() {
		instance = this
		getLeaf(this)
		manager = new BlockManager<>()
	}

	def split() {

		// create two new children
		BTreeNode<K> left = clone()
		BTreeNode<K> right = clone()

		// assign parent to this
		[left, right]*.parent = this

		// Assign the left and right pointer lists
		left.pointers = pointers.subList(0, count / 2 as int) as LinkedList
		right.pointers = pointers.subList(count / 2 as int, count) as LinkedList

		// clear the rightmost left key
		if (left.internalNode) {
			left.pointers[-1].key = null
		}
		else {
			left.rightSibling = right
			right.leftSibling = left
		}

		// reassign the parent node if not buckets
		if (!bucketNode) {
			[left, right].each { node -> node.pointers*.value*.parent = node }
		}

		// add the children to this
		pointers.clear()
		addDirect(new BTreeEntry(right.smallestKey, left))
		addDirect(new BTreeEntry(null, right))

		// Transform into a pointer node
		if (leafNode) {
			getPointer(this)
		}
	}

	def add(K key, V value) {

		if (count > 0 && search(key)) {
			throw new IllegalArgumentException("$key is already in the tree")
		}

		BlockManager.Block.BlockElement<V> element = manager.element
		element.value = value

		super.add key, element
	}

	def delete(K key) {

		if (count > 0 && !search(key)) {
			throw new IllegalArgumentException("$key is not in the tree")
		}

		super.delete key

		if (count == 0) {
			getLeaf(this)
		}
	}
}
