package net.fehmicansaglam.elasticsearch.repl;

public interface Interpreter {

    public String interpret(String input);

    public String prompt();
}
