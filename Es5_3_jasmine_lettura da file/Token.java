public class Token {
	public final int tag;
	public final String lexeme;
    
	public Token(int t) {
		tag = t;
		lexeme = null;
	}
	public Token(int t, String s) {
		tag = t;
		lexeme = s;
	}
	public String toString() {
		return "<" + tag + ">";
	}
	
	public static final Token
    comma = new Token(','),
    colon = new Token(':'),
    semicolon = new Token(';'),
    lpar = new Token('('),
    rpar = new Token(')'),
    plus = new Token('+'),
    minus = new Token('-'),
    mult = new Token('*'),
    div = new Token('/'),
    lt = new Token('<'),
    gt = new Token('>');
    
}