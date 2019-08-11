package node;

import java.util.HashMap;
import java.util.Map;

import lexer.TokenType;

public class FunctionNode implements ValueNode {
	// binaryOpNode클래스를 보고 참고해서 작성
	public enum FunctionType {
		ATOM_Q {
			TokenType tokenType() {
				return TokenType.ATOM_Q;
			}
		},
		CAR {
			TokenType tokenType() {
				return TokenType.CAR;
			}
		},
		CDR {
			TokenType tokenType() {
				return TokenType.CDR;
			}
		},
		COND {
			TokenType tokenType() {
				return TokenType.COND;
			}
		},
		CONS {
			TokenType tokenType() {
				return TokenType.CONS;
			}
		},
		DEFINE {
			TokenType tokenType() {
				return TokenType.DEFINE;
			}
		},
		EQ_Q {
			TokenType tokenType() {
				return TokenType.EQ_Q;
			}
		},
		NOT {
			TokenType tokenType() {
				return TokenType.NOT;
			}
		},
		NULL_Q {
			TokenType tokenType() {
				return TokenType.NULL_Q;
			}
		},
		LAMBDA {
			TokenType tokenType() {
				return TokenType.LAMBDA;
			}
		};
		private static Map<TokenType, FunctionType> fromTokenType = new HashMap<TokenType, FunctionType>();

		static {
			for (FunctionType fType : FunctionType.values()) {
				fromTokenType.put(fType.tokenType(), fType);
			}
		}

		static FunctionType getFunctionType(TokenType tType) {
			return fromTokenType.get(tType);
		}

		abstract TokenType tokenType();

	}

	public FunctionType funcType;

	@Override
	public String toString() {
		// 내용 채우기
		return funcType.name();
	}

	public void setValue(TokenType tType) {
		// 내용 채우기
		FunctionType fType = FunctionType.getFunctionType(tType);
		funcType = fType;
	}
}
