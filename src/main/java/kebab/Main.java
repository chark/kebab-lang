package kebab;

import kebab.lang.engine.KebabEngine;

public class Main {

    public static void main(String... args) {

        String file;
        if (args.length > 0) {
            file = args[0];
        } else {
            System.out.println("java -jar kebab-lang.jar <path_to_source_file>");
            return;
        }

        try {
            KebabEngine
                    .file(file)
                    .run();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}