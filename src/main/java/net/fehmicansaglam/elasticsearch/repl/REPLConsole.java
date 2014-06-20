package net.fehmicansaglam.elasticsearch.repl;

import java.io.PrintWriter;

import jline.console.ConsoleReader;

class REPLConsole {

    private static String remote = "idle";

    private static String prompt() {
        return "[elasticsearch:" + remote + "] $ ";
    }

    public static void start() throws Exception {
        ConsoleReader reader = new ConsoleReader();
        reader.setPrompt(prompt());
        PrintWriter out = new PrintWriter(reader.getOutput());

        String line;
        loop:
        while ((line = reader.readLine()) != null) {
            out.println("======>\"" + line + "\"");
            out.flush();

            switch (line) {
                case "quit":
                case "q":
                case "exit":
                case "bye":
                    break loop;
                case "clear":
                case "cl":
                    // Because of a problem with SBT.
                    reader.setPrompt("");
                    reader.clearScreen();
                    reader.setPrompt(prompt());
                    break;
            }
        }

        out.println("See you soon!");
        out.flush();
    }

}
