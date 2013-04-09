/**
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.informant.local.ui;

import java.io.IOException;
import java.sql.SQLException;

import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.informant.config.ConfigService;
import io.informant.local.store.DataSource;
import io.informant.local.store.SnapshotDao;
import io.informant.markers.OnlyUsedByTests;
import io.informant.markers.Singleton;
import io.informant.snapshot.SnapshotTraceSink;
import io.informant.trace.TraceRegistry;

/**
 * Json service for various admin tasks.
 * 
 * @author Trask Stalnaker
 * @since 0.5
 */
@Singleton
@JsonService
class AdminJsonService {

    private static final Logger logger = LoggerFactory.getLogger(AdminJsonService.class);

    private final SnapshotDao snapshotDao;
    private final ConfigService configService;
    private final SnapshotTraceSink snapshotTraceSink;
    private final DataSource dataSource;
    private final TraceRegistry traceRegistry;

    AdminJsonService(SnapshotDao snapshotDao, ConfigService configService,
            SnapshotTraceSink snapshotTraceSink, DataSource dataSource,
            TraceRegistry traceRegistry) {
        this.snapshotDao = snapshotDao;
        this.configService = configService;
        this.snapshotTraceSink = snapshotTraceSink;
        this.dataSource = dataSource;
        this.traceRegistry = traceRegistry;
    }

    @JsonServiceMethod
    void compactData() {
        logger.debug("compactData()");
        try {
            dataSource.compact();
        } catch (SQLException e) {
            // this might be serious, worth logging as error
            logger.error(e.getMessage(), e);
        }
    }

    @JsonServiceMethod
    void deleteAllData() {
        logger.debug("deleteAllData()");
        snapshotDao.deleteAllSnapshots();
    }

    @OnlyUsedByTests
    @JsonServiceMethod
    void resetAllConfig() throws IOException {
        logger.debug("resetAllConfig()");
        configService.resetAllConfig();
    }

    @OnlyUsedByTests
    @JsonServiceMethod
    String getNumPendingCompleteTraces() {
        logger.debug("getNumPendingCompleteTraces()");
        return Integer.toString(snapshotTraceSink.getPendingCompleteTraces().size());
    }

    @OnlyUsedByTests
    @JsonServiceMethod
    String getNumStoredSnapshots() {
        logger.debug("getNumStoredSnapshots()");
        return Long.toString(snapshotDao.count());
    }

    @OnlyUsedByTests
    @JsonServiceMethod
    String getNumActiveTraces() {
        logger.debug("getNumActiveTraces()");
        return Integer.toString(Iterables.size(traceRegistry.getTraces()));
    }
}
