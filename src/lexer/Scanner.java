package lexer;



import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Scanner {
    // return tokens as an Iterator
    public static Iterator<Token> scan(String aString){
        ScanContext context = new ScanContext(aString);
        return new TokenIterator(context);
    }

    // return tokens as a Stream 
    public static Stream<Token> stream(String aString) {
        Iterator<Token> tokens = scan(aString);
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(tokens, Spliterator.ORDERED), false);
    }
}