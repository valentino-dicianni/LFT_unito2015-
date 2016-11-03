class Es1_5 {
	
	/*Progettare e implementare un DFA che riconosca il linguaggio dei numeri binari
		(stringhe di 0 e 1) il cui valore e multiplo di 3. Per esempio, ` 110 e 1001 sono stringhe del
		linguaggio (rappresentano rispettivamente i numeri 6 e 9), mentre 10 e 111 no (rappresentano
		rispettivamente i numeri 2 e 7).
	*/
	
	public static boolean scan(String s) {
		
		int state = 0;
		int i = 0;
		while (state >= 0 && i < s.length()) {
			
			final char ch = s.charAt(i++);
			switch (state) {
			case 0:		/*stato q0: divisione per 3 con resto 0*/
				if (ch == '0')
					state = 0;
				else if (ch == '1')
					state = 1;
				else
					state = -1;	/*stato di errore q-1*/
				break;
				
			case 1:		/*stato q1: divisione per 3 con resto */
				if (ch == '0')
					state = 2;
				else if (ch == '1')
					state = 0;
				else
					state = -1;
			break;

			case 2:		/*stato q2: divisione per 3 con resto 2*/
				if (ch == '0')
					state = 1;
				else if (ch == '1')
					state = 2;
				else
					state = -1;
				break;
			
			}
		}
		
		return state == 0;
	}
	public static void main(String[] args) {
		
		System.out.println(scan(args[0]) ? "\nSTRINGA ACCETTATA" : "\nSTRINGA NON ACCETTATA"); /*parte l'analisi della stringa che prende in imput 
																						ciò che trova in args[0], lo passa al metodo scan e
																						stampa il risultato a video*/
	}
}