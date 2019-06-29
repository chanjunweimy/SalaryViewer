package cdit.util;

import org.slf4j.Logger;

public class LoggerHelper {
  public static final String METHOD_POST = "POST";
  public static final String METHOD_GET = "GET";

  public static void logMessageAtStartOfMethod(Logger logger, String method, String url,
      String controller) {
    logger.info("Executing %s Request on \"%s\" in %s.", method, url, controller);
  }

  public static void logMessageAtEndOfMethod(Logger logger, String method, String url,
      String controller) {
    logger.info("Successfully Executed %s Request on \"%s\" in %s.", method, url, controller);
  }
}
