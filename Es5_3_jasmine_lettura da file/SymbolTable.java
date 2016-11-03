import java.util.*;
import java.io.*;

public class SymbolTable {
  
    Map <String, Type> TypeMap = new HashMap <String, Type>();  //SymbolTable dei tipi
    Map <String, Integer> OffsetMap = new HashMap <String, Integer>();   //SymbolTable degli indirizzi
   
    public void insert( String s, Type t, int address ) {   //metodo che INSERISCE tipi e indirizzi nelle SymbolTable
        if( !TypeMap.containsKey(s) )   //verifica che la mappa non contenga un associazione per la key s
            TypeMap.put(s,t);
        else throw new IllegalArgumentException("Variabile gia’ dichiarata.");
        
        if( !OffsetMap.containsValue(address) ) //verifica che non ci sia nessua key con valore address
            OffsetMap.put(s,address);
        else throw new IllegalArgumentException("Riferimento ad unalocazione di memoria gia’ occupata da un’altra variabile." );
    }
    
    public Type lookupType ( String s) {    //metodo che ritorna il Type della variabile s
        if( TypeMap.containsKey(s) )
            return TypeMap.get(s);
        throw new IllegalArgumentException("Variabile sconosciuta ." + s );
    }
    
    public int lookupAddress ( String s ) { //metodo che ritorna l' Address della variabile s
        if( OffsetMap.containsKey(s) )
            return OffsetMap.get(s);
        throw new IllegalArgumentException("Variabile sconosciuta.");
    }
}