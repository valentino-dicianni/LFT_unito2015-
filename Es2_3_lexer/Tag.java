public class Tag {
		
	/* Dato che i valori della codifica ASCII in decimale arrivano fino al numero 255, per identificare durante la traduzione qeuste parole specifiche 
		con un determinato tag, ad ogni parola viene attribuito un valore da 256 in su. Qui i vari identificatori sono definiti come costanti statiche della classe*/
	
	public final static int
	EOF = -1,
	NUM = 256,
	ID = 257,
	AND = 258,
	OR = 259,
	VAR = 260,
	INTEGER = 261,
	BOOLEAN = 262,
	ASSIGN = 263,
	EQ = 264,
	GE = 265,
	LE = 266,
	NE = 267,
	TRUE = 268,
	FALSE = 269,
	NOT = 270,
	PRINT = 271,
	IF = 272,
	THEN = 273,
	ELSE = 274,
	WHILE = 275,
	DO = 276,
	BEGIN = 277,
	END = 278;
	
	
}