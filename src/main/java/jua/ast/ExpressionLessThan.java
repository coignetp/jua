package jua.ast;

import jua.evaluator.LuaRuntimeException;
import jua.evaluator.Scope;
import jua.objects.LuaBoolean;
import jua.objects.LuaNumber;
import jua.objects.LuaObject;
import jua.objects.LuaString;
import jua.token.TokenOperator;

public class ExpressionLessThan extends ExpressionBinary {
  ExpressionLessThan(TokenOperator token, Expression lhs, Expression rhs) {
    super(token, lhs, rhs);
  }

  @Override
  public LuaBoolean evaluate(Scope scope) throws LuaRuntimeException {
    LuaObject o1 = lhs.evaluate(scope);
    LuaObject o2 = rhs.evaluate(scope);
    LuaObject.ensureSameType(o1, o2);

    if (o1 instanceof LuaNumber) {
      return LuaBoolean.getLuaBool(((LuaNumber) o1).getValue() < ((LuaNumber) o2).getValue());
    }

    if (o1 instanceof LuaString) {
      boolean val = ((LuaString) o1).getValue().compareTo(((LuaString) o2).getValue()) < 0;
      return LuaBoolean.getLuaBool(val);
    }

    throw new LuaRuntimeException(String.format("Could not evaluate %s < %s", o1, o2));
  }
}
