package jua.ast;

import java.util.Objects;
import jua.evaluator.LuaRuntimeException;
import jua.evaluator.Scope;
import jua.objects.*;
import jua.token.Token;

public class StatementNumericFor extends StatementFor {

  Expression var;
  Expression limit;
  Expression step;

  public StatementNumericFor(
      Token token,
      ExpressionIdentifier variable,
      Expression var,
      Expression limit,
      Expression step,
      Statement block) {

    super(token, util.Util.createArrayList(variable), block);
    this.var = var;
    this.limit = limit;
    this.step = step;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    StatementNumericFor that = (StatementNumericFor) o;
    return Objects.equals(var, that.var)
        && Objects.equals(limit, that.limit)
        && Objects.equals(step, that.step);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), var, limit, step);
  }

  @Override
  public String toString() {
    return String.format(
        "for %s = %s, %s, %s do\n %s\nend", variables.get(0), var, limit, step, block);
  }

  @Override
  public LuaObject evaluate(Scope scope) throws LuaRuntimeException {
    Scope forScope = scope.createChild();

    LuaNumber varValue = LuaNumber.valueOf(var.evaluate(forScope));
    LuaNumber limitValue = LuaNumber.valueOf(limit.evaluate(forScope));
    LuaNumber stepValue = LuaNumber.valueOf(step.evaluate(forScope));

    LuaObject ret = LuaNil.getInstance();

    while ((stepValue.getValue() > 0 && varValue.getValue() <= limitValue.getValue())
        || (stepValue.getValue() <= 0 && varValue.getValue() >= limitValue.getValue())) {

      forScope.assignLocal(variables.get(0).getIdentifier(), varValue);
      ret = block.evaluate(forScope);
      varValue = new LuaNumber(varValue.getValue() + stepValue.getValue());

      if (ret instanceof LuaReturn) {
        return ret;
      }
      if (ret instanceof LuaBreak) {
        return LuaNil.getInstance();
      }
    }

    return ret;
  }
}
