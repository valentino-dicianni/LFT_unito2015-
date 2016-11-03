import java.io.*;
public class Parser {
	private Lexer_extended lex; /*campi del parsificatore: un lexer,che restitutisce i token, e un campo look, che rappresenta il token preso in esame, e un campo Buffer pbr */
	private BufferedReader pbr; 
	private Token look;

	public Parser(Lexer_extended l, BufferedReader br) {     /*costruttore*/
			lex = l;
			pbr = br;	/*inizializza il campo pdr con il buffer br*/
			move();	 /*richiama subito il metodo move per leggere il primo token*/
	}
	void move() {	/*metodo che legge il prossimo token e lo stampa*/
		look = lex.lexical_scan(pbr);
		System.err.println("token = " + look);
	}
	
	void error(String s) {		/*metodo di errore...è stato trovato un errore di sintassi */
		throw new Error("near line " + lex.line + ": " + s);
	}
	
	void match(int t) {		/*metodo che verifica che non siamo arrivati alla fine del file e che non ci siano 
							errori di sintassi(ovvero che il token presente sia esattamente quello che ci si aspetta), poi muove al prossimo carattere */
		if (look.tag == t) {	/*verifica che il campo tag del token in esame sia uguale all'intero passato come parametro che rappresenta il campo tag che si il parsificatore si aspetta di trovare*/
			if (look.tag != Tag.EOF) move();
		} else error("syntax error match");	
	}
	
	public void start() { 	/*metodo iniziale  start */
		if(look.tag == '(' || look.tag == Tag.NUM){	/*controlla che il token look faccia parte dell' insieme guida della produzione <start> ::= <expr>EOF */
			expr();	/*invoca il metodo  <expr> ed ha inizio la parsificazione top-down a discesa ricorsiva */
			match(Tag.EOF);		/*l'ultimo mach per per assicurarsi di essere arrivati all'end of file*/
		System.out.println("-------------->FINE");
		}else error("syntax error expr");	/*se il token look non fa parte dell'insieme guida: errore di sintassi*/
	}	
	
	/*viene applicato lo stesso procedimento per tutti gli altri metodi del parsificatore*/
    
	private void expr() { 
		if(look.tag == '(' || look.tag == Tag.NUM){	/*insieme guida*/
			term();
			exprp(); 
		}else error("syntax error expr");
	}
	
	private void exprp() { 
		if(look.tag == '+' || look.tag == '-' || look.tag == Tag.EOF || look.tag == ')') {
			switch (look.tag) { 
		
			case '+':
				match('+'); 
				term(); 
				exprp(); 
				break;
		
			case '-':
				match('-'); 
				term(); 
				exprp(); 
				break;
		
			case Tag.EOF:	/*quando la produzione si riscrive <termp> ::= e, sui simboli dell'insieme guida di questa produzione si esegue
							l'operazione "do nothing" e si disalloca il frame relativo a questo metodo*/
				break;
		
			case ')':
				break;
			}
		}
		else error("syntax error exprp");
	
	}
	
	private void term() {
		if(look.tag == '(' || look.tag == Tag.NUM){
			fact();
			termp();
		}
		else error("syntax error term");

	}
	
	private void termp() { 
		if(look.tag == '+' || look.tag == '-' || look.tag == Tag.EOF || look.tag == ')' || look.tag == '*' || look.tag == '/') {
			switch (look.tag) { 
			
			case '*':
				match('*');
				fact();
				termp(); 
				break;
			
			case '/':
				match('/');
				fact();
				termp(); 
				break;
		
			case '+': 
				break;
		
			case '-': 
				break;
		
			case ')':
				break;
			
			case Tag.EOF:
				break;
		
			}
		}
		else error("syntax error termp");
	
	}	
	
	private void fact() { 
		if(look.tag == Tag.NUM || look.tag == '(') {
			switch (look.tag) {
				case '(': 
					match('(');
					expr();
					match(')');
					break;
			
				case Tag.NUM:		
					match(look.tag);	
					break;
			/*La classe number è sottoclasse di token: look è di tipo Token, quindi quando vado a guardare look.tag, viene preso il 
			tag di quel Token. Quando creo un oggetto Number, il suo tag = Tag.NUM, quindi a differenza degli altri casi, dove passo il char equivalente 
			al numero in codifica ASCII, qui sono costretto a passare direttamente Tag.NUM dato che nella codifica ASCII non esistono caratteri
			la cui codifica ASCII sia uguale a Tag.NUM (ovvero 256 in questo caso)*/
			}
		}
		else error("syntax error fact");
	
	}

	
	public static void main(String[] args) {
		Lexer_extended lex = new Lexer_extended();
		String path = "testo.txt"; /* il percorso del file da leggere */
		try {
			BufferedReader br = new BufferedReader(new FileReader(path)); 
			Parser parser = new Parser(lex, br);
			parser.start();
			br.close();
		}
		catch (IOException e) {e.printStackTrace();} 
	}
}