package com.readutf.matchmaker.queue.serverfilter;

public class InbuiltFilters {

    public static ServerFilterCreator getCategoryFilter() {
        return args -> {
            if(args.length != 1) throw new IllegalArgumentException("Invalid arguments");
            String category = args[0];

            return server -> {
                System.out.println("category: " + server.getCategory());
                System.out.println("category: " + category);
                return server.getCategory().equalsIgnoreCase(category);
            };
        };
    }

}
