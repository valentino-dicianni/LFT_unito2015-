import java.io.*;
import java.util.*;

public class Traduttore {

/* Si scriva un traduttore per programmi ben tipati scritti nel frammento del linguaggio P che permette di stampare
 sul terminale il valore di un’espressione aritmetica-logico. 
 */

private Lexer lex;  /*campi del parsificatore: un lexer,che restitutisce i token, e un campo look, che rappresenta il token preso in esame */
private Token look;

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
	
	public void start(CodeGenerator code) { 	/*metodo iniziale*/
		prog(code);
		match(Tag.EOF);		/*l'ultimo match per per assicurarsi di essere arrivati all'end of file*/
	}
    
	/*-----> N.B l' ordine in cui vengono eseguite le azioni semantiche e  la valutazione degli attributi per ogni produzione della grammatica proposta segue
	lo schema di traduzione (SDT) fornito sulle slide di teoria del corso*/
	
	private void prog(CodeGenerator code) {	/*code viene passato come parametro nei vari metodi in quanto è l'oggetto nel quale viene composta la lista di istruzioni*/
		Type orE_type;	/*variabile in cui viene memorizzato il valore di ritorno di orE per la valutazione */
		
		match(Tag.PRINT);
		match('(');
		orE_type = orE(code);
        match(')');
        if(orE_type == Type.INTEGER)	/*  type checking per la valutazione di quale metodo invokestatic eseguire per la stampa del risultato(INTEGER o BOOLEAN)*/
            code.emit(OpCode.invokestatic, 1);
        else if (orE_type == Type.BOOLEAN)
            code.emit(OpCode.invokestatic, 0);
        else error("ERRORE prog");
    
    }
	/*
	<orE>   ::=  <andE>  <orE_p> {if <orE_p>.tipo = nil then <orE>.tipo = <andE>.tipo  else if <orE_p>.tipo  =  boolean	then <orE>.tipo= boolean  else ERROR()} 
	<orE_p> ::= or  <andE> <orE_p>1  {if <andE>.tipo = boolean && (<orE_p>1.tipo = boolean or <orE_p>1.tipo = nil) then <orE_p>.tipo = boolean else ERROR( )} 
	<orE_p> ::= e {<orE_p>.tipo = nil}   

	*/
	
