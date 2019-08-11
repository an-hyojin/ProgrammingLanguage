package lexer;




class ScanContext {
	private final CharStream input;
	private StringBuilder builder;
	
	ScanContext(String aString) {
		this.input = CharStream.from(aString);
		this.builder = new StringBuilder();
	}
	
	CharStream getCharStream() {
		return input;
	}
	
	String getLexime() {
		String str = builder.toString();
		builder.setLength(0);
		return str;
	}
	
	void append(char ch) {
		builder.append(ch);
	}
}
