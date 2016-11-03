public class Word extends Token {	
/* classe word: è una sottoclasse di token, la quale, 
oltre che ereditare il parametro tag da token, aggiunge un ulteriore parametro lexeme*/
	
	public String lexeme = "";	/* lesema assegnato alla parola*/

	public Word(int tag, String s) { /*costruttore di Word: richiama il costruttore in Token con la chiamata super() e inizializza in nuovo campo lexeme*/
		super(tag,s); 
		lexeme = s; 
	}
	public String toString() { 	
		return "<" + tag + ", " + lexeme + ">";
	}
	/* come per la classe Token, vengono riservate alcune parole per la trsduzione.
		Nell' invocazione viene specificato il tag corrispondente e il lexema */
	public static final Word	
		and = new Word(Tag.AND, "&&"),
		or = new Word(Tag.OR, "||"),
		eq = new Word(Tag.EQ, "=="),
		le = new Word(Tag.LE, "<="),
		ne = new Word(Tag.NE, "<>"),
		ge = new Word(Tag.GE, ">="),
		assign = new Word(Tag.ASSIGN, ":=");
}