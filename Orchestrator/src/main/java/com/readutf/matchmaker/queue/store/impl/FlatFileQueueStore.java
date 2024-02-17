package com.readutf.matchmaker.queue.store.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.readutf.matchmaker.queue.Queue;
import com.readutf.matchmaker.queue.store.QueueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FlatFileQueueStore implements QueueStore {

    private static final Logger logger = LoggerFactory.getLogger(FlatFileQueueStore.class);
    private static final Gson gson = new Gson();

    private final File queueStoreFile;

    public FlatFileQueueStore(File baseDir) throws IOException {
        this.queueStoreFile = new File(baseDir, "queues.json");
        if (!queueStoreFile.exists() && queueStoreFile.createNewFile()) logger.info("Created queue store file");
    }

    @Override
    public void saveQueues(Collection<Queue> queues) {
        try {
            FileWriter fileWriter = new FileWriter(queueStoreFile, false);
            gson.toJson(queues, fileWriter);
            fileWriter.flush();
        } catch (IOException e) {
            logger.error("Failed to save queues", e);
        }
    }

    @Override
    public List<Queue> loadQueues() {
        try {
            FileReader fileReader = new FileReader(queueStoreFile);
            List<Queue> queues = gson.fromJson(fileReader, new TypeToken<>() {});
            return queues == null ? new ArrayList<>() : queues;
        } catch (FileNotFoundException e) {
            logger.error("Failed to load queues", e);
            return new ArrayList<>();
        }
    }
}
