import java.io.*;
import java.util.*;

public class Lexer {
	/*
	Lettura da un file:

	La lettura di un programma da un file, anzichè dalla tastiera come inListing 4, può essere realizzata nel modo illustrato in Listing 5.
	Il metodo main crea un oggetto della classe BufferedReader, che poi è passato come parametro al metodo lexical scan, e a sua volta a readch.
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
		
		if (peek == (char)-1) { /* sostituisce if (peek == ’$’)*/
			return new Token(Tag.EOF); 
		}
	
		switch (peek) {	/* gestione della casistica del carattere peek in imput*/
			/* casi costituiti da un carattere solo */
			case ',':		
				peek = ' ';	/* se viene letto un carattere, rimette peek a ' ' per il buon funzionamento dello scanner */
				return Token.comma;		/* ritorna il token relativo a comma */
			
			case ':':
				readch(br);		/* continua la lettura nel caso in cui  ':' sia seguito da '=' */
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
				readch(br);
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
				readch(br);
				if (peek == '=') {
					peek = ' ';
					return Word.ge;
				}
				else {
						return Token.gt;
					}
			
			
			case '&':		/* la '&' viene gestita a parte perchè  è diversa da && */
				readch(br);
				if (peek == '&') {
					peek = ' ';
					return Word.and;	/* se la prima '&' non è seguita da una seconda, errore.*/
				} 
				else {
					System.err.println("Erroneous character" + " after & : " + peek );
					return null;		/*non  ho trovato nessun token perchè & da sola non è nulla di valido*/
				}
	
			case '|':
				readch(br);
				if (peek == '|') {
					peek = ' ';
					return Word.or;	//ho trovato un'altro | quindi ritorno il token per OR
				} 
				else {
					System.err.println("Erroneous character" + " after | : " + peek );
					return null;		
				}
				
			case '=' :
				readch(br);
				if (peek == '=') {
					peek = ' ';
					return Word.eq;	
				} 
				else {
					System.err.println("Erroneous character" + " after = : " + peek );
					return null;		
				}
				
				
			
			default:		//se non sono nessuno di quelli gestiti prima 
					if (Character.isLetter(peek) || peek == '_') {
						String s = "";
						int state = 0;
						while (state >= 0 && peek != ' ') {
							switch(state) { 
							case 0:	//se continuo a trovare lettere
								if (Character.isLetter(peek)) {
									state = 1;
									s+= peek;
									readch(br);
								}
								else if (peek == '_') {
									state = 2;
									s+= peek;
									readch(br);
								}
								else		
									state = -1;
				
								break;
						
							case 1:
								if (Character.isLetter(peek)) {
									state = 1;
									s+= peek;
									readch(br);
								}
								else if (Character.isDigit(peek)) {
									state = 1;
									s+= peek;
									readch(br);
								}
								else if (peek == '_') {
									state = 1;
									s+= peek;
									readch(br);
								}
								else 
									state = -2;	//stato finale
							
								
								break;
						
							case 2:			//se incontro '_' : se non è seguito da nulla mi da l'errore, altrimenti va allo stato 1
							if (Character.isDigit(peek)) {
								state = 1;
								s+= peek;
								readch(br);
								}
							else if (Character.isLetter(peek)) {
								state = 1;
								s+= peek;
								readch(br);
							}
							else if (peek == '_') {
								state = 1;
								s+= peek;
								readch(br);
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
						
						
						if ((Word)words.get(s) != null)		//ho finito di leggere la stringa e controllo se esiste un token assegnato a quella stringa
							return (Word)words.get(s);
						else {		//altrimenti ho trovato un nuovo identificatore
							Word wID = new Word(Tag.ID, s);
							words.put(s, wID);			//lo metto in tabella
							return wID;
						}
					} 
				
				else {		//altrimenti
					if (Character.isDigit(peek)) {
						String s = "";
						do {
						s+= peek;
						readch(br);
						} while (Character.isDigit(peek));
					
					Word wNUM = new Word(Tag.NUM, s);
					return wNUM;
					} 
				
					else {	//se viene inserito qualsiasi altro carattere che non fa parte della grammatica
						System.err.println("Erroneous character: "+ peek );
						return null;
					}
				}
	}
}

	






public static void main(String[] args) {
	Lexer lex = new Lexer();
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