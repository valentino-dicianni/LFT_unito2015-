import java.io.*;
import java.util.*;

public class Traduttore {

/*
 Modificare il programma dell' esercizio precedente in modo da tradurre programmi ben tipati scritti nel linguaggio P, cioè
 il linguaggio completo: rispetto al linguaggio preso in considerazione in Esercizio 5.2, il linguaggio di questo esercizio
 permette cicli di esecuzione, utilizzando il comando while ... do, e comandi condizionali, utilizzando il comando if ... then ... else.
*/

private Lexer lex; /*campi del parsificatore: un lexer,che restitutisce i token, e un campo look, che rappresenta il token preso in esame */
private Token look;
    
    /* Creo una variabile statica address che mi tenga conto degli indirizzi quando vado a riempire la symbolTable con le nuove variabili:
     il campo address verrà incremmentato di uno ogni qual volta una nuova variabile viene aggiunta.
     Ho scelto una variabile statica cosicchè fosse a disposizione di tutta la classe e fosse sempre aggiornata per ogni metodo che
     richieda il soutilizzo */

    public static int address = 0;
    
	
	public Traduttore(Lexer l) { 	/*costruttore*/
		lex = l;
		move(); 	/*richiama subito il metodo move per leggere il primo token*/
	}
	
	void move() {	/*metodo che legge il prossimo token e lo stampa a video*/
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
		} else error("syntax error");	
	}
	
	public void start(CodeGenerator code, SymbolTable table) { 	/*metodo iniziale*/
		prog(code, table);
		match(Tag.EOF);		/*l'ultimo match per vedere se siamo arrivati all'end of file*/
	}
    
	/*-----> N.B l' ordine in cui vengono eseguite le azioni semantiche e  la valutazione degli attributi per ogni produzione della grammatica proposta segue
	lo schema di traduzione (SDT) fornito sulle slide di teoria del corso*/
    
	private void prog(CodeGenerator code, SymbolTable table) {
        declist(table);
        stat(code, table);
    }
	/*
	<declist> ::=  <dec> ; <declist> 
	<declist> ::=  e
	<dec>     ::= <type> <id>  {addtype(ID.lexeme, <type>.tipo) ; <idlist>.it = <type>.tipo} <idlist> 
	*/
    
    private void declist(SymbolTable table) {
        if(look.tag == Tag.BOOLEAN || look.tag == Tag.INTEGER) {    /*controlla che ci siano ancora dichiarazioni di variabili, altrimenti applica la produzione epsilon*/
            dec(table);
            match(';');
            declist(table);
        }
    }
    
    private void dec(SymbolTable table) {
        String ID_val;
        Type type_tipo, idlist_it;
        
        type_tipo = type();
        ID_val = id();
        table.insert(ID_val, type_tipo, address);
        address = address + 1;
        idlist_it = type_tipo;
        idlist(table, idlist_it);
    }
    /*
	<idlist> ::= , <id>  {addtype(ID.lexeme, <idlist>.it) ; <idlist>1.it = <idlist>.it}  <idlist>1 
	<idlist> ::=  e 
	*/
    private void idlist(SymbolTable table, Type idlist_it) {
        String ID_val;
        Type idlist1_it;
        
        if(look.tag == ',') {
            match(',');
            ID_val = id();
            table.insert(ID_val, idlist_it, address);	/*inserisce nella symbolTable il nuovo identificatore trovato con il suo tipo*/
            address = address + 1;
            idlist1_it = idlist_it;
            idlist(table, idlist1_it);
        }
    }
    
	/*
	<type> ::= int  {<type>.tipo = integer}
	<type> ::= bool {<type>.tipo = boolean}
	*/
	
    private Type type() {   /*metodo che ritorna il tipo delle variabili in dec*/
        Type type_tipo;
        
        switch(look.tag) {
            case Tag.INTEGER:
                type_tipo = Type.INTEGER;
                match(Tag.INTEGER);
                break;
                
            case Tag.BOOLEAN:
                type_tipo = Type.BOOLEAN;
                match(Tag.BOOLEAN);
                break;
           
            default:
                type_tipo = Type.NIL;
                error("ERRORE: type checking Type");
                break;
        }
        return type_tipo;
    }
    
    
    private String id() {	/*metodo che restituisce il lessema dell'identificatore ID*/
        String ID_val = "";
        
        if(look.tag == Tag.ID) {
            ID_val = ID_val + look.lexeme;
            match(Tag.ID);
        }
        else error("ERRORE id");
    return ID_val;
    }
	
    /*
	 <stat>  ::= ID := <exp>  {if <id>.type != <or_E>.tipo ERROR( ) ; emit (istore address (ID.lexeme))}
	 <stat> ::=  while {label1 =  newlabel( ) ; emitlabel(label1)} <exp>  
				{if <exp>.tipo != bool ERROR( ) ;
				emit(ldc 0) ; label2 = newlabel( ) ;
				emit(if_icmpeq label2)}
				do <stat>  {emit(goto label1); emitlabel(label2)}

	 
	 <stat> ::=   if <exp>  {if <exp>.tipo != bool ERRORE( ) ;
				  emit(ldc 0) ;
				  label1 =  newlabel( ) ; 
				  emit(if_icmpeq label1)} then <stat>  {emitlabel(label1)}
	 
	 <stat> ::=  if <exp> {if <exp>.tipo != bool ERRORE( ) ; 
				 emit(ldc 0) ; label1 = newlabel( ) ;
				 emit(if_icmpeq label1)} then <stat> 
				 else {label2 = newlabel( ) ; 
				 emit (goto label2); 
				 emitlabel (label1)} <stat>  {emitlabel (label2)} 

	 <stat> ::=  begin <statlist> end 
	 <stat> ::=  print (exp>) {emit(istruzione di stampa)}
	*/
    
    
    private void stat(CodeGenerator code, SymbolTable table) {
        String ID_val;
        Type ID_type, exp_type;
        int ID_address, L1, L2, l1, l2;
        
        switch(look.tag){
            case Tag.ID:
                ID_val = id();
                match(Tag.ASSIGN);
                exp_type = exp(code, table);
                ID_type = table.lookupType(ID_val);
                ID_address = table.lookupAddress(ID_val);
                // ... type checking ...
                if(ID_type != exp_type) error("ERRORE stat");
                code.emit(OpCode.istore, ID_address);
                break;
                
            
            /* N.B, all'interno della print è possibile solo passare espressioni logico/aritmetiche,
               non si possono eseguire assegnamenti nè altro ---> print(x := x +2) non è consentito
               da questa grammatica*/
            case Tag.PRINT:
                match(Tag.PRINT);
                match('(');
                exp_type = exp(code, table);
                match(')');
                if(exp_type == Type.INTEGER)
                    code.emit(OpCode.invokestatic, 1);
                else if (exp_type == Type.BOOLEAN)
                    code.emit(OpCode.invokestatic, 0);
                else error("ERRORE stat");
                break;
                
            case Tag.BEGIN:
                match(Tag.BEGIN);
                statlist(code, table);
                match(Tag.END);
                break;
                
            case Tag.WHILE:
                match(Tag.WHILE);
                L1 = code.newLabel();
                code.emitLabel(L1);
                exp_type = exp(code, table);
                if(exp_type != Type.BOOLEAN) error("ERRORE stat_WHILE");
                code.emit(OpCode.ldc, 0);
                L2 = code.newLabel();
                code.emit(OpCode.if_icmpeq, L2);
                match(Tag.DO);
                stat(code, table);
                code.emit(OpCode.GOto, L1);
                code.emitLabel(L2);
                break;
                
            case Tag.IF:
                match(Tag.IF);
                exp_type = exp(code, table);
                if(exp_type != Type.BOOLEAN) error("ERRORE stat_IF");
                code.emit(OpCode.ldc, 0);
                l1 = code.newLabel();
                code.emit(OpCode.if_icmpeq, l1);
                match(Tag.THEN);
                stat(code, table);
                if(look.tag == Tag.ELSE) {      //caso if ⟨exp⟩ then ⟨stat⟩ else ⟨stat⟩
                    match(Tag.ELSE);
                    l2 = code.newLabel();
                    code.emit(OpCode.GOto, l2);
                    code.emitLabel(l1);
                    stat(code, table);
                    code.emitLabel(l2);
                }
                code.emitLabel(l1);  // caso if ⟨exp⟩ then ⟨stat⟩
                break;
        }
    }
    
    
    private Type exp(CodeGenerator code, SymbolTable table) {
        Type exp_type; 
        exp_type = orE(code, table);
        return exp_type;
    }
    /*
	 <statlist> ::=  <stat> <statlist_p> 
	 <statlist_p> ::= ; <stat> <statlist_p> 
	 <statlist_p> ::= e
	*/
    private void statlist(CodeGenerator code, SymbolTable table) {
        stat(code, table);
        statlist_p(code, table);
    }
    
    private void statlist_p(CodeGenerator code, SymbolTable table) {
        if(look.tag == ';') {
            match(';');
            stat(code, table);
            statlist_p(code, table);
        }
    }
