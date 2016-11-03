import java.util.*;
import java.io.*;

public class SymbolTable {
  
    Map <String, Type> TypeMap = new HashMap <String, Type>();  /*SymbolTable dei tipi*/
    Map <String, Integer> OffsetMap = new HashMap <String, Integer>();   /*SymbolTable degli indirizzi*/
   
    public void insert( String s, Type t, int address ) {   /*metodo che inserisce tipi e indirizzi nelle SymbolTable*/
        if( !TypeMap.containsKey(s) )	/*controlla che non sia presente nella ST dei tipi la key pasata come parametro*/
            TypeMap.put(s,t);	/*se non c'è, mette nella ST dei tipi il tipo t associandolo alla key s*/
        else throw new IllegalArgumentException("Variabile già dichiarata.");	/*altrimenti solleva un eccezione*/
        
        if( !OffsetMap.containsValue(address) )	/*controlla che nella ST dei valori non sia presente il valore passato come paramentro*/
            OffsetMap.put(s,address);		/*associa alla key s il valore di address*/
        else throw new IllegalArgumentException("Riferimento ad unalocazione di memoria gia’ occupata da un’altra variabile." );
    }
    
    public Type lookupType ( String s) {    /*etodo che ritorna il Type dell'espressione in s*/
        if( TypeMap.containsKey(s) )
            return TypeMap.get(s);
        throw new IllegalArgumentException("Variabile sconosciuta ." + s );
    }
    
    public int lookupAddress ( String s ) { /*metodo ch eirtorna l' Address dell'espressione in s*/
        if( OffsetMap.containsKey(s) )
            return OffsetMap.get(s);
        throw new IllegalArgumentException("Variabile sconosciuta.");
    }
}