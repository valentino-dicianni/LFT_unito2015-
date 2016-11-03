import java.lang.Character;		/*librearia con metodi Character.isDigit(ch) / Character.isLetter(ch)*/

class Es1_4 {
	
	/*Progettare e implementare un DFA che riconosca il linguaggio degli identificatori
		in un linguaggio in stile Java: un identificatore e una sequenza non vuota di lettere, numeri, ed il `
		simbolo di sottolineatura _ che non comincia con un numero e che non puo essere composto solo `
		da un _
	*/
	
	public static boolean scan(String s) {
		
		int state = 0;	/*stato iniziale q0*/
		int i = 0;		/*intero utilizzato per segnare la posizione della stringa in imput*/
		while (state >= 0 && i < s.length()) {
			final char ch = s.charAt(i++);
			
			switch (state) {
			case 0:			/*stato iniziale q0*/
				if (Character.isLetter(ch))
					state = 1;
				else if (ch == '_')
					state = 2;
				else
					state = -1;	/*stato di errore q-1*/
				break;
				
			case 1:		/*stato q1: è stata incontrata una lettera o un numero o un '_'*/
				if (Character.isLetter(ch))
					state = 1;
				else if (Character.isDigit(ch))
					state = 1;
				else if (ch == '_')
					state = 1;
				else
					state = -1;
				
			break;

			case 2:		/*stato q2: è statp incontrato un '_'*/
				if (Character.isDigit(ch))
					state = 1;
				else if (Character.isLetter(ch))
					state = 1;
				else if (ch == '_')
					state = 1;
				else
					state = -1;
				break;
			
			}
		}
		
		return state == 1;	/*stringa accettata per state = q1*/
	}
	public static void main(String[] args) {
		
		System.out.println(scan(args[0]) ?"\nSTRINGA ACCETTATA" : "\nSTRINGA NON ACCETTATA"); /*parte l'analisi della stringa che prende in imput 
																						ciò che trova in args[0], lo passa al metodo scan e
																						stampa il risultato a video*/
	}
}