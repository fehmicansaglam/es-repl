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

    protected String appendLine(String command, String line) {
        if(command == null) {
            command = line;
        } else {
            command += " " + line;
            reader.getHistory().replace(command);
        }

        return command;
    }

    public void start() throws IOException {
        reader = new ConsoleReader();
        reader.setPrompt(interpreter().prompt());
        PrintWriter out = new PrintWriter(reader.getOutput());

        String line, command = null;
        loop:
        while ((line = reader.readLine()) != null) {
            if (line.charAt(line.length() - 1) == '\\') {
                reader.setPrompt("> ");
                command = appendLine(command, line.substring(0, line.length() - 1));
                continue;
            } else {
                command = appendLine(command, line);
            }

            switch (command) {
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
                    out.println(interpreter().interpret(command));
                    out.flush();
            }

            command = null;
            reader.setPrompt(interpreter().prompt());
        }

        out.println("See you soon!");
        out.flush();
        reader.shutdown();
        interpreter().shutdown();
    }

}
