public class Token {
	public final int tag;		/*campi di token: è stato aggiunto un campo lexeme per facilitre la lettura dei Number*/
	public final String lexeme;
	
	public Token(int t) {	/*costruttori*/
		tag = t;
		lexeme = null;
	}
	public Token(int t, String s) {	/*questo costruttore viene chiamato solo dal costruttore della classe Number ed inizializza i campi di Token*/
		tag = t;
		lexeme = s;
	}
	public String toString() {
		return "<" + tag + ">";
	}
	/* elenco di Token riservati: rappresentano gli operatori unari. Durante la traduzione da parte del Lexer, ogni volta
	che si incontra uno dei seguenti token viene ritornato il valore intero corrispondente alla codifica ASCII  relativa 
	a quel simbolo secifico....sono delle costanti di tipo static, quindi costanti della classe Token, public. Questo fa in modo che da 
	una qualsiasi altra classe si possa accedere alle costanti*/
	
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