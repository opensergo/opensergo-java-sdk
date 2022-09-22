/*
 * Copyright 2022, OpenSergo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.opensergo.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eric Zhao
 */
public class OpenSergoLogger {

    private static Logger logger;

    static {
        try {
            logger = LoggerFactory.getLogger("openSergoLogger");
        } catch (Throwable t) {
            System.err.println("Failed to initialize OpenSergo logger");
            t.printStackTrace();
        }
    }

    public static void info(String format, Object... arguments) {
        if (logger == null) {
            return;
        }
        logger.info(format, arguments);
    }

    public static void info(String msg, Throwable e) {
        if (logger == null) {
            return;
        }
        logger.info(msg, e);
    }

    public static void warn(String format, Object... arguments) {
        if (logger == null) {
            return;
        }
        logger.warn(format, arguments);
    }

    public static void warn(String msg, Throwable e) {
        if (logger == null) {
            return;
        }
        logger.warn(msg, e);
    }

    public static void trace(String format, Object... arguments) {
        if (logger == null) {
            return;
        }
        logger.trace(format, arguments);
    }

    public static void trace(String msg, Throwable e) {
        if (logger == null) {
            return;
        }
        logger.trace(msg, e);
    }

    public static void debug(String format, Object... arguments) {
        if (logger == null) {
            return;
        }
        logger.debug(format, arguments);
    }

    public static void debug(String msg, Throwable e) {
        if (logger == null) {
            return;
        }
        logger.debug(msg, e);
    }

    public static void error(String format, Object... arguments) {
        if (logger == null) {
            return;
        }
        logger.error(format, arguments);
    }

    public static void error(String msg, Throwable e) {
        if (logger == null) {
            return;
        }
        logger.error(msg, e);
    }

    private OpenSergoLogger() {}
}
