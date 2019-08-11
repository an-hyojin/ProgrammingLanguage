package lexer;

class CharStream {
	private Character cache;
	private String read;
	private int index;

	static CharStream from(String aString){
		return new CharStream(aString);
	}

	CharStream(String aString) {
		this.read = aString;
		this.cache = null;
		this.index = 0;
	}

	Char nextChar() {
		if (cache != null) {
			char ch = cache;
			cache = null;
			return Char.of(ch);
		} else {
			if (index == read.length()) {
				return Char.end();
			}

			else {
				index++;
				return Char.of(read.charAt(index-1));
			}
		}
	
	}

	void pushBack(char ch) {
		cache = ch;
	}
}
