package jua.parser;

import jua.ast.Expression;
import jua.ast.ExpressionFactory;
import jua.token.Token;
import jua.token.TokenOperator;

public class OperatorParser implements PrefixParser, InfixParser {

  private final int precedence;

  public OperatorParser(int precedence) {
    this.precedence = precedence;
  }

  public int getPrecedence() {
    return precedence;
  }

  @Override
  public Expression parseInfix(Parser parser, Token tok, Expression lhs)
      throws IllegalParseException {
    Expression rhs = parser.parseExpression(precedence);
    return ExpressionFactory.create((TokenOperator) tok, lhs, rhs);
  }

  @Override
  public Expression parsePrefix(Parser parser, Token tok) throws IllegalParseException {
    Expression rhs = parser.parseExpression(precedence);
    return ExpressionFactory.create((TokenOperator) tok, rhs);
  }
}
