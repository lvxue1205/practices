import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.CoreConstants;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


@LogMessageMask(key = "user", mask = "*****")
@LogMessageMask(pattern = "\\w+", mask = "lo")
public class LogMessageMasksConverterTest {

  @Test
  @LogMessageMask(key = "username", pattern = "[\\w-]+", mask = "wa wa wa")
  public void testConvert() {
    ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<ILoggingEvent>();

    String converter = LogMessageMasksConverter.class.getName();

    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    Map<String, String> map = (Map<String, String>) loggerContext
        .getObject(CoreConstants.PATTERN_RULE_REGISTRY);

    if (map == null) {
      map = new HashMap<>();
      loggerContext.putObject(CoreConstants.PATTERN_RULE_REGISTRY, map);
    }
    map.put("message", converter);

    PatternLayoutEncoder ple = new PatternLayoutEncoder();
    ple.setPattern("%-20(%d{HH:mm:ss.SSS} [%thread]) %level %logger{32} - %message\n");

    ple.setContext(loggerContext);
    ple.start();

    appender.setEncoder(ple);
    appender.setContext(loggerContext);
    appender.start();

    Logger logger = (Logger) LoggerFactory.getLogger(LogMessageMasksConverterTest.class);
    logger.addAppender(appender);
    logger.setAdditive(false);

    logger.info("Hello user:12345 !, userId=33333");

  }

  @Test
  public void test() {
    String message = "Hello user:12345 !";
    String pattern = Pattern.compile("(?<=user\\s?:?\\s?)[\\w-]+").pattern();
    message = message.replaceAll(pattern, "*****");

    System.out.println(message);
  }
}
