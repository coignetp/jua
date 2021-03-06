package jua.objects;

import java.util.ArrayList;
import jua.ast.Expression;
import jua.ast.StatementList;
import jua.evaluator.IllegalCastException;
import jua.evaluator.LuaRuntimeException;
import jua.evaluator.Scope;
import jua.token.TokenIdentifier;

public class LuaFunction implements LuaObject, Function {
  private ArrayList<String> argNames;
  private Scope environment;
  private StatementList block;
  private LuaTable self = null;
  private boolean variadic;

  public LuaFunction(ArrayList<String> argNames, Scope environment, StatementList block) {
    if (argNames != null
        && argNames.size() > 0
        && argNames.get(argNames.size() - 1) == TokenIdentifier.VariadicToken) {
      this.variadic = true;
      argNames.remove(argNames.size() - 1);
    }
    this.argNames = argNames;
    this.environment = environment;
    this.block = block;
  }

  public static LuaFunction valueOf(LuaObject o) throws IllegalCastException {
    if (o instanceof LuaFunction) {
      return (LuaFunction) o;
    }
    throw new IllegalCastException(String.format("%s is not a function", o.repr()));
  }

  @Override
  public String repr() {
    return String.format("function(%s) %s", argNames, block);
  }

  @Override
  public String getTypeName() {
    return "function";
  }

  public LuaObject evaluateUnwrap(Scope scope, ArrayList<Expression> args)
      throws LuaRuntimeException {
    LuaReturn ret = evaluate(scope, args);

    // only unwrap a LuaReturn if we reach a function call
    return ret.getValues().get(0);
  }

  public LuaReturn evaluate(Scope scope, ArrayList<Expression> args) throws LuaRuntimeException {
    // Evaluate each arg
    return evaluate(util.Util.evaluateExprs(scope, args));
  }

  public LuaReturn evaluate(ArrayList<LuaObject> args) throws LuaRuntimeException {
    Scope funcScope = this.environment.createChild();

    if (self != null) {
      funcScope.assignSelf(self);
    }

    // Init args to nil
    argNames.forEach(arg -> funcScope.assignLocal(arg, LuaNil.getInstance()));

    // Assign evaluated args to arg names
    for (int i = 0; i < Math.min(argNames.size(), args.size()); i++) {
      funcScope.assignLocal(argNames.get(i), args.get(i));
    }
    if (this.variadic) {
      LuaTable vararg = new LuaTable();
      for (int i = Math.min(argNames.size(), args.size()); i < args.size(); i++) {
        vararg.insertList(args.get(i));
      }
      funcScope.assignLocal(TokenIdentifier.VariadicToken, vararg);
    }

    LuaObject ret = block.evaluate(funcScope);
    if (ret instanceof LuaReturn) {
      return (LuaReturn) ret;
    }

    return new LuaReturn(ret);
  }

  public Scope getEnvironment() {
    return environment;
  }

  public void setSelf(LuaTable self) {
    this.self = self;

    if (!this.argNames.get(0).equals(Scope.SELF)) {
      this.argNames.add(0, Scope.SELF);
    }
  }
}
