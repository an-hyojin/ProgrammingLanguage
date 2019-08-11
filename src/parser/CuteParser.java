package parser;


import java.util.Iterator;
import lexer.Scanner;
import lexer.Token;
import lexer.TokenType;
import node.BinaryOpNode;
import node.BooleanNode;
import node.FunctionNode;
import node.IdNode;
import node.IntNode;
import node.ListNode;
import node.Node;
import node.QuoteNode;

public class CuteParser {
	private Iterator<Token> tokens;
	private static Node END_OF_LIST = new Node() {
	}; // 새로 추가된 부분

	public CuteParser(String aString) {
		tokens = Scanner.scan(aString);
	}

	private Token getNextToken() {
		if (!tokens.hasNext())
			return null;
		return tokens.next();

	}

	public Node parseExpr() {
		Token t = getNextToken();
		if (t == null) {
			System.out.println("No more token");
			return null;

		}
		TokenType tType = t.type();
		String tLexeme = t.lexme();
		switch (tType) {
		case ID:
			return new IdNode(tLexeme);
		case INT:
			if (tLexeme == null)
				System.out.println("???");
			return new IntNode(tLexeme); // 새로 구현된 BooleanNode Case
		case DIV:
		case EQ:
		case MINUS:
		case GT:
		case PLUS:
		case TIMES:
		case LT:
			BinaryOpNode binaryOpNode = new BinaryOpNode();
			binaryOpNode.setValue(tType);
			;
			return binaryOpNode;
		// 내용 채우기
		// FunctionNode에 대하여 작성
		// 키워드가 FunctionNode에 해당
		case ATOM_Q:
		case CAR:
		case CDR:
		case COND:
		case CONS:
		case DEFINE:
		case EQ_Q:
		case LAMBDA:
		case NOT:
		case NULL_Q:
			FunctionNode functionNode = new FunctionNode();
			functionNode.setValue(tType);
			;
			return functionNode;
		case FALSE:
			return BooleanNode.FALSE_NODE;
		case TRUE:
			return BooleanNode.TRUE_NODE; // 새로 구현된 L_PAREN, R_PAREN Case
		case L_PAREN:
			return parseExprList();
		case R_PAREN:
			return END_OF_LIST; // 새로 추가된 APOSTROPHE, QUOTE
		case APOSTROPHE:
			QuoteNode quoteNode = new QuoteNode(parseExpr());
			ListNode listNode = ListNode.cons(quoteNode, ListNode.EMPTYLIST);
			return listNode;
		case QUOTE:
			return new QuoteNode(parseExpr());
		default:
			System.out.println("Parsing Error!");
			return null;
		}
	}

	private ListNode parseExprList() {
		Node head = parseExpr();
		if (head == null)
			return null;
		if (head == END_OF_LIST) // if next token is RPAREN
			return ListNode.EMPTYLIST;
		ListNode tail = parseExprList();
		if (tail == null)
			return null;
		return ListNode.cons(head, tail);
	}
}