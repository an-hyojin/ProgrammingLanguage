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

// ListNode, QuoteNode, Node에 대한 printNode 함수를 각각 overload 형식으로 작성
	private void printList(ListNode listNode) {
		if (listNode == ListNode.EMPTYLIST) {
			return;
		}
		printNode(listNode.car());
		printList(listNode.cdr());

		// 이후 부분을 주어진 출력 형식에 맞게 코드를 작성하시오.
	}

	private void printNode(QuoteNode quoteNode) {
		if (quoteNode.nodeInside() == null)
			return; // 이후 부분을 주어진 출력 형식에 맞게 코드를 작성하시오.
		sb.append("\' ");
		printNode(quoteNode.nodeInside());

	}

	private void printNode(Node node) {
		if (node == null)
			return; // 이후 부분을 주어진 출력 형식에 맞게 코드를 작성하시오.
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