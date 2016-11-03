class Es1_8 {
	
	/*Costruire il DFA equivalente al e-NFA in Figura 3: espressione regolare ---> a*ab + ba*a
	*/
	
	public static boolean scan(String s) {
		
		int state = 0;	/*stato iniziale q0*/
		int i = 0;		/*intero utilizzato per segnare la posizione della stringa in imput*/
		while (state >= 0 && i < s.length()) {
			
			final char ch = s.charAt(i++);		/*ch è il simbolo in imput analizzato*/
			switch (state) {
				case 0: 	/*stato iniziale q0*/
					if (ch == 'a')
						state = 1;
					else if (ch == 'b')
						state = 4;
					else
						state = -1;
					break;
				
				case 1: 	/*stato q1: è stata trovata almeno una a all'inizio della stringa*/
					if (ch == 'a')
						state = 2;
					else if (ch == 'b')
						state = 3;
					else
						state = -1;
					break;
				
				case 2:	/*stato q2: è stata trovata un'altra a (questo stato potrebbe tranquillamente essere eliminato)*/
					if (ch == 'a')
						state = 1;
					else if (ch == 'b')
						state = 3;
					else
						state = -1;
					break;
					
				case 3:	/*stato finale q3: se ci fossero altri simboli in imput dopo essere entrati nello stato q3, la stringa non sarebbe accettata*/
					state = -1;
					break;
					
				case 4:		/*stato q4: è stata trovata una b all'inizio della stringa*/
					if (ch == 'a')
						state = 5;
					else
						state = -1;
					break;
					
				case 5:		/*stato finale q5: è stata trovata almeno una a*/
					if (ch == 'a')
						state = 5;
					else
						state = -1;
					break;
				
			}
		
		
		}
		return state == 3 || state == 5;	/*stringa accettata per state = q3 or q5*/
	}
	
	public static void main(String[] args) {
		
		System.out.println(scan(args[0]) ? "\nSTRINGA ACCETTATA" : "\nSTRINGA NON ACCETTATA"); /*parte l'analisi della stringa che prende in imput 
																						ciò che trova in args[0], lo passa al metodo scan e
																						stampa il risultato a video*/
	}
	
	
} 