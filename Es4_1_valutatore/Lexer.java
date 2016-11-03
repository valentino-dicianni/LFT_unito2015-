import java.io.*;
import java.util.*;
public class Lexer {
	public static int line = 1;	/* campo che rappresenta la linea corrente che viene inizializzato a 1 */
	private char peek = ' ';			/* rappresenta il carattere correte inizializzato a ' ' */
	

	
	private void readch() {		/* metodo che legge il CARATTERE SUCCESSIVO */
		try {
			peek = (char)System.in.read(); 	/* legge il carattere in imput: esegue un cast a char  perchè la System.in.read() restituirebbe un int */ 
		} 
		catch (IOException exc) {
			peek = (char) -1;  /* Exception handler:	La lettura non è andata a buon fine, fa il cast di -1 per poterlo scrivere in char peek. */
		}
	}
	
	public Token lexical_scan() {
		/* metodo che esegue la traduzione in token del testo in imput */ 
		/* se durante la traduzione vengono incontrati spazi, new line, ecc vengono ingorati e viene letto il carattere successivo*/
		while (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
			if (peek == '\n') line++;
			readch();
		}
		switch (peek) {	/* gestione della casistica del carattere peek in imput*/
			/* casi costituiti da un carattere solo */
			case '(':
				peek = ' ';	/* se viene letto un carattere, rimette peek a ' ' per il buon funzionamento dello scanner */
				return Token.lpar;	/* ritorna il token relativo a lpar */
			
			/* viene seguito lo stesso procedimento per tutti i rimanenti caratteri della grammatica */	
				
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
				
			default:		/* se peek non corrisponde a nessuno dei simboli conosciuti */ 
				
					if (Character.isDigit(peek)) {	/*se peek è un numero*/
						String s = "";
						do {
						s+= peek;
						readch();
						} while (Character.isDigit(peek));
					
					Number NUM = new Number(Tag.NUM, s);	/*viene creato un nuovo oggetto number che viene ritornato*/
					return NUM;
					} 
				
					/* se siamo arrivati alla fine del programma e abbiamo trovato $ */						
					if (peek == '$') {
						return new Token(Tag.EOF);
					}
					else {	/* altrimenti se viene inserito qualsiasi altro carattere che non fa parte della grammatica viene stampato un errore */
						System.err.println("Erroneous character: "+ peek );
						return null;
					}
		}
	}



public static void main(String[] args) {
	Lexer lex = new Lexer();		/*crea nuovo lexer*/
	Token tok;
	do {
		tok = lex.lexical_scan();
		System.out.println("Scan: " + tok);		/*Stampa su schermo la rappresentazione testuale di quel token (grazie al metodo toString della classe token 
												  o in alternativa il metodo toString della classe Word/Number in quanto classe che estende token)*/
	} while (tok.tag != Tag.EOF);			/* cicla fino ad arrivare all'end of file*/
}
}