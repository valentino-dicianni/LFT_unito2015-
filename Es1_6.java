class Es1_6{
	
	/*Progettare e implementare un DFA con l’alfabeto {/, *, a} che riconosca il linguaggio
		di stringhe che contengono almeno 4 caratteri che iniziano con /*, che finiscono con * /, e che
		contengono una solo occorrenza della sequenza * /, quella finale.
	*/
	
	public static boolean scan(String s) {
		
		int state = 0;
		int i = 0;
		while (state >= 0 && i < s.length()) {
			
			final char ch = s.charAt(i++);
			switch (state) {
			case 0:		/*stato iniziale q0*/
				if (ch == '/')	
					state = 1;
				else
					state = -1;	/*stato di errore q-1 : è stato inserito un carattere che non fa parte della grammatica o una sequenza di caratteri erronea*/
				break;
				
			case 1:		/*stato q1: è stato trovato il primo simbolo '/' */
				if (ch == '*')
					state = 2;
				else
					state = -1;
				break;

			case 2:		/*stato q2: è stato trovato il secondo simbolo '*' */
				if (ch == 'a')
					state = 3;
				else if (ch == '/')
					state = 3;
				else if (ch == '*')
					state = 4;
				else
					state = -1;
				break;
				
			case 3:		/*stato q3: stato in cui vengono trovate sequenze dei caratteri a, /  */
				if (ch == 'a')
					state = 3;
				else if (ch == '/')
					state = 3;
				else if (ch == '*')
					state = 4;
				else
					state = -1;
				break;
			
			case 4:		/*stato q4:  è stato trovato un '*' quindi viene effettuato il controllo se sia seguito o meno da un '/' */
				if (ch == 'a')
					state = 3;
				else if (ch == '/')
					state = 5;
				else if (ch == '*')
					state = 4;
				else
					state = -1;
				break;
			
			case 5:		/*stato accettante q5:  N.B se ci fossero ancora simboli in imput dopo la sequenza '* /'  si andrebbe nello stato di errore q-1*/
				state = -1;
				break;
			}
			
		}
		
		return state == 5;	/*stringa accettata per state = q5*/
	}
	public static void main(String[] args) {
		
		System.out.println(scan(args[0]) ? "\nSTRINGA ACCETTATA" : "\nSTRINGA NON ACCETTATA"); /*parte l'analisi della stringa che prende in imput 
																						ciò che trova in args[0], lo passa al metodo scan e
																						stampa il risultato a video*/
	}
}