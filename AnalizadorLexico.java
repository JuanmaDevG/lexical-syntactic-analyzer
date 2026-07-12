import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.StringBuilder;

public class AnalizadorLexico {
  private RandomAccessFile file;
  private int fila, columna;
  private int lastType;
  private int lastTkLen;

  private static final int SKIP = 0, FINAL = -1, SINGLE_SYMBOL = -2, ERROR = -3;

  public AnalizadorLexico(RandomAccessFile file) {
    this.file = file;
    fila = 0;
    columna = 0;
    lastTkLen = 0;
  }

  public Token siguienteToken() {
    StringBuilder lexbuilder = new StringBuilder();
    char c;
    int state = 0, next;

    try {
      file.seek(lastTkLen);
      c = (char) file.readByte();

      do {
        next = delta(state, c);
        if (next == SKIP) {
          if (c == '\n')
            fila++;
          else
            columna++;
        } else if (next == ERROR) {
          // TODO: look at the specs
          // Throw exception? Return null?
        } else if (next == SINGLE_SYMBOL) {
          lexbuilder.append(c);
          next = FINAL;
        }

        if (next == FINAL) {
          lastTkLen = lexbuilder.length();
          file.seek(-lastTkLen);
          return new Token(fila, columna, lastType, lexbuilder.toString());
        }
        lexbuilder.append(c);

        state = next;
        c = (char) file.readByte();
      } while (true);
    } catch (IOException ioex) {
      return new Token(fila, columna, Token.EOF);
    }
  }

  private int delta(int state, char c) throws IOException {
    switch (state) {
      case 0:
        // Single symbols
        if (c == '(') {
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
        else if (c == 'c')
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
        else if (belongsToID(c)) {
          return 123;
        } else {
          return SKIP;
        }

      case 1:
        if (c == '=') {
          lastType = Token.OPREL;
          return SINGLE_SYMBOL;
        }
        lastType = Token.ASIG;
        file.seek(-1);
        return FINAL;
      case 2:
        lastType = Token.OPREL;
        if (c == '=') {
          return SINGLE_SYMBOL;
        }
        file.seek(-1);
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
          return 16;
        break;
      case 16:
        if (c == 'o')
          return 17;
        break;
      case 17:
        if (c == 'f')
          return 10;
        else if (c == 'n')
          return 11;
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

      // Special cases
      case 31:
        // id code

      default:
        return SKIP;
    }

    // TODO: reaching this point means generic checks
    // and file seek -1, return FINAL
    return SKIP;
  }

  private boolean belongsToID(char c) {
    return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9'));
  }
}
