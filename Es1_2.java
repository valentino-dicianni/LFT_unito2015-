import java.lang.Character;	/*librearia con metodi Character.isDigit(ch) / Character.isLetter(ch) */

public class Es1_2 {
	
	/*Progettare un DFA che riconosca il linguaggio delle costanti numeriche in virgola
		mobile. Esempi di tali costanti sono:
		123 123.5 .567 +7.5 -.7 67e10 1e-2 -.7e2
	*/
	
	public static boolean scan(String s) {
		int state = 0;	/*stato iniziale q0*/
		int i = 0;		/*intero utilizzato per segnare la posizione della stringa in imput*/
		
		while (state >= 0 && i < s.length()) {
			final char ch = s.charAt(i++);		/*ch è il simbolo in imput analizzato*/
			
			switch (state) {
			case 0:			/*stato iniziale q0*/
				if (Character.isDigit(ch))
					state = 1;
				else if (ch == '.')
					state = 2;
				else if (ch == '+' || ch == '-')
					state = 3;
				else	
					state = -1;	/*stato di errore q-1*/
				break;
				
			case 1:			/*stato q1 : si presenta un numero*/
				if (Character.isDigit(ch))
					state = 1;
				else if (ch == 'e')
					state = 4;
				else if (ch == '.')
					state = 2;
				else	
					state = -1;		
				break;

			case 2:			/*stato q2 : si presenta un simbolo '.'*/
				if (Character.isDigit(ch))
					state = 1;
				else	
					state = -1;
			
			case 3:			/*stato q3 : si presenta un simbolo +/-*/
				if (Character.isDigit(ch))
					state = 1;
				else if (ch == '.')
					state = 2;
				else	
					state = -1;
			break;
			
			case 4:				/*stato q4 : si presenta un simbolo di potenza 'e'*/
				if (Character.isDigit(ch))
					state = 1;
				else if (ch == '+' || ch == '-')
					state = 4;
				else	
					state = -1;
			break;
			}
		}
		return state == 1;	/*l'unico stato accettante è lo stato q1 : la stringa infatti può solo finire con un numero*/
	}
	public static void main(String[] args) {
		System.out.println(scan(args[0]) ? "\nSTRINGA ACCETTATA" : "\nSTRINGA NON ACCETTATA"); /*parte l'analisi della stringa che prende in imput 
																						ciò che trova in args[0], lo passa al metodo scan e
																						stampa il risultato a video*/
	}
}