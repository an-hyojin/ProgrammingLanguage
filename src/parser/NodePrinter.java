package parser;


import node.ListNode;
import node.Node;
import node.QuoteNode;

public class NodePrinter {
	private StringBuffer sb = new StringBuffer();
	private Node root;

	public NodePrinter(Node root) {
		this.root = root;
	}

// ListNode, QuoteNode, Node�� ���� printNode �Լ��� ���� overload �������� �ۼ�
	private void printList(ListNode listNode) {
		if (listNode == ListNode.EMPTYLIST) {
			return;
		}
		printNode(listNode.car());
		printList(listNode.cdr());

		// ���� �κ��� �־��� ��� ���Ŀ� �°� �ڵ带 �ۼ��Ͻÿ�.
	}

	private void printNode(QuoteNode quoteNode) {
		if (quoteNode.nodeInside() == null)
			return; // ���� �κ��� �־��� ��� ���Ŀ� �°� �ڵ带 �ۼ��Ͻÿ�.
		sb.append("\' ");
		printNode(quoteNode.nodeInside());

	}

	private void printNode(Node node) {
		if (node == null)
			return; // ���� �κ��� �־��� ��� ���Ŀ� �°� �ڵ带 �ۼ��Ͻÿ�.
		if (node instanceof ListNode) {
			ListNode li = (ListNode) node;
			if (li.car() instanceof QuoteNode) {
				QuoteNode qu = (QuoteNode) li.car();
				printNode(qu);
				printList(li.cdr());
			} else {
				sb.append("( ");
				printList(li);
				sb.append(")");
			}
		}else if (node instanceof QuoteNode) {
			printNode((QuoteNode)node);
		}
		else {
			sb.append(node);
			sb.append(" ");
		}
	}

	public void prettyPrint() {
		printNode(root);
		System.out.println(sb.toString());
		
	}
}