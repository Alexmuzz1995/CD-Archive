public class BinaryTree {

	BtNode root;
	String list;

	public void addNode(int key, String name) {

		// Create a new Node and initialize it

		BtNode newBtNode = new BtNode(key, name);

		// If there is no root this becomes root

		if (root == null) {

			root = newBtNode;

		} else {

			// Set root as the Node we will start
			// with as we traverse the tree

			BtNode focusBtNode = root;

			// Future parent for our new Node

			BtNode parent;

			while (true) {

				// root is the top parent so we start
				// there

				parent = focusBtNode;

				// Check if the new node should go on
				// the left side of the parent node

				if (key < focusBtNode.key) {

					// Switch focus to the left child

					focusBtNode = focusBtNode.leftChild;

					// If the left child has no children

					if (focusBtNode == null) {

						// then place the new node on the left of it

						parent.leftChild = newBtNode;
						return; // All Done

					}

				} else { // If we get here put the node on the right

					focusBtNode = focusBtNode.rightChild;

					// If the right child has no children

					if (focusBtNode == null) {

						// then place the new node on the right of it

						parent.rightChild = newBtNode;
						return; // All Done

					}

				}

			}
		}

	}

	// All nodes are visited in ascending order
	// Recursion is used to go to one node and
	// then go to its child nodes and so forth

	public void inOrderTraverseTree(BtNode focusBtNode) {

		if (focusBtNode != null) {

			// Traverse the left node

			inOrderTraverseTree(focusBtNode.leftChild);

			// Visit the currently focused on node

			//System.out.println(focusBtNode);
			list = list + focusBtNode.toString();

			// Traverse the right node

			inOrderTraverseTree(focusBtNode.rightChild);


		}

	}

	public void preorderTraverseTree(BtNode focusBtNode) {

		if (focusBtNode != null) {

			//System.out.println(focusBtNode);
			list = list + focusBtNode.toString();

			preorderTraverseTree(focusBtNode.leftChild);
			preorderTraverseTree(focusBtNode.rightChild);

		}

	}

	public void postOrderTraverseTree(BtNode focusBtNode) {

		if (focusBtNode != null) {

			postOrderTraverseTree(focusBtNode.leftChild);
			postOrderTraverseTree(focusBtNode.rightChild);

			//System.out.println(focusBtNode);
			list = list + focusBtNode.toString();

		}

	}

	public String nodeList(){
		String nodeList = list;
		list = "";
		return nodeList;
	}

	public BtNode findNode(int key) {

		// Start at the top of the tree

		BtNode focusBtNode = root;

		// While we haven't found the Node
		// keep looking

		while (focusBtNode.key != key) {

			// If we should search to the left

			if (key < focusBtNode.key) {

				// Shift the focus Node to the left child

				focusBtNode = focusBtNode.leftChild;

			} else {

				// Shift the focus Node to the right child

				focusBtNode = focusBtNode.rightChild;

			}

			// The node wasn't found

			if (focusBtNode == null)
				return null;

		}

		return focusBtNode;

	}

	class BtNode {

		int key;
		String name;

		BtNode leftChild;
		BtNode rightChild;

		BtNode(int key, String name) {

			this.key = key;
			this.name = name;

		}

		public String toString() {

			return name + "   ---   " + key + "\n";
		}

	}
}
