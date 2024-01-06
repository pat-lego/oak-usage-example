package com.adobe.support.aem.oak;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    static Logger logger = LoggerFactory.getLogger(Main.class);

    private static final String STORE = "STORE";

    public static void main(String[] args) {
        logger.info("Initializing main method");
        logger.info("Received {} args from the command line", args.length);

        Map<String, String> argsMap = parseParams(args);
        Optional<String> storeType = Optional.ofNullable(argsMap.get(STORE));

        if (storeType.isPresent()) {
            logger.info("Parameter map is set to {}", argsMap.toString());
            switch(storeType.get()) {
                case StoreTypes.FILE: 
                    new FileStore().create();
                    break;
                case StoreTypes.MEMORY:
                    new MemoryStore().create();
                    break;
                default:
                    logger.info("Invalid {} type provided not using any stores", STORE);
                    break;
            }
        } else {
            logger.error("Failed to provide a {} type in the command line args shutting down", STORE);
            System.exit(1);
        }

        logger.info("Closing main method");
        System.exit(0);
    }

    public static Map<String, String> parseParams(String[] args) {
        Map<String, String> argsMap = new HashMap<String, String>();
        List<String> argsList = Arrays.asList(args);
        
        argsList.stream().map(arg -> arg.split("=")).forEach(entry -> argsMap.put(entry[0].toUpperCase(), entry[1].toUpperCase()));
        return argsMap;
    }

   
}
