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
        if (c == '(')
          lastType = Token.PARI;
        else if (c == ')')
          lastType = Token.PARD;
        else if (c == ':')
          lastType = Token.DOSP;
        else if (c == '{')
          lastType = Token.LBRA;
        else if (c == '}')
          lastType = Token.RBRA;
        else if (c == '=') {
          return 1;
        } else if (c == ';')
          lastType = Token.PYC;
        // TODO: keep adding cases
        else
          return SKIP;
        return FINAL;
      case 1:
        if (c == '=') {
          lastType = Token.OPREL;
          return 3;
        }
        lastType = Token.ASIG;
        return 2;

      case 2:
      case 3:
        file.seek(-1);
        return FINAL;
      default:
        break;
    }
    return 0;
  }
}
