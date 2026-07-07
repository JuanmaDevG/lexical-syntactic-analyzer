import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.StringBuilder;

public class AnalizadorLexico {
  private RandomAccessFile file;
  private int fila, columna;
  private int lastType;
  private int lastTkLen;

  private static final int SKIP = 0, FINAL = -1, ERROR = -2;

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
        }
        if (next == ERROR) {
          // Throw exception? Return null?
        }
        if (next == FINAL) {
          lastTkLen = lexbuilder.length();
          file.seek(-lexbuilder.length());
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
        if (c == '(') {
          lastType = Token.PARI;
          return FINAL;
        } else if (c == ')') {
          lastType = Token.PARD;
          return FINAL;
        } else if (c == ':') {
          lastType = Token.DOSP;
          return FINAL;
        } else if (c == '{') {
          lastType = Token.LBRA;
          return FINAL;
        } else if (c == '}') {
          lastType = Token.RBRA;
          return FINAL;
        } else if (c == ';') {
          lastType = Token.PYC;
          return FINAL;
        } else if (c == '-' || c == '+') {
          lastType = Token.OPAS;
          return FINAL;
        } else if (c == '*' || c == '/') {
          lastType = Token.OPMUL;
          return FINAL;

        } else if (c == '=') {
          lastType = Token.OPAS;
          return 1;
        } else if (c == '<' || c == '>') {
          lastType = Token.OPREL;
          return 1;
        } else if (c == '!') {
          lastType = Token.OPREL;
          return 3;
        } else if (c == '+' || c == '-') {
          lastType = Token.OPAS;
          return 11;
        } else if (c == '*' || c == '/') {
          lastType = Token.OPMUL;
        }

        else if (c == 'c')
          return 5;
        else if (c == 'f')
          return 10;
        else if (c == 'i')
          return 13;
        else if (c == 'e')
          return 21;
        else if (c == 'p')
          return 26;
        else {
          return SKIP;
        }

        // Symbols
      case 1:
        if (c == '=') {
          lastType = Token.OPREL;
          return FINAL;
        }
        file.seek(-1);
        return FINAL;
      case 3:
        if (c == '=') {
          return FINAL;
        }
        file.seek(-1);
        return ERROR;

      // Reserved words
      case 5:
        if (c == 'l')
          return 6;
        if (belongsToID(c))
          return 31;
      case 6:
        if (c == 'a')
          return 7;
        if (belongsToID(c))
          return 31;
      case 7:
        if (c == 's')
          return 8;
        if (belongsToID(c))
          return 31;
      case 8:
        if (c == 's')
          return 9;
        if (belongsToID(c))
          return 31;
      case 9:
        lastType = Token.CLASS;
        return FINAL;
      case 10:
        if (c == 'u')
          return 11;
        else if (c == 'l')
          return 16;
        else if (c == 'i')
          return 25;
        if (belongsToID(c))
          return 31;
      case 11:
        if (c == 'n')
          return 12;
        if (belongsToID(c))
          return 31;
      case 12:
        lastType = Token.FUN;
        return FINAL;
      case 13:
        if (c == 'n')
          return 14;
        if (c == 'f')
          return 20;
        if (belongsToID(c))
          return 31;
      case 14:
        if (c == 't')
          return 15;
        if (belongsToID(c))
          return 31;
      case 15:
        lastType = Token.INT;
        return FINAL;
      case 16:
        if (c == 'o')
          return 17;
        if (belongsToID(c))
          return 31;
      case 17:
        if (c == 'a')
          return 18;
        if (belongsToID(c))
          return 31;
      case 18:
        if (c == 't')
          return 19;
        if (belongsToID(c))
          return 31;
      case 19:
        lastType = Token.FLOAT;
        return FINAL;
      case 20:
        lastType = Token.IF;
        return FINAL;
      case 21:
        if (c == 'l')
          return 22;
        if (belongsToID(c))
          return 31;
      case 22:
        if (c == 's')
          return 23;
        if (belongsToID(c))
          return 31;
      case 23:
        if (c == 'e')
          return 24;
        if (belongsToID(c))
          return 31;
      case 24:
        lastType = Token.ELSE;
        return FINAL;
      case 25:
        lastType = Token.FI;
        return FINAL;
      case 26:
        if (c == 'r')
          return 27;
        if (belongsToID(c))
          return 31;
      case 27:
        if (c == 'i')
          return 28;
        if (belongsToID(c))
          return 31;
      case 28:
        if (c == 'n')
          return 29;
        if (belongsToID(c))
          return 31;
      case 29:
        if (c == 't')
          return 30;
        if (belongsToID(c))
          return 31;
      case 30:
        lastType = Token.PRINT;
        return FINAL;

      // Special cases
      case 31:
        // id code

      default:
        return SKIP;
    }
  }

  private boolean belongsToID(char c) {
    return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9'));
  }
}
