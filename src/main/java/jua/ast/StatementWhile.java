package jua.ast;

import jua.evaluator.LuaRuntimeException;
import jua.evaluator.Scope;
import jua.objects.LuaBoolean;
import jua.objects.LuaNil;
import jua.objects.LuaObject;
import jua.objects.LuaReturn;
import jua.token.Token;

public class StatementWhile extends Statement {

  private Expression condition;
  private Statement consequence;

  public StatementWhile(Token token, Expression condition, Statement consequence) {
    super(token);
    this.condition = condition;
    this.consequence = consequence;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    StatementWhile that = (StatementWhile) o;

    if (!condition.equals(that.condition)) return false;
    return consequence.equals(that.consequence);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + condition.hashCode();
    result = 31 * result + consequence.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return String.format("while %s do\n%s end", condition, consequence);
  }

  public Expression getCondition() {
    return condition;
  }

  public Statement getConsequence() {
    return consequence;
  }

  @Override
  public LuaObject evaluate(Scope scope) throws LuaRuntimeException {
    Scope whileScope = scope.createChild();

    LuaObject ret = LuaNil.getInstance();
    while (LuaBoolean.valueOf(condition.evaluate(whileScope)).getValue()) {
      ret = consequence.evaluate(whileScope.createChild());

      if (ret instanceof LuaReturn) {
        return ((LuaReturn) ret).getValue();
      }
    }
    return ret;
  }
}
