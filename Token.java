import java.util.ArrayList;

public class Token {

	public int fila;
	public int columna;

	public String lexema;
	public static final ArrayList<String> nombreToken = new ArrayList<String>();

	static{
		nombreToken.add("(");
		nombreToken.add(")");
		nombreToken.add(":");
		nombreToken.add("{");
		nombreToken.add("}");
		nombreToken.add("=");
		nombreToken.add(";");
		nombreToken.add("< <= > >= == !=");
		nombreToken.add("+ -");
		nombreToken.add("* /");
		nombreToken.add("'class'");
		nombreToken.add("'fun'");
		nombreToken.add("'int'");
		nombreToken.add("'float'");
		nombreToken.add("'if'");
		nombreToken.add("'else'");
		nombreToken.add("'fi'");
		nombreToken.add("'print'");
		nombreToken.add("identificador");
		nombreToken.add("numero entero");
		nombreToken.add("numero real");
		nombreToken.add("fin de fichero");
	}

	public int tipo;		// tipo es: ID, ENTERO, REAL ...

	public static final int
		PARI 		= 0,
		PARD		= 1,
		DOSP            = 2,
		LBRA            = 3,
		RBRA            = 4,
		ASIG		= 5,
		PYC		= 6,
		OPREL           = 7,
		OPAS		= 8,
		OPMUL		= 9,
		CLASS		= 10,
		FUN		= 11,
		INT		= 12,
		FLOAT		= 13,
		IF              = 14,
		ELSE            = 15,
		FI              = 16,
		PRINT           = 17,
		ID		= 18,
		NUMENTERO	= 19,
		NUMREAL		= 20,
		EOF		= 21;

	public String toString(){
	        return nombreToken.get(tipo);
	}
}
