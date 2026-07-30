import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.StringBuilder;
import java.text.MessageFormat;

public class AnalizadorLexico {
  private RandomAccessFile file;
  private int fila, columna;
  private int lastType;
  private int lastTkLen;

  private static final int SKIP = 0, FINAL = -1, SINGLE_SYMBOL = -2, ERROR = -3;

  public AnalizadorLexico(RandomAccessFile file) {
    this.file = file;
    fila = 1;
    columna = 1;
    lastTkLen = 0;
  }

  public Token siguienteToken() {
    StringBuilder lexbuilder = new StringBuilder();
    char c;
    int state = 0, next;

    try {
      file.seek(file.getFilePointer() + lastTkLen);
      columna += lastTkLen;
      c = (char) file.readByte();
      ignoreComments();

      do {
        next = delta(state, c);
        if (next == SKIP) {
          if (c == '\n') {
            fila++;
            columna = 1;
          } else {
            columna++;
          }
          c = (char) file.readByte();
          continue;
        } else if (next == ERROR) {
          System.out
              .println(MessageFormat.format("Error lexico ({0},{1}): caracter '{2}' incorrecto", fila, columna, c));
          System.exit(-1);
        } else if (next == SINGLE_SYMBOL) {
          lexbuilder.append(c);
          next = FINAL;
        }

        if (next == FINAL) {
          lastTkLen = lexbuilder.length();
          file.seek(file.getFilePointer() - lastTkLen);
          return new Token(fila, columna, lastType, lexbuilder.toString());
        }
        lexbuilder.append(c);

        state = next;
        try {
          c = (char) file.readByte();
        } catch (IOException ioex) {
          if (state != SKIP) {
            System.out.println("Error lexico: fin de fichero inesperado");
            System.exit(-1);
          } else
            return new Token(fila, columna, Token.EOF);
        }
      } while (true);
    } catch (IOException ioex) {
      return new Token(fila, columna, Token.EOF);
    }
  }

  private int delta(int state, char c) throws IOException {
    switch (state) {
      case 0:
        if (canBeIgnored(c)) {
          return SKIP;
        }

        // Single symbols
        else if (c == '(') {
          lastType = Token.PARI;
          return SINGLE_SYMBOL;
        } else if (c == ')') {
          lastType = Token.PARD;
          return SINGLE_SYMBOL;
        } else if (c == ':') {
          lastType = Token.DOSP;
          return SINGLE_SYMBOL;
        } else if (c == '{') {
          lastType = Token.LBRA;
          return SINGLE_SYMBOL;
        } else if (c == '}') {
          lastType = Token.RBRA;
          return SINGLE_SYMBOL;
        } else if (c == ';') {
          lastType = Token.PYC;
          return SINGLE_SYMBOL;
        } else if (c == '+' || c == '-') {
          lastType = Token.OPAS;
          return SINGLE_SYMBOL;
        } else if (c == '*' || c == '/') {
          lastType = Token.OPMUL;
          return SINGLE_SYMBOL;
        }

        // Composed operators
        else if (c == '=') {
          return 1;
        } else if (c == '<' || c == '>') {
          return 2;
        } else if (c == '!') {
          return 3;
        }

        // Reserved words
        lastType = Token.ID;
        if (c == 'c')
          return 4;
        else if (c == 'f')
          return 9;
        else if (c == 'i')
          return 17;
        else if (c == 'e')
          return 21;
        else if (c == 'p') {
          return 25;
        }

        // Generics
        else if (isAlpha(c)) {
          return 30;
        } else if (isNumeric(c)) {
          return 31;
        } else {
          return SKIP;
        }

      case 1:
        if (c == '=') {
          lastType = Token.OPREL;
          return SINGLE_SYMBOL;
        }
        lastType = Token.ASIG;
        file.seek(file.getFilePointer() - 1);
        return FINAL;
      case 2:
        lastType = Token.OPREL;
        if (c == '=') {
          return SINGLE_SYMBOL;
        }
        file.seek(file.getFilePointer() - 1);
        return FINAL;
      case 3:
        if (c == '=') {
          lastType = Token.OPREL;
          return SINGLE_SYMBOL;
        }
        return ERROR;

      // Reserved words
      case 4:
        if (c == 'l')
          return 5;
        break;
      case 5:
        if (c == 'a')
          return 6;
        break;
      case 6:
        if (c == 's')
          return 7;
        break;
      case 7:
        if (c == 's')
          return 8;
        break;
      case 8:
        lastType = Token.CLASS;
        break;
      case 9:
        if (c == 'i')
          return 10;
        else if (c == 'u')
          return 11;
        else if (c == 'l')
          return 13;
        break;
      case 10:
        lastType = Token.FI;
        break;
      case 11:
        if (c == 'n')
          return 12;
        break;
      case 12:
        lastType = Token.FUN;
        break;
      case 13:
        if (c == 'o')
          return 14;
        break;
      case 14:
        if (c == 'a')
          return 15;
        break;
      case 15:
        if (c == 't')
          return 34;
        break;
      case 16:
        if (c == 'o')
          return 17;
        break;
      case 17:
        if (c == 'f')
          return 18;
        else if (c == 'n')
          return 19;
        break;
      case 18:
        lastType = Token.IF;
        break;
      case 19:
        if (c == 't')
          return 20;
        break;
      case 20:
        lastType = Token.INT;
        break;
      case 21:
        if (c == 'l')
          return 22;
        break;
      case 22:
        if (c == 's')
          return 23;
        break;
      case 23:
        if (c == 'e')
          return 24;
        break;
      case 24:
        lastType = Token.ELSE;
        break;
      case 25:
        if (c == 'r')
          return 26;
        break;
      case 26:
        if (c == 'i')
          return 27;
        break;
      case 27:
        if (c == 'n')
          return 28;
        break;
      case 28:
        if (c == 't')
          return 29;
        break;
      case 29:
        lastType = Token.PRINT;
        break;

      // ID
      case 30:
        if (belongsToID(c)) {
          return 30;
        }
        lastType = Token.ID;
        break;

      // Integer
      case 31:
        if (isNumeric(c))
          return 31;
        else if (c == '.')
          return 32;
        lastType = Token.NUMENTERO;
        break;

      // Float
      case 32:
        if (isNumeric(c))
          return 33;
        else
          return ERROR;
      case 33:
        if (isNumeric(c))
          return 33;
        lastType = Token.NUMREAL;
        break;

      // Float keyword
      case 34:
        lastType = Token.FLOAT;
        break;

      default:
        return SKIP;
    }

    if (state >= 4 && state <= 29 && belongsToID(c))
      return 30;

    file.seek(file.getFilePointer() - 1);
    return FINAL;
  }

  private void ignoreComments() throws IOException {
    boolean finished = false;
    long fp = file.getFilePointer();

    while (!finished) {
      while (canBeIgnored((char) file.readByte()))
        ;
      if ((char) file.readByte() == '/' && (char) file.readByte() == '*') {
        while (!finished) {
          if ((char) file.readByte() == '*' && (char) file.readByte() == '/') {
            finished = true;
          }
        }
      } else {
        file.seek(fp);
        finished = true;
      }
    }
  }

  private boolean belongsToID(char c) {
    return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9'));
  }

  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
  }

  private boolean isNumeric(char c) {
    return (c >= '0' && c <= '9');
  }

  private boolean canBeIgnored(char c) {
    return (c == ' ' || c == '\t' || c == '\n');
  }
}
