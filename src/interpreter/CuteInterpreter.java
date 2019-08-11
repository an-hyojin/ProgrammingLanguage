package interpreter;

import node.BinaryOpNode;
import node.BooleanNode;
import node.FunctionNode;
import node.IdNode;
import node.IntNode;
import node.ListNode;
import node.Node;
import node.QuoteNode;
import parser.CuteParser;
import parser.NodePrinter;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CuteInterpreter {

	private Map<Key, Node> table = new HashMap<>();
	private int depth = 0;
	private Map<Integer, String> removeFunctionName = new HashMap<>();
	
	class Key {
		private String string;
		private int depth;

		public Key(String string, int depth) {
			this.string = string;
			this.depth = depth;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Key)) {
				return false;
			} else {
				Key temp = (Key) o;
				if (temp.string.equals(this.string)) {
					if (temp.depth == this.depth) {
						return true;
					}
				}
				return false;
			}
		}

		@Override
		public int hashCode() {
			int result = this.string.hashCode();
			result = result * 31 + Integer.hashCode(this.depth);
			return result;
		}
	}

	public static void main(String[] args) {
		Scanner aScanner = new Scanner(System.in);
		CuteInterpreter interpreter = new CuteInterpreter();
		while (true) {
			System.out.print(">   ");
			String aString = aScanner.nextLine();
			CuteParser cuteParser = new CuteParser(aString);
			Node parseTree = cuteParser.parseExpr();
			Node resultNode = interpreter.runExpr(parseTree);
			if (!(resultNode == null)) {
				NodePrinter nodePrinter = new NodePrinter(resultNode);
				System.out.print("...");
				nodePrinter.prettyPrint();
			}
		}
	}

	private void errorLog(String err) {
		System.out.println(err);
	}

	public Node runExpr(Node rootExpr) {
		if (rootExpr == null)
			return null;
		if (rootExpr instanceof IdNode) {
			if (lookupTable(((IdNode) rootExpr).toString()) != null) {
				return lookupTable(((IdNode) rootExpr).toString());
			}
			return rootExpr;
		} else if (rootExpr instanceof IntNode)
			return rootExpr;
		else if (rootExpr instanceof BooleanNode)
			return rootExpr;
		else if (rootExpr instanceof ListNode)
			return runList((ListNode) rootExpr);
		else
			errorLog("run Expr error");
		return null;
	}

	private Node runList(ListNode list) {
		Node result;
		if (list.equals(ListNode.EMPTYLIST))
			return list;
		if (list.car() instanceof FunctionNode) {
			return runFunction((FunctionNode) list.car(), (ListNode) stripList(list.cdr()));
		}
		if (list.car() instanceof BinaryOpNode) {
			return runBinary(list);
		}
		if (list.car() instanceof IdNode) {
			result = lookupTable(((IdNode) list.car()).toString());
		} else if (list.car() instanceof ListNode) {
			result = runExpr(list.car());
		} else {
			return list;
		}
		if (result instanceof ListNode) {
			depth += 1;
			ListNode solve = (ListNode) result;
			ListNode para = (ListNode) solve.cdr().car();
			ListNode input = (ListNode)list.cdr();
			inputRecursion(para, input);
			Node nodetoReturn = runExpr(stripList(solve.cdr().cdr()));
			removeRecursion(para);
			depth -= 1;
			return nodetoReturn;
		} else if(result == null){
			return runExpr(stripList(list.cdr()));
	
		}else {
			return runExpr(ListNode.cons(result, list.cdr()));
		}
	}

	private Node runFunction(FunctionNode operator, ListNode operand) {
		switch (operator.funcType) {
		// CAR, CDR, CONS등에 대한 동작 구현
		case CAR:
			ListNode listNode;
			QuoteNode quoteNode;
			if (operand.car() instanceof IdNode) {
				listNode = (ListNode) lookupTable(((IdNode) operand.car()).toString());
			} else if (operand.car() instanceof QuoteNode) {
				listNode = operand;
			} else {
				listNode = (ListNode) runExpr(operand);
			}
			listNode = (ListNode) runQuote(listNode);
			if (listNode.car() instanceof IntNode || listNode.car() instanceof BooleanNode) {
				return listNode.car();
			} else {// intNode나 booleanNode이외의 것은 quote로 감싼후 ListNode로 리턴해야함
				quoteNode = new QuoteNode(listNode.car());
				return ListNode.cons(quoteNode, ListNode.EMPTYLIST);
			}
		case CDR:
			if (operand.car() instanceof IdNode) {
				listNode = (ListNode) lookupTable(((IdNode) operand.car()).toString());
			} else if (operand.car() instanceof QuoteNode) {
				listNode = operand;
			} else {
				listNode = (ListNode) runExpr(operand);
			}
			listNode = (ListNode) runQuote(listNode);
			quoteNode = new QuoteNode(listNode.cdr());// cdr은 리스트이므로 항상 ' 를 붙여서 반환
			return ListNode.cons(quoteNode, ListNode.EMPTYLIST);
		case CONS:
			Node car;
			ListNode cdr;
			Node temp0;
			ListNode temp;
			if (operand.car() instanceof IdNode) {
				temp0 = lookupTable(((IdNode) operand.car()).toString());
			} else if (operand.car() instanceof ListNode) {
				temp0 = stripList((ListNode) operand.car());
				temp0 = runExpr(temp0);
			} else {
				temp0 = operand.car();
			}
			if (temp0 instanceof ListNode) {// car가 리스트 노드일경우
				car = runQuote((ListNode) temp0);
			} else {// 리스트 노드가 아니라면
				car = temp0;// car 넣어줌
			}
			if (operand.cdr().car() instanceof IdNode) {
				temp = (ListNode) lookupTable(((IdNode) operand.cdr().car()).toString());
			} else if (operand.cdr().car() instanceof ListNode) {
				temp0 = stripList((ListNode) operand.cdr().car());
				temp = (ListNode) runExpr(temp0);
			} else {
				temp = (ListNode) stripList(operand.cdr());// cons는 앞 뒤 두개로 옮
			}
			cdr = (ListNode) runQuote(temp);
			quoteNode = new QuoteNode(ListNode.cons(car, cdr));// car, cdr합쳐서 Quote에 넣음
			listNode = ListNode.cons(quoteNode, ListNode.EMPTYLIST);// 반환
			return listNode;
		case NOT:
			BooleanNode result;
			if (operand.car() instanceof BooleanNode) {// BooleanNode라면
				result = (BooleanNode) operand.car();// 바로 받음
			} else if (operand.car() instanceof IdNode) {
				result = (BooleanNode) lookupTable(((IdNode) operand.car()).toString());
			} else {// BooleanNode가 아니라면 계산이 필요
				result = (BooleanNode) runExpr(operand);
			}
			if (result == BooleanNode.TRUE_NODE)
				return BooleanNode.FALSE_NODE;
			return BooleanNode.TRUE_NODE;
		case COND:
			// 단일 조건문일때와 복합조건문일때 operand.car의 형식이 다름
			ListNode returnNode;
			if (operand.car() instanceof IdNode || operand.car() instanceof BooleanNode) {
				if (operand.car() instanceof IdNode) {
					result = (BooleanNode) lookupTable(((IdNode) operand.car()).toString());
				} else {
					result = (BooleanNode) operand.car();
				}
				returnNode = operand.cdr();
			} else {
				ListNode parseNode = (ListNode) operand.car();// 현재 확인해야할 리스트
				returnNode = parseNode.cdr();
				if (parseNode.car() instanceof ListNode || parseNode.car() instanceof BooleanNode) {
					result = (BooleanNode) runExpr(parseNode.car());// 결과값은 boolean
				} else if (parseNode.car() instanceof IdNode) {
					result = (BooleanNode) lookupTable(((IdNode) parseNode.car()).toString());
				} else {// 단일 조건문인경우
					result = (BooleanNode) runExpr(parseNode);
					returnNode = operand.cdr();
				}
			}
			if (result == BooleanNode.TRUE_NODE) {
				if (returnNode.car() instanceof IdNode) {
					return lookupTable(((IdNode) returnNode.car()).toString());
				}
				if (returnNode.car() instanceof ListNode) {
					return runExpr(stripList((ListNode)returnNode.car()));
				} else {
					return returnNode.car();
				}

			} else {
				return runFunction(operator, operand.cdr());
			}

		case ATOM_Q:
			if (operand.car() instanceof IdNode) {
				temp = (ListNode) lookupTable(((IdNode) operand.car()).toString());
			} else if (operand.car() instanceof QuoteNode) {// 뒤에 확인할 것이 ' 일때
				temp = operand;
			} else {// ' 가아니라 계산해야할 때
				Node atom = runExpr(operand);
				if (atom instanceof ListNode) {// 계산식의 결과가 리스트라면 ' 와 같이 묶여있을것 -> 뒤에서 확인해야함
					temp = (ListNode) atom;
				} else {// 리스트가 아니라면 atom이기 떄문에
					return BooleanNode.TRUE_NODE;
				}
			}
			if (runQuote(temp) instanceof ListNode) {// '를 벗겨냈을때, 리스트라면
				if ((ListNode) runQuote(temp) == ListNode.EMPTYLIST) {// 빈리스트라면
					return BooleanNode.TRUE_NODE;
				}
				return BooleanNode.FALSE_NODE;
			} else {
				return BooleanNode.TRUE_NODE;
			}
		case NULL_Q:
			if (operand.car() instanceof IdNode) {
				temp = (ListNode) lookupTable(((IdNode) operand.car()).toString());
			} else if (operand.car() instanceof QuoteNode) {
				temp = operand;
			} else {
				Node nullQ = runExpr(operand);
				if (nullQ instanceof ListNode) {// 계산식의 결과가 리스트라면 뒤에서 확인해야함 비어있는 노드인지
					temp = (ListNode) nullQ;
				} else {// 리스트가 아니라면 값이있기 떄문에
					return BooleanNode.FALSE_NODE;
				}
			}
			if (runQuote(temp) instanceof ListNode) {// '를 벗겨냈을때, 리스트라면
				if ((ListNode) runQuote(temp) == ListNode.EMPTYLIST) {// 빈리스트라면
					return BooleanNode.TRUE_NODE;
				}
				return BooleanNode.FALSE_NODE;
			} else {
				return BooleanNode.FALSE_NODE;
			}
		case EQ_Q:
			Node firstNode, secondNode;
			if (operand.car() instanceof IdNode) {
				firstNode = lookupTable(((IdNode) operand.car()).toString());
				if (firstNode instanceof ListNode) {
					firstNode = runQuote((ListNode) firstNode);
				}
			} else {
				temp = (ListNode) stripList((ListNode) operand.car());
				if (temp.car() instanceof QuoteNode) {
					firstNode = runQuote(temp);
				} else {
					firstNode = runExpr(temp);
					if (firstNode instanceof ListNode) {// 이미 runExpr로 돌려줬기때문에 listNode라면 내부가 Quote로 묶여있을것
						firstNode = runQuote((ListNode) firstNode);
					}
				}
			}
			if (operand.cdr().car() instanceof IdNode) {
				secondNode = lookupTable(((IdNode) operand.cdr().car()).toString());
				if (secondNode instanceof ListNode) {
					secondNode = runQuote((ListNode) secondNode);
				}
			} else {
				temp = (ListNode) stripList((ListNode) operand.cdr().car());
				if (temp.car() instanceof QuoteNode) {
					secondNode = runQuote(temp);
				} else {
					secondNode = runExpr(temp);
					if (secondNode instanceof ListNode) {
						secondNode = runQuote((ListNode) secondNode);
					}
				}
			}
			if (firstNode.equals(secondNode)) {
				return BooleanNode.TRUE_NODE;
			}
			return BooleanNode.FALSE_NODE;
		case LAMBDA:
			return ListNode.cons(operator, operand);
		case DEFINE:
			IdNode a = (IdNode) operand.car();
			Node valueNode;
			// operand.cdr의 car만 있을때 valueNode로서
			// operand.cdr이 리스트 노드이고 그 car도 리스트 노드일때 -> runExpr
			temp = (ListNode) stripList(operand.cdr());
			valueNode = operand.cdr().car();
			if (valueNode instanceof ListNode) {
				temp = (ListNode) valueNode;
				if (temp.car() instanceof QuoteNode) {
					Node save = runQuote(temp);
					if (save instanceof IntNode || save instanceof BooleanNode) {// intNode나 booleanNode일땐 내부에 있는 값 저장해줌
						valueNode = save;
					}
				} else {
					valueNode = runExpr((ListNode) stripList(operand.cdr()));
				}
			}
			if(this.depth>0) {
				removeFunctionName.put(this.depth,a.toString());
			}
			insertTable(a.toString(), valueNode);
		default:
			break;
		}
		return null;

	}

	private void insertTable(String id, Node value) {
		Node putNode;
		if (value instanceof IdNode) {
			putNode = lookupTable(((IdNode) value).toString());
		} else {
			putNode = value;
		}
		Key key = new Key(id, this.depth);
		table.put(key, putNode);

	}

	private void removeTable(String id) {
		Key key = new Key(id, this.depth);
		table.remove(key);
	}

	private Node lookupTable(String id) {
		Key key = new Key(id, this.depth);
		if(table.get(key)!=null) {
			return table.get(key);
		}
		key = new Key(id, 0);
		return table.get(key);
	}

	private Node lookupTableWithDepth(String id, int Inputdepth) {
		Key key = new Key(id, Inputdepth);
		if(table.get(key)!=null) {
			return table.get(key);
		}
		key = new Key(id, 0);
		return table.get(key);
	}
	
	private void inputRecursion(ListNode para, ListNode input) {
		if (para != ListNode.EMPTYLIST) {
			IdNode formalParameter = (IdNode) para.car();
			Node actualParameter = input.car();
			if (actualParameter instanceof IdNode) {
				actualParameter = lookupTableWithDepth(((IdNode) actualParameter).toString(), this.depth-1);
			}
			insertTable(formalParameter.toString(), actualParameter);
			inputRecursion(para.cdr(), input.cdr());
		}
	}

	private void removeRecursion(ListNode para) {
		if (para != ListNode.EMPTYLIST) {
			IdNode remove = (IdNode) para.car();
			removeTable(remove.toString());
		}
	}

	private Node stripList(ListNode node) {
		if (node.car() instanceof ListNode && node.cdr() == ListNode.EMPTYLIST) {
			Node listNode = node.car();
			return listNode;
		} else {
			return node;
		}
	}

	 private Node runBinary(ListNode list) {
         BinaryOpNode operator = (BinaryOpNode) list.car();// car은 항상 BinaryOpNode(오류 없을 때)
         IntNode operand1;
         IntNode operand2;
         if (list.cdr().car() instanceof IdNode) {
            operand1 = (IntNode) lookupTable(((IdNode) list.cdr().car()).toString());
         }
         else if(list.cdr().car() instanceof ListNode) {
            if (((ListNode) list.cdr().car()).car() instanceof QuoteNode) {
               operand1 = (IntNode) runQuote(((ListNode) list.cdr().car()));
            } else {
               // 리스트인 경우
               operand1 = (IntNode) runExpr(list.cdr().car());
            }
         }
         else {
            operand1 = (IntNode) runExpr(list.cdr().car());
         }
         if (list.cdr().cdr().car() instanceof IdNode) {
            operand2 = (IntNode) lookupTable(((IdNode) list.cdr().cdr().car()).toString());
         }
         else if(list.cdr().cdr().car() instanceof ListNode) {
            if (((ListNode) list.cdr().cdr().car()).car() instanceof QuoteNode) {
               operand2 = (IntNode) runQuote(((ListNode) list.cdr().cdr().car()));
            } else {
               // 리스트인 경우
               operand2 = (IntNode) runExpr(list.cdr().cdr().car());
            }
         }
         else {
            operand2 = (IntNode) runExpr(list.cdr().cdr().car());
         }
         Integer value1 = operand1.getValue();
         Integer value2 = operand2.getValue();
         // 구현과정에서 필요한 변수 및 함수 작업 가능
         switch (operator.binType) {
         // +,-,/ 등에 대한 바이너리 연산 동작 구현
         case PLUS:
            return new IntNode(String.valueOf(value1 + value2));
         case MINUS:
            return new IntNode(String.valueOf(value1 - value2));
         case TIMES:
            return new IntNode(String.valueOf(value1 * value2));
         case DIV:
            return new IntNode(String.valueOf(value1 / value2));
         case LT:
            if (value1 < value2)
               return BooleanNode.TRUE_NODE;
            return BooleanNode.FALSE_NODE;
         case GT:
            if (value1 > value2)
               return BooleanNode.TRUE_NODE;
            return BooleanNode.FALSE_NODE;
         case EQ:
            if (operand1.equals(operand2))
               return BooleanNode.TRUE_NODE;
            return BooleanNode.FALSE_NODE;
         default:
            break;
         }
         return null;

      }


	private Node runQuote(ListNode node) {
		return ((QuoteNode) node.car()).nodeInside();
	}
}