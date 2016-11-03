public class Number extends Token {
	/* classe number: è una sottoclasse di token, la quale, 
	oltre che ereditare il parametro tag da token, aggiunge un ulteriore parametro lexeme*/
	
	public String lexeme = "";
	
	public Number(int tag, String s) {  /*costruttore di Number: richiama il costruttore in Token con la chiamata super() e inizializza in nuovo campo lexeme*/
		super(tag, s); 
		lexeme = s; 
	}
	public String toString() { 
		return "<" + tag + ", " + lexeme + ">";
	}
}
	