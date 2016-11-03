import java.io.*;
import java.util.*;
public class Lexer {
	
	/*
	 Consideriamo la seguente nuova definizione di identificatori (ID): un identificatore è composto da una sequenza non vuota di 
	 lettere,numeri,ed il simbolo di sottolineatura _ , che non comincia con un numero e che non può essere composto solo da_.
	 Più precisamente, i terminali ID corrispondono all’espressione regolare ((a..z|A..Z)|(_(_)*(a..z|A..Z|0..9)))(a..z|A..Z|0..9| )*.
	 Estendere il metodo lexer_scan per gestire identficatori che corrispondono alla nuova definizione. 
	*/
	
	public static int line = 1;	/* campo che rappresenta la linea corrente che viene inizializzato a 1 */
	private char peek = ' ';			/* rappresenta il carattere correte inizializzato a ' ' */
	Hashtable words = new Hashtable();	/* crea una nuova struttura dati HASTABLE dove memorizzare le parole chiave riservate*/
	
	void reserve(Word w) { 	/* metodo che riserva parole chiave all'interno dell' HASTABLE*/
		words.put(w.lexeme, w);
	}

	public Lexer() {		/*costruttore di Lexer: inserisce le PAROLE CHIAVE tramite la reserve nella HASHTABLE */
		reserve(new Word(Tag.VAR, "var"));
		reserve(new Word(Tag.INTEGER, "integer"));
		reserve(new Word(Tag.BOOLEAN, "boolean"));
		reserve(new Word(Tag.NOT, "not"));
		reserve(new Word(Tag.TRUE, "true"));
		reserve(new Word(Tag.FALSE, "false"));
		reserve(new Word(Tag.PRINT, "print"));
		reserve(new Word(Tag.IF, "if"));
		reserve(new Word(Tag.THEN, "then"));
		reserve(new Word(Tag.ELSE, "else"));
		reserve(new Word(Tag.WHILE, "while"));
		reserve(new Word(Tag.DO, "do"));
		reserve(new Word(Tag.BEGIN, "begin"));
		reserve(new Word(Tag.END, "end"));
	}
	private void readch() {		/* metodo che legge il CARATTERE SUCCESSIVO */
		try {
		peek = (char)System.in.read(); 	/* legge il carattere in imput: esegue un cast a char  perchè la System.in.read() restituirebbe un int */ 
		} 
		catch (IOException exc) {
		peek = (char) -1;  /* Exception handler:	La lettura non è andata a buon fine, fa il cast di -1 per poterlo scrivere in char peek. */
		}
	}
	
public Token lexical_scan() {	/* metodo che esegue la traduzione in token del testo in imput */ 
		/* se durante la traduzione vengono incontrati spazi, new line, ecc vengono ingorati e viene letto il carattere successivo*/
		while (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
			if (peek == '\n') line++;
			readch();
		}
		switch (peek) {	/* gestione della casistica del carattere peek in imput*/
			/* casi costituiti da un carattere solo */
			case ',':		
				peek = ' ';	/* se viene letto un carattere, rimette peek a ' ' per il buon funzionamento dello scanner */
				return Token.comma;		/* ritorna il token relativo a comma */
			
			case ':':
				readch();		/* continua la lettura nel caso in cui  ':' sia seguito da '=' */
				if (peek == '=') {
					peek = ' ';
					return Word.assign; /* ritorna la parola corrispondente a ':=' */
				}
				else {
					return Token.colon;	/* altrimenti ritorna il token relativo a ':' . Non mette il peek a black cosi alla prossima lettura del 
										carattere non si entra nel while e viene utilizzato	il carattere letto in cerca di un = nel readch() precedente */	
				}
			
			/* viene seguito lo stesso procedimento per tutti i rimanenti caratteri della grammatica */
			
			case ';':
				peek = ' ';
				return Token.semicolon;
			
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
				
			
			case '<':
				readch();
				if (peek == '=') {
					peek = ' ';
					return Word.le;
				}
				if(peek == '>') {
					peek = ' ';
					return Word.ne;
				}
				else {
					return Token.lt;
					}
			
			case '>':
				readch();
				if (peek == '=') {
					peek = ' ';
					return Word.ge;
				}
				else {
						return Token.gt;
					}
			
			
			case '&':		/* la '&' viene gestita a parte perchè  è diversa da && */
				readch();
				if (peek == '&') {
					peek = ' ';
					return Word.and;	/* se la prima '&' non è seguita da una seconda, errore.*/
				} 
				else {
					System.err.println("Erroneous character" + " after & : " + peek );
					return null;		/*non  ho trovato nessun token perchè & da sola non è nulla di valido*/
				}
	
			case '|':
			readch();
				if (peek == '|') {
					peek = ' ';
					return Word.or;	/*ho trovato un'altro | quindi ritorno il token per OR*/
				} 
				else {
					System.err.println("Erroneous character" + " after | : " + peek );
					return null;		
				}
				
			case '=' :
			readch();
				if (peek == '=') {
					peek = ' ';
					return Word.eq;	
				} 
				else {
					System.err.println("Erroneous character" + " after = : " + peek );
					return null;		
				}
				
				
			default:		/* se peek non corrisponde a nessuno dei simboli conosciuti */ 
					if (Character.isLetter(peek) || peek == '_') {
						String s = "";
						int state = 0;
						while (state >= 0 && peek != ' ') {
							switch(state) { 
							case 0:	/*stato iniziale q0*/
								if (Character.isLetter(peek)) {
									state = 1;
									s+= peek;
									readch();
								}
								else if (peek == '_') {
									state = 2;
									s+= peek;
									readch();
								}
								else		
									state = -1;
				
								break;
						
							case 1:	/*stato q1: è stata incontrata una lettera o un numero o un '_'*/
								if (Character.isLetter(peek)) {
									state = 1;
									s+= peek;
									readch();
								}
								else if (Character.isDigit(peek)) {
									state = 1;
									s+= peek;
									readch();
								}
								else if (peek == '_') {
									state = 1;
									s+= peek;
									readch();
								}
								else 
									state = -2;	//stato finale
							
								
								break;
						
							case 2:			/*stato q2: è statp incontrato un '_' come primo carattere...se non incontra nulla dopo '_' viene segnalato l'errore*/
							if (Character.isDigit(peek)) {
								state = 1;
								s+= peek;
								readch();
								}
							else if (Character.isLetter(peek)) {
								state = 1;
								s+= peek;
								readch();
							}
							else if (peek == '_') {
								state = 1;
								s+= peek;
								readch();
							}
							else
								state = -1;
							break;
							}
							
						}
						if(state == -1){
							System.err.println("ERROR: Erroneous character sequence" );
							return null;	
						} 
						
						
						if ((Word)words.get(s) != null)		/*ho finito di leggere la stringa e controllo se esiste una parola riservata nella HASHTABLE */
							return (Word)words.get(s);
						else {		/* altrimenti ho trovato un nuovo identificatore */
							Word wID = new Word(Tag.ID, s);
							words.put(s, wID);			/*mette il nuovo identificatore nella HASHTABLE*/
							return wID;
						}
					} 
			
				else {			/* Altrimenti */
					if (Character.isDigit(peek)) {	/* se peek è un numero */
						String s = "";
						do {
						s+= peek;
						readch();
						} while (Character.isDigit(peek));		/* continua a comporre s finchè trova numeri*/
					
					Number nNUM = new Number(Tag.NUM, s);	/* crea un nuovo oggetto numero e lo ritorna */
					return nNUM;
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