public class Valutatore {
	
	/* Modificarel’analizzatore sintattico di Esercizio 3.1 in modo da valutare le espressioni aritmetiche semplici, 
	facendo riferimento allo schema di traduzione diretto dalla sintassi seguente:
	
	<start> ::= <expr> EOF {print(expr.val)} 
	<expr>  ::= <term> {exprp.i = term.val } <exprp> {expr.val = exprp.val } 
	<exprp> ::= + <term> {exprp1.i = exprp.i + term.val} <exprp1> {exprp.val = exprp1.val } 
			| 	- <term> {exprp1.i = exprp.i − term.val} <exprp1> {exprp.val = exprp1.val }
			| 	e {exprp.val = exprp.i} 
	
	<term>  ::= <fact> {termp.i = fact.val } <termp> {term.val = termp.val }
	<termp> ::= * <fact> {termp1.i = termp.i * fact.val } <termp1> {termp.val = termp1.val } 
			|   / <fact> {termp1.i = termp.i/fact.val } <termp1> {termp.val = termp1.val } 
			|   e {termp.val = termp.i}

	<fact> ::= (<expr>){fact.val = expr.val } 
			|   NUM{fact.val = NUM.value } 
	
	*/

	
	private Lexer lex	;	 /*campi del parsificatore: un lexer,che restitutisce i token, e un campo look, che rappresenta il token preso in esame */
	private Token look;

	
	public Valutatore(Lexer l) { 	/*costruttore*/
		lex = l;
		move(); /*richiama subito il metodo move per leggere il primo token*/
	}
	
	void move() {	/*metodo che legge il prossimo token e lo stampa*/
		look = lex.lexical_scan(); 
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
	
	
	public void start() {	/*metodo iniziale  start */
		if(look.tag == '(' || look.tag == Tag.NUM){	/*controlla che il token look faccia parte dell' insieme guida della produzione <start> ::= <expr>EOF */
			int expr_val;		/*variabile in cui viene memorizzato il valore di ritorno di expr() per la valutazione */
			expr_val = expr();	/*invoca il metodo  <expr> ed ha inizio la parsificazione top-down a discesa ricorsiva */
			match(Tag.EOF);		/*l'ultimo mach per per assicurarsi di essere arrivati all'end of file*/
			System.out.println("\n" + "Risultato: " + expr_val);	/*viene stampato il risultato della valutazione*/
			System.out.println("-------------->FINE");	
		}else error("syntax error expr\n");		/*se il token look non fa parte dell'insieme guida: errore di sintassi*/
	}
	
	/*viene applicato lo stesso procedimento per tutti gli altri metodi del valutatore*/

	private int expr() {
		int term_val, exprp_val = 0;
		if(look.tag == '(' || look.tag == Tag.NUM){	/*insieme guida*/
				/*variabili in cui memorizzare i valori di ritorno*/
            term_val = term();
            exprp_val = exprp(term_val);
        }else error("syntax error expr\n");
		return exprp_val;	/*a differenza dell'esercizio precedente nel quale in parsificatore faceva solo la parsificazione top-down,
						qui durante la parsificazione viene aggiunta anche la valutazione quindi il return serve per comunicare ai gradi superiori della ricorsione il risultato 
						della valutazione*/
	}
	/*viene applicato lo stesso procedimento per tutti gli altri metodi del parsificatore*/
	
   private int exprp(int exprp_i) {
		int term_val, exprp_val = 0;
		if(look.tag == '+' || look.tag == '-' || look.tag == Tag.EOF || look.tag == ')') {
			switch (look.tag) {
			case '+':
				match('+');
				term_val = term();
				exprp_val = exprp(exprp_i + term_val);
				break;
         
			case '-':
				match('-');
				term_val = term();
				exprp_val = exprp(exprp_i - term_val);
				break;
            
			case Tag.EOF:	/*a differenza dell'esercizio precedente, quando una produzione di riscrive con e, sui siboli dell'iniseme guida di quella produzione non si 
							applica l'istruzione "do nothing" ma in valore di ritorno del metodo diventa uguale a quello passato come parametro del metodo stesso*/
				exprp_val = exprp_i;  
				break;				
			
			case ')':
				exprp_val = exprp_i;  
				break;	
		}
		}else error("syntax error exprp");
	return exprp_val;
   }
	
	
	
   private int term() {
		int fact_val,term_val = 0;
		if(look.tag == '(' || look.tag == Tag.NUM){
			fact_val = fact();
			term_val = termp(fact_val);
		}
		else error("syntax error term");
		return term_val;
	}
	
   
   
   private int termp(int termp_i) {
		int fact_val, termp_val = 0;
		if(look.tag == '+' || look.tag == '-' || look.tag == '/'|| look.tag == '*' || look.tag == ')' ||  look.tag == Tag.EOF) {
		switch (look.tag) {
			case '*':
				match('*');
				fact_val = fact();
				termp_val = termp(termp_i * fact_val);
				break;
         
			case '/':
				match('/');
				fact_val = fact();
				termp_val = termp(termp_i / fact_val);
				break;
            
			case '+':
				termp_val = termp_i;
				break;
            
			case '-':	
				termp_val = termp_i;
				break;
			
			case ')':			
				termp_val = termp_i;
				break;
		
			case Tag.EOF:
				termp_val = termp_i; 
				break;						
		}
		}else error("syntax error termp");
	return termp_val;
	}
	

   private int fact() {
		int fact_val = 0;
		if(look.tag == '(' || look.tag == Tag.NUM) {
			switch (look.tag) {
				
				case Tag.NUM:
					fact_val = Integer.parseInt(look.lexeme); 
					match(Tag.NUM);
					break;
				
				case '(':
					match('(');
					fact_val = expr();
					match(')');
					break;         
			}
		}else error("syntax error fact");
	return fact_val;
   }

	
	public static void main(String[] args) { 
		Lexer lex = new Lexer();		/*creo un Lexer*/
		Valutatore valutatore = new Valutatore(lex); 	/*creo un oggetto Parser con il lexer lex passato come argomento al costruttore*/
		valutatore.start();					/*comincia la parsificazione con lo start();*/			
	}
}