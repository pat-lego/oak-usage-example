package com.adobe.support.aem.oak;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.segment.SegmentNodeStore;
import org.apache.jackrabbit.oak.segment.SegmentNodeStoreBuilders;
import org.apache.jackrabbit.oak.segment.file.InvalidFileStoreVersionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryStore implements Store {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void create() {
        Optional<org.apache.jackrabbit.oak.segment.memory.MemoryStore> ms = null;
        Optional<Repository> repo = null;

        try {
            ms = Optional.ofNullable(buildMemoryStore());
            repo = Optional.ofNullable(buildSegmentNodeStore(ms.get()));
        } catch (InvalidFileStoreVersionException | IOException e) {
            logger.error("Failed to build the segment nodestore", e);
        }

        if (repo.isPresent()) {
            String nodeName = "hello";
            try {
                createNode(nodeName, repo.get());
                logger.info("Successfully created node {}", nodeName);
            } catch (RepositoryException e) {
                logger.error("Failed to create node {}", nodeName, e);
            }
        }
        logger.info("Completed the Oak Setup terminating process");
    }

    public org.apache.jackrabbit.oak.segment.memory.MemoryStore buildMemoryStore()
            throws InvalidFileStoreVersionException, IOException {
        org.apache.jackrabbit.oak.segment.memory.MemoryStore ms = new org.apache.jackrabbit.oak.segment.memory.MemoryStore();
        return ms;
    }

    public Repository buildSegmentNodeStore(org.apache.jackrabbit.oak.segment.memory.MemoryStore ms)
            throws InvalidFileStoreVersionException, IOException {
        SegmentNodeStore ns = SegmentNodeStoreBuilders.builder(ms).build();
        Repository repo = new Jcr(new Oak(ns)).createRepository();

        return repo;
    }

    @Override
    public String getType() {
        return StoreTypes.MEMORY;
    }

}
