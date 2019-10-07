package jua.ast;

import java.util.stream.Collectors;
import jua.evaluator.LuaRuntimeException;
import jua.evaluator.Scope;
import jua.objects.LuaNil;
import jua.objects.LuaObject;
import jua.token.Token;

public class StatementFunction extends Statement {
  private ExpressionIdentifier name;
  private ExpressionFunction func;

  public StatementFunction(Token token, ExpressionIdentifier name, ExpressionFunction func) {
    super(token);
    this.name = name;
    this.func = func;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    StatementFunction that = (StatementFunction) o;

    if (!name.equals(that.name)) return false;
    return func.equals(that.func);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + name.hashCode();
    result = 31 * result + func.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return String.format(
        "function %s(%s)\n%s\nend",
        name,
        func.getArgs().stream().map(Object::toString).collect(Collectors.joining("\n")),
        util.Util.indent(func.getStatements().toString()));
  }

  @Override
  public LuaObject evaluate(Scope scope) throws LuaRuntimeException {
    scope.assignGlobal(name.getIdentifier(), func.evaluate(scope));

    return LuaNil.getInstance();
  }
}
