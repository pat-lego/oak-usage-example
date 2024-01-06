package com.adobe.support.aem.oak;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.segment.SegmentNodeStore;
import org.apache.jackrabbit.oak.segment.SegmentNodeStoreBuilders;
import org.apache.jackrabbit.oak.segment.SegmentStore;
import org.apache.jackrabbit.oak.segment.file.FileStoreBuilder;
import org.apache.jackrabbit.oak.segment.file.InvalidFileStoreVersionException;

public class FileStore implements Store {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void create() {
        Optional<org.apache.jackrabbit.oak.segment.file.FileStore> fs = null;
        Optional<Repository> repo = null;

        try {
            fs = Optional.ofNullable(buildFileStore());
            repo = Optional.ofNullable(buildSegmentNodeStore(fs.get()));
        } catch (InvalidFileStoreVersionException | IOException e) {
            logger.error("Failed to build the segment nodestore", e);
        }

        if (repo.isPresent()) {
            String nodeName = "hello";
            try {
                Long count = createNode(nodeName, repo.get());

                logger.info("Successfully created node {} with value {}", nodeName, count);
            } catch (RepositoryException e) {
                logger.error("Failed to create node {}", nodeName, e);
            }
        }
        fs.get().close();
        logger.info("Completed the Oak Setup terminating process");

    }

    public org.apache.jackrabbit.oak.segment.file.FileStore buildFileStore() throws InvalidFileStoreVersionException, IOException {
        org.apache.jackrabbit.oak.segment.file.FileStore fs = FileStoreBuilder.fileStoreBuilder(new File("repository")).build();
        return fs;
    }

    public Repository buildSegmentNodeStore(SegmentStore fs)
            throws InvalidFileStoreVersionException, IOException {
        SegmentNodeStore ns = SegmentNodeStoreBuilders.builder((org.apache.jackrabbit.oak.segment.file.FileStore) fs).build();
        Repository repo = new Jcr(new Oak(ns)).createRepository();

        return repo;
    }

    @Override
    public String getType() {
        return StoreTypes.FILE;
    }

}
