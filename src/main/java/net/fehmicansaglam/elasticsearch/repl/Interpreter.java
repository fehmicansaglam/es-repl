package net.fehmicansaglam.elasticsearch.repl;

public interface Interpreter {

    String interpret(String input);

    String prompt();

    void shutdown();
}
