package pl.emmajk;

/** Placeholder entry point, to be replaced by the scoreboard. */
public final class HelloWorld {

    private HelloWorld() {
    }

    /** Returns the greeting text. */
    public static String greeting() {
        return "Hello, World!";
    }

    /** Prints the greeting to standard output. */
    public static void main(String[] args) {
        System.out.println(greeting());
    }
}
