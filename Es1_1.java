public class Es1_1 {

	public static boolean scan(String s) {
		
		int state = 0;	/*stato iniziale q0*/
		int i = 0;		/*intero utilizzato per segnare la posizione della stringa in imput*/
		while (state >= 0 && i < s.length()) {
			
			final char ch = s.charAt(i++);		/*ch è il simbolo in imput analizzato*/
			switch (state) {
			case 0:		/*operazioni sullo stato q0 a seconda del simbolo in imput che riceve*/
				if (ch == '0')
					state = 1;
				else if (ch == '1')
					state = 0;
				else	/*stato di errore*/
					state = -1;
				break;
				
			case 1:		/*operazioni sullo stato q1 a seconda del simbolo in imput che riceve*/
				if (ch == '0')
					state = 2;
				else if (ch == '1')
					state = 0;
				else
					state = -1;
			break;

			case 2:		/*operazioni sullo stato q2 a seconda del simbolo in imput che riceve*/
				if (ch == '0')
					state = 3;
				else if (ch == '1')
					state = 0;
				else
					state = -1;
				break;
			
			case 3:		/*operazioni sullo stato q3 a seconda del simbolo in imput che riceve*/
				if (ch == '0' || ch == '1')
					state = 3;
				else
					state = -1;
			break;
			}
		}
		if(state == -1)	/*se si finisce nello stato q-1 (stato di errore) al termine del ciclo viene ritornato il valore false*/
			return false;
		else return state == 3;	/*altrimenti, se alla fine della stringa in imput ci troviamo nello satato accettante q3, la stringa viene accettata*/
	}
	public static void main(String[] args) {
		System.out.println(scan(args[0]) ? "\nSTRINGA ACCETTATA" : "\nSTRINGA NON ACCETTATA"); /*parte l'analisi della stringa che prende in imput 
																						ciò che trova in args[0], lo passa al metodo scan e
																						stampa il risultato a video*/
	}
}