    private Type orE(CodeGenerator code) {
		Type orE_p_type, orE_type, andE_type ;
		
		andE_type = andE(code);
		orE_p_type = orE_p(code);
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

    private Type orE_p(CodeGenerator code) {
		Type orE_p_type, orE_p1_type, andE_type;
		
		switch(look.tag) {
            case Tag.OR:
                match(Tag.OR);
                andE_type = andE(code);
                code.emit(OpCode.ior);
                // ... type checking ...
                orE_p1_type = orE_p(code);
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
    /*
	 <andE>   ::=  <relE> <andE_p> {if <andE_p>.tipo = nil then <andE>.tipo = <relE>.tipo  else if <andE_p>.tipo  =  boolean	then <andE>.tipo= boolean  else ERROR()} 
	 <andE_p> ::= and  <relE> <andE_p>   {if <relE>.tipo = boolean && (<andE_p>1.tipo = boolean or <endE_p>1.tipo = nil) then <andE_p>.tipo = boolean else ERROR( )} 
	 <andE_p> ::= e {<andE_p>.tipo = nil}   

	*/
    private Type andE(CodeGenerator code) {
		Type andE_p_type, andE_type, relE_type;
		
		relE_type = relE(code);
		andE_p_type = andE_p(code);
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
    
    private Type andE_p(CodeGenerator code) {
		Type andE_p_type, andE_p1_type, relE_type;
		
		switch(look.tag) {
            case Tag.AND:
                match(Tag.AND);
                relE_type = relE(code);
                code.emit(OpCode.iand);
                // ... type checking ...
                andE_p1_type = andE_p(code);
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
    private Type relE(CodeGenerator code) {
        Type relE_p_type, relE_type, addE_type;
        
		addE_type = addE(code);
        relE_p_type = relE_p(code);
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

    private Type relE_p(CodeGenerator code) {
        Type addE_type, relE_p_type ;
        
        switch(look.tag) {
            case Tag.EQ:    //==
                match(Tag.EQ);
                addE_type = addE(code);
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
                addE_type = addE(code);
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
                addE_type = addE(code);
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
                addE_type = addE(code);
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
                addE_type = addE(code);
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
                addE_type = addE(code);
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
  /*<addE> ::= <multE> <addE_p> {if <addE_p>.tipo = nil then <addE>.tipo = <multE>.tipo  else if <multE>.tipo  =  integer then <addE>.tipo= integer  else ERROR( )} 
  <addE_p> ::= + <multE> <addE_p> {if <multE>.tipo = integer and (<addE_p>1.tipo = integer or <addE_p>1.tipo = nil) then <addE_p>.tipo = integer else ERROR( )} 
  <addE_p> ::= - <multE> <addE_p> {if <multE>.tipo = integer and (<addE_p>1.tipo = integer or <addE_p>1.tipo = nil) then <addE_p>.tipo = integer else ERROR( )} 
  <addE_p> ::= e {<addE_p>.tipo = nil} 

  */
    private Type addE(CodeGenerator code) {
        Type addE_p_type, addE_type, multE_type;
        
        multE_type = multE(code);
		addE_p_type = addE_p(code);
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
    
    private Type addE_p(CodeGenerator code) {
        Type multE_type, addE_p_type, addE_p1_type;
        
        switch(look.tag) {
            case '+':
                match('+');
                multE_type = multE(code);
                code.emit(OpCode.iadd);
                // ... type checking ...
                addE_p1_type = addE_p(code);
                if(multE_type == Type.INTEGER && (addE_p1_type == Type.INTEGER || addE_p1_type == Type.NIL))
                    addE_p_type = Type.INTEGER;     
                else { 
					addE_p_type = Type.NIL;
					error("ERRORE orE");
				}
                break;
           
            case '-':
                match('-');
                multE_type = multE(code);
                code.emit(OpCode.isub);
                // ... type checking ...
                addE_p1_type = addE_p(code);
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
    private Type multE(CodeGenerator code) {
        Type multE_type, multE_p_type, fact_type;
        
        fact_type = fact(code);
        multE_p_type = multE_p(code);
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
    
    private Type multE_p(CodeGenerator code) {
        Type multE_p_type, fact_type, multE_p1_type;
        
        switch(look.tag) {
            case '*':
                match('*');
                fact_type = fact(code);
                code.emit(OpCode.imul);
                // ... type checking ...
                multE_p1_type = multE_p(code);
                if(fact_type == Type.INTEGER && (multE_p1_type == Type.NIL || multE_p1_type == Type.INTEGER))
                    multE_p_type = Type.INTEGER;
               else { 
					multE_p_type = Type.NIL;
					error("ERRORE orE");
				}
                break;
                
            case '/':
                match('/');
                fact_type = fact(code);
                code.emit(OpCode.idiv);
                // ... type checking ...
                multE_p1_type = multE_p(code);
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
    
    private Type fact(CodeGenerator code) {
        Type orE_type, fact_type;
		int NUM_val;
		
		switch(look.tag) {
			case '(':
				match('(');
				orE_type = orE(code);
				match(')');
				fact_type = orE_type;
				break;
				
			case Tag.NUM:
				NUM_val = Integer.parseInt(look.lexeme);
                match(Tag.NUM);
				code.emit(OpCode.ldc, NUM_val);
				fact_type = Type.INTEGER;
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
		Lexer lex = new Lexer();		//creo un Lexer
        CodeGenerator code = new CodeGenerator();
		Traduttore traduttore = new Traduttore(lex); 	//creo un oggetto Traduttore
		traduttore.start(code);					//comincia la parsificazione con lo start();
        try {
            code.toJasmin();
        } catch (IOException err) {
            System.out.println("errore ToJasmine");
        }
        System.out.println("\n" + "-------------->FINE");

        
	}	
}