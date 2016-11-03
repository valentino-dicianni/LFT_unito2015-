import java.io.*;
import java.util.*;
public class Lexer {
	public static int line = 1;
	private char peek = ' ';	//rappresenta il carattere correte	
	Hashtable words = new Hashtable();	//struttura dati HASTABLE
	
	void reserve(Word w) { 
		words.put(w.lexeme, w);
	}

	public Lexer() {		//gestisce le PAROLE CHIAVE
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
    
    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        }
        catch (IOException exc) {
            peek = (char) -1; // ERROR 
        }
    }
public Token lexical_scan(BufferedReader br) {
		while (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
			if (peek == '\n') line++;
			readch(br);
		}
		switch (peek) {
			//casi costituiti da un carattere solo
			case ',':		//se leggo un virgola rimetto peek a ' ' per il buon funzionamento dello scanner
				peek = ' ';
				return Token.comma;
			
			case ':':
				readch(br);
				if (peek == '=') {
					peek = ' ';
					return Word.assign;
				}
				else {
					return Token.colon;	//non metto il peek a black cosi alla prossima lettura del carattere non si entra nel while e viene letto il carattere che ho letto in cerca di un = nel readch() prima	
				}
			
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
			
			
			case '&':		// la gestisco a parte perche & è diverso da &&
				readch(br);
				if (peek == '&') {
					peek = ' ';
					return Word.and;	//ho trovato un'altra & quindi ritorno il token per END
				} 
				else {
					System.err.println("Erroneous character" + " after & : " + peek );
					return null;		//non  ho trovato nessun token perchè & da sola non è nulla di valido
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
				if (Character.isLetter(peek)) {		//se è una lettera, non può essere l'inizio di un identificatore o un 
					String s = "";
					do {
						s+= peek;
						readch(br);
					} while (Character.isDigit(peek) || Character.isLetter(peek));		//continuo ad aggiungerlo finche è un carattere o un numero
					
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
					
					Number nNUM = new Number(Tag.NUM, s);
					return nNUM;
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
}


    
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "testo.txt";
        System.out.println("Testo preso in esame: " + path); // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
    }
}