/*
	<orE>   ::=  <andE>  <orE_p> {if <orE_p>.tipo = nil then <orE>.tipo = <andE>.tipo  else if <orE_p>.tipo  =  boolean	then <orE>.tipo= boolean  else ERROR()} 
	<orE_p> ::= or  <andE> <orE_p>1  {if <andE>.tipo = boolean && (<orE_p>1.tipo = boolean or <orE_p>1.tipo = nil) then <orE_p>.tipo = boolean else ERROR( )} 
	<orE_p> ::= e {<orE_p>.tipo = nil}   

	*/
    private Type orE(CodeGenerator code, SymbolTable table) {
		Type orE_p_type, orE_type, andE_type ;
		
		andE_type = andE(code, table);
		orE_p_type = orE_p(code, table);
		if(orE_p_type == Type.NIL)
			orE_type = andE_type;
		else if(orE_p_type == Type.BOOLEAN)
			orE_type = Type.BOOLEAN;
		else { 
			orE_type = Type.NIL;
			error("ERRORE orE");
		}
		return orE_type;
	}

    private Type orE_p(CodeGenerator code, SymbolTable table) {
		Type orE_p_type, orE_p1_type, andE_type;
		
		switch(look.tag) {
            case Tag.OR:
                match(Tag.OR);
                andE_type = andE(code, table);
                code.emit(OpCode.ior);
                // ... type checking ...
                orE_p1_type = orE_p(code, table);
                if(andE_type == Type.BOOLEAN && (orE_p1_type == Type.BOOLEAN || orE_p1_type == Type.NIL))
                    orE_p_type = Type.BOOLEAN;     
				else { 
					orE_p_type = Type.NIL;
					error("ERRORE orE");
				}
                break;
			default:
				orE_p_type = Type.NIL;
				break;
		}
		return orE_p_type;
		
	}
    
    private Type andE(CodeGenerator code, SymbolTable table) {
		Type andE_p_type, andE_type, relE_type;
		
		relE_type = relE(code, table);
		andE_p_type = andE_p(code, table);
		if(andE_p_type == Type.NIL)
			andE_type = relE_type;
		else if(andE_p_type == Type.BOOLEAN)
			andE_type = Type.BOOLEAN;
		else { 
			andE_type = Type.NIL;
			error("ERRORE orE");
		}
		return andE_type;
	}
     /*
	 <andE>   ::=  <relE> <andE_p> {if <andE_p>.tipo = nil then <andE>.tipo = <relE>.tipo  else if <andE_p>.tipo  =  boolean	then <andE>.tipo= boolean  else ERROR()} 
	 <andE_p> ::= and  <relE> <andE_p>   {if <relE>.tipo = boolean && (<andE_p>1.tipo = boolean or <endE_p>1.tipo = nil) then <andE_p>.tipo = boolean else ERROR( )} 
	 <andE_p> ::= e {<andE_p>.tipo = nil}   

	*/
    private Type andE_p(CodeGenerator code, SymbolTable table) {
		Type andE_p_type, andE_p1_type, relE_type;
		
		switch(look.tag) {
            case Tag.AND:
                match(Tag.AND);
                relE_type = relE(code, table);
                code.emit(OpCode.iand);
                // ... type checking ...
                andE_p1_type = andE_p(code, table);
                if(relE_type == Type.BOOLEAN && (andE_p1_type == Type.BOOLEAN || andE_p1_type == Type.NIL))
                    andE_p_type = Type.BOOLEAN;     
                else { 
					andE_p_type = Type.NIL;
					error("ERRORE orE");
				}
                break;
			default:
				andE_p_type = Type.NIL;
				break;
		}
		return andE_p_type;
		
	}
    /*
	 <relE>   ::=  <addE> <relE_p>  {if <relE_p>.tipo = nil then <relE>.tipo = <addE>.tipo  else if <addE>.tipo = integer then <relE>.tipo = boolean else ERROR( )} 
	 <relE_p> ::= <oprel> <addE> {if <addE>.tipo = integer then <relE_p>.tipo = integer else ERROR( ) ;
								  label1 = newlabel( ) ;
								  label2 = newlabel( ) ; 
								  emit(if_icmplt  label1) ; 
								  emit(ldc  0) ; 
								  emit(goto label2) ; 
								  emitlabel(label1) ; 
								  emit(ldc 1) ; 
								  emitlabel(label2)
								  } 
	 <relE_p> ::= e  {<relE_p>.tipo = nil}
	 <oprel>  ::= < | > | = | <= | >= | <>
	*/
    private Type relE(CodeGenerator code, SymbolTable table) {
        Type relE_p_type, relE_type, addE_type;
        
		addE_type = addE(code, table);
        relE_p_type = relE_p(code, table);
        if(relE_p_type == Type.NIL) 
            relE_type = addE_type;
        else if(addE_type == Type.INTEGER)
            relE_type = Type.BOOLEAN;
        else { 
			relE_type = Type.NIL;
			error("ERRORE orE");
		}
        return relE_type;
    }

    private Type relE_p(CodeGenerator code, SymbolTable table) {
        Type addE_type, relE_p_type ;
        
        switch(look.tag) {
            case Tag.EQ:    //==
                match(Tag.EQ);
                addE_type = addE(code, table);
                int ltrue = code.newLabel();
                int lnext = code.newLabel();
                code.emit (OpCode.if_icmpeq,ltrue );
                code.emit (OpCode.ldc,0);
                code.emit (OpCode.GOto,lnext);
                code.emitLabel (ltrue);
                code.emit (OpCode.ldc,1);
                code.emitLabel (lnext);
                // ... type checking ...
                if(addE_type == Type.INTEGER)
                    relE_p_type = Type.INTEGER;
				else { 
					relE_p_type = Type.NIL;
					error("ERRORE orE");
				}
                break;
            
            case Tag.NE:    //<>
                match(Tag.NE);
                addE_type = addE(code, table);
                int ltrue0 = code.newLabel();
                int lnext0 = code.newLabel();
                code.emit (OpCode.if_icmpne,ltrue0 );
                code.emit (OpCode.ldc,0);
                code.emit (OpCode.GOto,lnext0);
                code.emitLabel (ltrue0);
                code.emit (OpCode.ldc,1);
                code.emitLabel (lnext0);
                // ... type checking ...
                if(addE_type == Type.INTEGER)
                    relE_p_type = Type.INTEGER;
                else { 
					relE_p_type = Type.NIL;
					error("ERRORE orE");
				}
                break;
                
            case Tag.LE:    //<=
                match(Tag.LE);
                addE_type = addE(code, table);
                int ltrue1 = code.newLabel();
                int lnext1 = code.newLabel();
                code.emit (OpCode.if_icmple,ltrue1 );
                code.emit (OpCode.ldc,0);
                code.emit (OpCode.GOto,lnext1);
                code.emitLabel (ltrue1);
                code.emit (OpCode.ldc,1);
                code.emitLabel (lnext1);
                // ... type checking ...
                if(addE_type == Type.INTEGER)
                    relE_p_type = Type.INTEGER;
                else { 
					relE_p_type = Type.NIL;
					error("ERRORE orE");
				}
                break;
                
            case Tag.GE:    //>=
                match(Tag.GE);
                addE_type = addE(code, table);
                int ltrue2 = code.newLabel();
                int lnext2 = code.newLabel();
                code.emit (OpCode.if_icmpge,ltrue2 );
                code.emit (OpCode.ldc,0);
                code.emit (OpCode.GOto,lnext2);
                code.emitLabel (ltrue2);
                code.emit (OpCode.ldc,1);
                code.emitLabel (lnext2);
                // ... type checking ...
                if(addE_type == Type.INTEGER)
                    relE_p_type = Type.INTEGER;
                else { 
					relE_p_type = Type.NIL;
					error("ERRORE orE");
				}
                break;
                
            case '<':    //<
                match('<');
                addE_type = addE(code, table);
                int ltrue3 = code.newLabel();
                int lnext3 = code.newLabel();
                code.emit (OpCode.if_icmplt,ltrue3 );
                code.emit (OpCode.ldc,0);
                code.emit (OpCode.GOto,lnext3);
                code.emitLabel (ltrue3);
                code.emit (OpCode.ldc,1);
                code.emitLabel (lnext3);
                // ... type checking ...
                if(addE_type == Type.INTEGER)
                    relE_p_type = Type.INTEGER;
                else { 
					relE_p_type = Type.NIL;
					error("ERRORE orE");
				}
                break;
            
            case '>':    //>
                match('>');
                addE_type = addE(code, table);
                int ltrue4 = code.newLabel();
                int lnext4 = code.newLabel();
                code.emit (OpCode.if_icmpgt,ltrue4 );
                code.emit (OpCode.ldc,0);
                code.emit (OpCode.GOto,lnext4);
                code.emitLabel (ltrue4);
                code.emit (OpCode.ldc,1);
                code.emitLabel (lnext4);
                // ... type checking ...
                if(addE_type == Type.INTEGER)
                    relE_p_type = Type.INTEGER;
                else { 
					relE_p_type = Type.NIL;
					error("ERRORE orE");
				}
                break;
                
            default:
                relE_p_type = Type.NIL; 
                break;
        
        }
        return relE_p_type;
    }
  /*
  <addE> ::= <multE> <addE_p> {if <addE_p>.tipo = nil then <addE>.tipo = <multE>.tipo  else if <multE>.tipo  =  integer then <addE>.tipo= integer  else ERROR( )} 
  <addE_p> ::= + <multE> <addE_p> {if <multE>.tipo = integer and (<addE_p>1.tipo = integer or <addE_p>1.tipo = nil) then <addE_p>.tipo = integer else ERROR( )} 
  <addE_p> ::= - <multE> <addE_p> {if <multE>.tipo = integer and (<addE_p>1.tipo = integer or <addE_p>1.tipo = nil) then <addE_p>.tipo = integer else ERROR( )} 
  <addE_p> ::= e {<addE_p>.tipo = nil} 

  */
    private Type addE(CodeGenerator code, SymbolTable table) {
        Type addE_p_type, addE_type, multE_type;
        
        multE_type = multE(code, table);
		addE_p_type = addE_p(code, table);
        if(addE_p_type == Type.NIL)
            addE_type = multE_type;
        else if(multE_type == Type.INTEGER)
            addE_type = Type.INTEGER;
        else { 
			addE_type = Type.NIL;
            error("ERRORE orE");
		}
        return addE_type;
            
    }
    
    private Type addE_p(CodeGenerator code, SymbolTable table) {
        Type multE_type, addE_p_type, addE_p1_type;
        
        switch(look.tag) {
            case '+':
                match('+');
                multE_type = multE(code, table);
                code.emit(OpCode.iadd);
                // ... type checking ...
                addE_p1_type = addE_p(code, table);
                if(multE_type == Type.INTEGER && (addE_p1_type == Type.INTEGER || addE_p1_type == Type.NIL))
                    addE_p_type = Type.INTEGER;     
                else { 
					addE_p_type = Type.NIL;
					error("ERRORE orE");
				}
                break;
           
            case '-':
                match('-');
                multE_type = multE(code, table);
                code.emit(OpCode.isub);
                // ... type checking ...
                addE_p1_type = addE_p(code, table);
                if(multE_type == Type.INTEGER && (addE_p1_type == Type.INTEGER || addE_p1_type == Type.NIL))
                    addE_p_type = Type.INTEGER;      
                else { 
					addE_p_type = Type.NIL;
					error("ERRORE orE");
				}
                break;
            
            default:
                addE_p_type = Type.NIL;
                break;
        }
        return addE_p_type;
    }
     /*
	 <multE> ::= <fact> <multE_p> {if <multE_p>.tipo = nil then <multE>.tipo = <fact>.tipo  else if <fact>.tipo  =  integer then <multE>.tipo= integer  else ERROR( )} 
	 <multE_p> ::= *  <fact> <multE_p>   {if <fact>.tipo = integer and (<multE_p>1.tipo = integer or <multE_p>1.tipo = nil) then <multE_p>.tipo = integer else ERROR( )}
	 <multE_p> ::= / <fact> <multE_p>    {if <fact>.tipo = integer and (<multE_p>1.tipo = integer or <multE_p>1.tipo = nil) then <multE_p>.tipo = integer else ERROR( )}
	 <multE_p> ::= e  {<multE_p>.tipo = nil}

	*/
    private Type multE(CodeGenerator code, SymbolTable table) {
        Type multE_type, multE_p_type, fact_type;
        
        fact_type = fact(code, table);
        multE_p_type = multE_p(code, table);
        if(multE_p_type == Type.NIL)
            multE_type = fact_type;
        else if(fact_type == Type.INTEGER)
            multE_type = Type.INTEGER;
        else { 
			multE_type = Type.NIL;
			error("ERRORE orE");
		}
        return multE_type;
    }
    
    private Type multE_p(CodeGenerator code, SymbolTable table) {
        Type multE_p_type, fact_type, multE_p1_type;
        
        switch(look.tag) {
            case '*':
                match('*');
                fact_type = fact(code, table);
                code.emit(OpCode.imul);
                // ... type checking ...
                multE_p1_type = multE_p(code, table);
                if(fact_type == Type.INTEGER && (multE_p1_type == Type.NIL || multE_p1_type == Type.INTEGER))
                    multE_p_type = Type.INTEGER;
               else { 
					multE_p_type = Type.NIL;
					error("ERRORE orE");
				}
                break;
                
            case '/':
                match('/');
                fact_type = fact(code, table);
                code.emit(OpCode.idiv);
                // ... type checking ...
                multE_p1_type = multE_p(code, table);
                if(fact_type == Type.INTEGER && (multE_p1_type == Type.NIL || multE_p1_type == Type.INTEGER))
                    multE_p_type = Type.INTEGER;
                else { 
					multE_p_type = Type.NIL;
					error("ERRORE orE");
				}
                break;

            default:
                multE_p_type = Type.NIL;
                break;
        }
        return multE_p_type;
    }
    /*
	<fact> ::= (<orE>)   {<fact>.tipo = <orE>.tipo}
	<fact> ::= NUM 	{<fact>.tipo = integer}
	<fact> ::= true {<fact>.tipo = boolean}
	<fact> ::= false {<fact>.tipo = boolean}
	*/
	
    private Type fact(CodeGenerator code, SymbolTable table) {
        Type orE_type, fact_type, ID_type;
		int NUM_val, ID_address;
        String ID_val;
		
		switch(look.tag) {
			case '(':
				match('(');
				orE_type = orE(code, table);
				match(')');
				fact_type = orE_type;
				break;
				
			case Tag.NUM:
				NUM_val = Integer.parseInt(look.lexeme);
                match(Tag.NUM);
				code.emit(OpCode.ldc, NUM_val);
				fact_type = Type.INTEGER;
				break;
            
            case Tag.ID:
                ID_val = look.lexeme;
                match(Tag.ID);
                ID_address = table.lookupAddress(ID_val);
                ID_type = table.lookupType(ID_val);
                code.emit(OpCode.iload, ID_address);
                fact_type = ID_type;
                break;
			
			case Tag.TRUE:
				match(Tag.TRUE);
				code.emit(OpCode.ldc, 1);
				fact_type = Type.BOOLEAN;
				break;
			
			case Tag.FALSE:
				match(Tag.FALSE);
				code.emit(OpCode.ldc, 0);
				fact_type = Type.BOOLEAN;
				break;
        
			default:
				fact_type = Type.NIL;
                break;
		}
		return fact_type;
	}
	
	
	
	
	public static void main(String[] args) {
        Lexer lex = new Lexer();                        //creo un oggetto Lexer
        CodeGenerator code = new CodeGenerator();       //creo un oggetto CodeGenerator
		Traduttore traduttore = new Traduttore(lex); 	//creo un oggetto Traduttore
        SymbolTable table = new SymbolTable();          //creo un oggetto SymbolTable
		traduttore.start(code, table);                  //comincia la parsificazione con lo start();
        try {                                           //gestione dell'errore
            code.toJasmin();
        } catch (IOException err) {
            System.out.println("errore ToJasmine");
        }
        System.out.println("\n" + "-------------->FINE");

        
	}	
}