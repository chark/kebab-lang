package kebab;

import kebab.lang.engine.KebabEngine;

public class Main {

    public static void main(String... args) {

        try {
            KebabEngine
                    .file("src/main/resources/main.kebab", "arg")
                    .run();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}