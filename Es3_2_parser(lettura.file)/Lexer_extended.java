import java.io.*;
import java.util.*;
public class Lexer_extended {
	public static int line = 1;	/* campo che rappresenta la linea corrente che viene inizializzato a 1 */
	private char peek = ' ';			/* rappresenta il carattere correte inizializzato a ' ' */
	
	private void readch(BufferedReader br) { /*il metodo readch legge dal buffer br invece che da stdin */
		try {
			peek = (char) br.read(); 
		} 
		catch (IOException exc) {	/*Exception Handler: gestisce un errore di I/O*/
			peek = (char) -1; 
		}
	}

	public Token lexical_scan(BufferedReader br) {/* metodo che esegue la traduzione in token del testo  */ 
		/* se durante la traduzione vengono incontrati spazi, new line, ecc vengono ingorati e viene letto il carattere successivo*/
		while (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
			if (peek == '\n') line++;
			readch(br);
		}
		switch (peek) {/* gestione della casistica del carattere peek in imput*/
			/* casi costituiti da un carattere solo */
			case '(':
				peek = ' ';
				return Token.lpar;
				
			case ')':
				peek = ' ';
				return Token.rpar;
				
			case '+':
				peek = ' ';
				return Token.plus;
				
			case '-':
				peek = ' ';
				return Token.minus;
			
			case '*':
				peek = ' ';
				return Token.mult;
				
			case '/':
				peek = ' ';
				return Token.div;
				
			default:		//se non sono nessuno di quelli gestiti prima 
				
					if (Character.isDigit(peek)) {
						String s = "";
						do {
						s+= peek;
						readch(br);
						} while (Character.isDigit(peek));
					
					Number NUM = new Number(Tag.NUM, s);
					return NUM;
					} 
				
					//se siamo arrivati alla fine del programma e abbiamo trovato $ 					
					if (peek == '$') {
						return new Token(Tag.EOF);
					}
					else {	//se viene inserito qualsiasi altro carattere che non fa parte della grammatica
						System.err.println("Erroneous character: "+ peek );
						return null;
					}
		}
	}



	public static void main(String[] args) {
		Lexer_extended lex = new Lexer_extended();
		String path = "testo.txt";	 /* il path del file in cui è presente il testo da analizzare*/
		System.out.println("Testo preso in esame: " + path); 
		try {
			BufferedReader br = new BufferedReader(new FileReader(path)); 	/*crea un nuovo buffer br e ci inserisce il testo del file specificato dal path*/
			Token tok;
	
			do {
				tok = lex.lexical_scan(br);
				System.out.println("Scan: " + tok); 
			} while (tok.tag != Tag.EOF);	/*continua la lettura dei token generati fino al EOF*/
			br.close();	/*chiude il buffer br*/
		} 
		catch (IOException e) {	/*Exception Handler: gestisce un eventuale errore di I/0*/
			e.printStackTrace();
		} 
	}
}