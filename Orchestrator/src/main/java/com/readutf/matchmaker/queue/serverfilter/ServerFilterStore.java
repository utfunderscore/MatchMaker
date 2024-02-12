package com.readutf.matchmaker.queue.serverfilter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class ServerFilterStore {

    private @Getter static Gson gson = new Gson();

    private final File filtersFile;

    @SneakyThrows
    public ServerFilterStore(File baseDir) {
        this.filtersFile = new File(baseDir, "filters.json");
        if(!filtersFile.exists() && filtersFile.createNewFile()) System.out.println("filters file created");
    }

    @SneakyThrows
    public Map<String, ServerFilterData> loadAll() {
        FileReader fileReader = new FileReader(filtersFile);
        Map<String, ServerFilterData> filterData = gson.fromJson(fileReader, new TypeToken<>(){}.getType());
        fileReader.close();

        if (filterData == null) filterData = new HashMap<>();
        return filterData;
    }

    @SneakyThrows
    public void saveFilters(Map<String, ServerFilterData> filters) {
        FileWriter fileWriter = new FileWriter(filtersFile);
        gson.toJson(filters, fileWriter);
        fileWriter.close();
    }

}
