package net.fehmicansaglam.elasticsearch.repl;

import java.io.IOException;
import java.io.PrintWriter;

import jline.console.ConsoleReader;

public abstract class AbstractConsole {

    private ConsoleReader reader;

    protected abstract Interpreter interpreter();

    protected void updatePrompt() {
        reader.setPrompt(interpreter().prompt());
    }

    public void start() throws IOException {
        reader = new ConsoleReader();
        reader.setPrompt(interpreter().prompt());
        PrintWriter out = new PrintWriter(reader.getOutput());

        String line;
        loop:
        while ((line = reader.readLine()) != null) {
            switch (line) {
                case "quit":
                case "q":
                case "exit":
                    break loop;
                case "clear":
                case "cl":
                    // Because of a problem with SBT.
                    reader.setPrompt("");
                    reader.clearScreen();
                    reader.setPrompt(interpreter().prompt());
                    break;
                default:
                    out.println(interpreter().interpret(line));
                    out.flush();
            }
        }

        out.println("See you soon!");
        out.flush();
        reader.shutdown();
    }

}
