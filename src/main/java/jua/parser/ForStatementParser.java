package jua.parser;

import java.util.ArrayList;
import jua.ast.*;
import jua.token.Keyword;
import jua.token.Literal;
import jua.token.Token;
import jua.token.TokenFactory;

public class ForStatementParser implements StatementParser {
  @Override
  public Statement parse(Parser parser) throws IllegalParseException {

    // consume FOR keyword
    Token tok = parser.currentToken();
    parser.consume(Keyword.FOR);

    if (AssignmentStatementParser.isAssignmentStatement(parser)) {
      return parseNumericForStatement(parser, tok);
    }

    return parseGenericForStatement(parser, tok);
  }

  private StatementFor parseNumericForStatement(Parser parser, Token tok)
      throws IllegalParseException {
    if (!AssignmentStatementParser.isAssignmentStatement(parser)) {
      throw new IllegalParseException("expected assignment in for loop");
    }
    StatementAssignment assignment = AssignmentStatementParser.parseAssignment(parser);
    ExpressionIdentifier variable = (ExpressionIdentifier) assignment.getLhs().get(0);
    Expression var = assignment.getRhs().get(0);
    Expression limit;
    Expression step;

    if (assignment.getRhs().size() > 1) {
      limit = assignment.getRhs().get(1);
    } else {
      throw new IllegalParseException("expected limit in for loop");
    }

    if (assignment.getRhs().size() > 2) {
      step = assignment.getRhs().get(2);
    } else {
      step =
          ExpressionFactory.create(
              TokenFactory.create(Literal.NUMBER, "1", tok.getLine(), tok.getPosition()));
    }

    BlockStatementParser blockParser = new BlockStatementParser();

    Statement block = blockParser.parse(parser);

    return new StatementNumericFor(tok, variable, var, limit, step, block);
  }

  private StatementFor parseGenericForStatement(Parser parser, Token tok)
      throws IllegalParseException {
    ArrayList<ExpressionIdentifier> variables = parser.parseCommaSeparatedExpressions(0);

    // Check if we are on equal jua.token
    parser.consume(Keyword.IN);

    ArrayList<Expression> expressionsList = parser.parseCommaSeparatedExpressions(0);

    BlockStatementParser blockParser = new BlockStatementParser();

    Statement block = blockParser.parse(parser);

    return new StatementGenericFor(tok, variables, expressionsList, block);
  }

  @Override
  public boolean matches(Parser parser) {
    return parser.currentToken().isSubtype(Keyword.FOR);
  }
}
