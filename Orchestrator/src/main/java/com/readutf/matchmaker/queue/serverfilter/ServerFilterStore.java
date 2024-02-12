package com.readutf.matchmaker.queue.serverfilter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class ServerFilterStore {

    private final static Gson gson = new Gson();
    private static final Logger logger = LoggerFactory.getLogger(ServerFilterStore.class);

    private final File filtersFile;

    @SneakyThrows
    public ServerFilterStore(File baseDir) {
        this.filtersFile = new File(baseDir, "filters.json");
        if(!filtersFile.exists() && filtersFile.createNewFile()) logger.info("filters file created");
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
