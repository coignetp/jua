package jua.objects.builtins;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import jua.ast.Expression;
import jua.evaluator.LuaRuntimeException;
import jua.evaluator.Scope;
import jua.objects.LuaFunction;
import jua.objects.LuaNil;
import jua.objects.LuaReturn;

public class Print extends LuaFunction {
  private final OutputStream out;

  public Print(Scope environment, OutputStream out) {
    super(null, environment, null);
    this.out = out;
  }

  public LuaReturn evaluate(Scope scope, ArrayList<Expression> args) throws LuaRuntimeException {
    for (Expression arg : args) {
      try {
        out.write(arg.evaluate(environment).repr().getBytes());
        out.write('\n');
      } catch (IOException e) {
        throw new LuaRuntimeException("could not write to output stream in `print` builtin");
      }
    }

    return new LuaReturn();
  }
}
