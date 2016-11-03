import java.util.LinkedList;
import java.io.*;


public class CodeGenerator {
    LinkedList<Instruction> instructions = new LinkedList<Instruction>(); /*generics: crea una lista concatenata di Instruction*/
    int label=0;
    
    public void emit( OpCode opCode) {  /*metodi emit ---> aggiungono alla lista concateata instructions nuovi membri Instruction*/
        instructions.add( new Instruction(opCode));
    }
    public void emit( OpCode opCode , int operand ) {
        instructions.add( new Instruction( opCode, operand ));
    }
    public void emitLabel (int operand ) {  /*aggiunge una nuova label allla lista*/
        emit( OpCode.label , operand );
    }
    public int newLabel () {    /*incrementa il contatore di label*/
        return label++;
    }
    public void toJasmin () throws IOException {	/*metodo toJasmine che genera il Bytecode del linguaggio e lo salva in un file Output.j che verrà dato in pasto a Jasmine*/
        PrintWriter out = new PrintWriter(new FileWriter("Output.j"));  /*crea un file "Output.j" e apre uno stream*/
        String temp = "";
        temp = temp + header;   /*concatena alla stringa temp "" la stringa header*/
        while(instructions.size() > 0){ /*scorre la lista di istruzioni*/
            Instruction tmp = instructions.remove();    /*puntatore al nodo rimosso della lista*/
            temp = temp + tmp.toJasmin();   /*concatena la stringa puntata da temp con la stringa  restituita dal metodo toJasmin in Instruction.java applicato su tmp*/
        }
        temp = temp +  footer;  /*concatena la stringa risultante con la stringa footer*/
        out.println(temp);  /*copia la stringa ottenuta nel file "Output.j"*/
        out.flush();		/*fa il flush dello stream*/
        out.close();    /*chiude lo stream*/
    }
    
    private static final String header = ".class public Output \n" + ".super java/lang/Object\n"
    + "\n"
    + ".method public <init>()V\n"
    + " aload_0\n"
    + " invokenonvirtual java/lang/Object/<init>()V\n"
    + " return\n"
    + ".end method\n"
    + "\n"
    + ".method public static printBool(I)V\n"
    + " .limit stack 3\n"
    + " getstatic java/lang/System/out Ljava/io/PrintStream;\n"
    + " iload_0 \n"
    + " bipush 1\n"
    + " if_icmpeq Ltrue\n"
    + " ldc \"false\"\n"
    + " goto Lnext\n"
    + " Ltrue:\n"
    + " ldc \"true\"\n"
    + " Lnext:\n"
    + " invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n"
    + " return\n"
    + ".end method\n"
    + "\n"
    + ".method public static printInt(I)V\n"
    + " .limit stack 2\n"
    + " getstatic java/lang/System/out Ljava/io/PrintStream;\n"
    + " iload_0 \n"
    + " invokestatic java/lang/Integer/toString(I)Ljava/lang/String;\n"
    + " invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n"
    + " return\n"
    + ".end method\n"
    + "\n"
    + ".method public static run()V\n"
    + " .limit stack 1024\n"
    + " .limit locals 256\n";
    
    private static final String footer = " return\n"
    + ".end method\n"
    + "\n"
    + ".method public static main([Ljava/lang/String;)V\n" + " invokestatic Output/run()V\n"
    + " return\n"
    + ".end method\n";
}