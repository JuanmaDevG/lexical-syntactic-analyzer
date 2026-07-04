import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

public class AnalizadorLexico {
  private RandomAccessFile f;
  private int fila, columna;

  public AnalizadorLexico(RandomAccessFile f) {
    this.f = f;
    fila = 0;
    columna = 0;
  }

  public Token siguienteToken() {
    char curChar;
    int tipo = Token.EOF;
    Iterator<RandomAccessFile> front, back;

    try {
      while (true) {
        curChar = (char) f.readByte();
        if (curChar == '\n') {
          fila++;
          continue;
        }
        columna++;

        if (curChar == '(') {
          return new Token(fila, columna, Token.PARI);
        } else if (curChar == ')') {
          return new Token(fila, columna, Token.PARD);
        } else if (curChar == ':') {
          return new Token(fila, columna, Token.DOSP);
        } else if (curChar == '{') {
          return new Token(fila, columna, Token.LBRA);
        } else if (curChar == '}') {
          return new Token(fila, columna, Token.RBRA);
        } else if (curChar == ';') {
          return new Token(fila, columna, Token.PYC);
        }

        tipo = Token.OPREL;
        char next = (char) f.readByte();
        if (curChar == '<') {
          if (next == '=') {
            return new Token(fila, columna, tipo, "<=");
          }
          f.seek(-1);
          return new Token(fila, columna, tipo, "<");
        } else if (curChar == '>') {
          if (next == '=') {
            return new Token(fila, columna, tipo, ">=");
          }
          f.seek(-1);
          return new Token(fila, columna, tipo, ">");
        } else if (curChar == '=') {
          if (next == '=') {
            return new Token(fila, columna, tipo, "==");
          }
          f.seek(-1);
          return new Token(fila, columna, Token.ASIG, "=");
        } else if (curChar == '!') {
          next = (char) f.readByte();
          if (next == '=') {
            return new Token(fila, columna, tipo, "!=");
          }
          // TODO: continue here
          // -----------------------------------------------------------------------------------------
        }

        tipo = Token.OPAS;
        if (curChar == '+') {
        } else if (curChar == '-') {
        }

        tipo = Token.OPMUL;
        if (curChar == '*') {
        } else if (curChar == '/') {

        } else if (curChar == 'c') {
          if ((next = (char) f.readByte()) == 'l'
              && (next = (char) f.readByte()) == 'a'
              && (next = (char) f.readByte()) == 's'
              && (next = (char) f.readByte()) == 's') {
            return new Token(fila, columna, Token.CLASS);
          }
        } else if (curChar == 'f') {
        } else if (curChar == 'i') {
        } else if (curChar == 'e') {
        } else if (curChar == 'p') {
        }

      }
    } catch (IOException ioex) {
      return new Token(fila, columna, Token.EOF);
    }
  }
}
