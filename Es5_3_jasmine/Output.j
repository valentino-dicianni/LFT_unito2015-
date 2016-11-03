.class public Output 
.super java/lang/Object

.method public <init>()V
 aload_0
 invokenonvirtual java/lang/Object/<init>()V
 return
.end method

.method public static printBool(I)V
 .limit stack 3
 getstatic java/lang/System/out Ljava/io/PrintStream;
 iload_0 
 bipush 1
 if_icmpeq Ltrue
 ldc "false"
 goto Lnext
 Ltrue:
 ldc "true"
 Lnext:
 invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
 return
.end method

.method public static printInt(I)V
 .limit stack 2
 getstatic java/lang/System/out Ljava/io/PrintStream;
 iload_0 
 invokestatic java/lang/Integer/toString(I)Ljava/lang/String;
 invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
 return
.end method

.method public static run()V
 .limit stack 1024
 .limit locals 256
 ldc 1
 istore 0
 ldc 0
 istore 1
 iload 0
 invokestatic Output/printBool(I)V
 iload 1
 invokestatic Output/printBool(I)V
 iload 0
 iload 1
 ior 
 ldc 0
 if_icmpeq L0
 ldc 1
 istore 1
 goto L1
L0:
 ldc 0
 istore 1
L1:
L0:
 iload 0
 invokestatic Output/printBool(I)V
 iload 1
 invokestatic Output/printBool(I)V
L2:
 iload 0
 iload 1
 iand 
 ldc 0
 if_icmpeq L3
 ldc 0
 istore 1
 goto L2
L3:
 iload 0
 invokestatic Output/printBool(I)V
 iload 1
 invokestatic Output/printBool(I)V
 iload 0
 ldc 0
 if_icmpeq L4
 iload 1
 invokestatic Output/printBool(I)V
L4:
 return
.end method

.method public static main([Ljava/lang/String;)V
 invokestatic Output/run()V
 return
.end method

