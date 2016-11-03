public class Number extends Token {
	public String lexeme = "";
	
	public Number(int tag, String s) { 
		super(tag, s);
		lexeme = s;
	}
	public String toString() { 
		return "<" + tag + ", " + lexeme + ">";
	}
	
}