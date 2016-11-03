public class Token {
	public final int tag;	/* campo intero di token "tag" */
	
	public Token(int t) {	/* costruttore della classe Token: inizializza tag */
		tag = t;
	}
	public String toString() {	/* metodo toString per l'otput */
		return "<" + tag + ">";
	}
	
	/* elenco di Token riservati: rappresentano gli operatori unari. Durante la traduzione da parte del Lexer, ogni volta
	che si incontra uno dei seguenti token viene ritornato il valore intero corrispondente alla codifica ASCII  relativa 
	a quel simbolo secifico....sono delle costanti di tipo static, quindi della classe Token, public. Questo fa in modo che da 
	una qualsiasi altra classe si possa accedere ai campi delle costanti */
	
	public static final Token
	
	lpar = new Token('('),
	rpar = new Token(')'),
	plus = new Token('+'),
	minus = new Token('-'),
	mult = new Token('*'),
	div = new Token('/');
	
}