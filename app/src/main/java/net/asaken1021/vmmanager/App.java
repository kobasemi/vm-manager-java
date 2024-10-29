package net.asaken1021.vmmanager;

import java.util.HashMap;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        Map<String, String> argsMap = parseArgument(args);
        String uri = "qemu:///system";

        if (argsMap.containsKey("uri")) {
            if (argsMap.get("uri").equals("")) {
                printError("--uri オプションが渡されましたが，URIの指定がありません．デフォルトを使用します");
            } else {
                uri = argsMap.get("uri");
            }
        }

        if (argsMap.containsKey("web-api")) {
            new WebApiApp(uri).run();
        } else {
            new CliApp(uri).run();
        }
    }

    private static Map<String, String> parseArgument(String[] args) {
        Map<String, String> parsedArgs = new HashMap<String, String>();

        String parsedValueDest = "";

        for (String arg : args) {
            switch (arg) {
                case "--uri":
                    parsedArgs.put("uri", "");
                    parsedValueDest = "uri";
                    break;
                case "--web-api":
                    parsedArgs.put("web-api", "");
                    break;
                default:
                    if (!parsedValueDest.isEmpty()) {
                        parsedArgs.put(parsedValueDest, arg);
                        parsedValueDest = "";
                    }
                    break;
            }
        }

        return parsedArgs;
    }

    private static void printError(String message) {
        System.err.println("エラー: " + message);
    }
}
