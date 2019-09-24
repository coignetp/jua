package lexer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;

public class Lexer {

  static int readLimit = 50000000;
  private BufferedInputStream in;
  private int currentLine;
  private int position;

  public Lexer(InputStream in) {
    this.in = new BufferedInputStream(in);
  }

  private char readChar() throws IOException {
    int nextByte = in.read();
    if (nextByte == -1) {
      return 0;
    }

    return (char) nextByte;
  }

  private void consumeWhitespace() throws IOException {
      char ch = readChar();
      while (ch == ' ' || ch == '\t' || ch == '\n') {
          if (ch == '\n') {
              position = 0;
              currentLine++;
          }
          in.mark(readLimit);
      }
      in.reset();
  }

  public Token nextToken() throws IOException {
    var token = new Token(currentLine, position, "");

    char ch = readChar();
    switch (ch) {
      case '=':
      case '+':
      case '-':
      case '*':
      case '/':
      case '<':
      case '>':
      case '(':
      case ')':
      case '[':
      case ']':
      case '{':
      case '}':
      case ',':
      case ';':
      case '%':
      case '^':
      case '#':
      case ':':
      case '.':
        token.setLitteral(String.valueOf((ch)));
        break;
      default:
        if (Character.isLetter(ch)) {
          in.reset();
          String identifier = nextIdentifier();
          token.setLitteral(identifier);
        }
    }

    consumeWhitespace();

    return token;
  }

  public String nextIdentifier() throws IOException {
    String identifier = new String();
    char ch = readChar();
    while (Character.isLetter(ch)) {
      in.mark(readLimit);
      identifier += Character.toString(ch);
      ch = readChar();
    }
    in.reset();

    return identifier;
  }
}