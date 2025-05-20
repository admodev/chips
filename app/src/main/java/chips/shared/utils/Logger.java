package chips.shared.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * A configurable logger utility that provides both console and file logging
 * capabilities.
 * This logger is designed to be easily imported and used across different
 * components of an application.
 * 
 * @author admodevops
 * @version 1.0
 */
public class Logger {
  private static final String DEFAULT_LOG_DIR = "logs";
  private static final String DEFAULT_LOG_FILE = "application.log";
  private static final Level DEFAULT_LEVEL = Level.INFO;

  private final java.util.logging.Logger logger;
  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

  /**
   * Log levels enum to simplify level usage
   */
  public enum LogLevel {
    SEVERE(Level.SEVERE),
    WARNING(Level.WARNING),
    INFO(Level.INFO),
    CONFIG(Level.CONFIG),
    FINE(Level.FINE),
    FINER(Level.FINER),
    FINEST(Level.FINEST);

    private final Level level;

    LogLevel(Level level) {
      this.level = level;
    }

    public Level getLevel() {
      return level;
    }
  }

  /**
   * Private constructor to enforce the singleton pattern per class name
   * 
   * @param name The logger name
   */
  private Logger(String name) {
    logger = java.util.logging.Logger.getLogger(name);
    setupDefaultHandlers();
  }

  /**
   * Creates a new Logger instance with the class name
   * 
   * @param clazz The class to create a logger for
   * @return A new Logger instance
   */
  public static Logger getLogger(Class<?> clazz) {
    return new Logger(clazz.getName());
  }

  /**
   * Creates a new Logger instance with a custom name
   * 
   * @param name The name of the logger
   * @return A new Logger instance
   */
  public static Logger getLogger(String name) {
    return new Logger(name);
  }

  /**
   * Sets up default handlers for both console and file logging
   */
  private void setupDefaultHandlers() {
    // Remove any existing handlers
    Arrays.stream(logger.getHandlers()).forEach(logger::removeHandler);

    // Configure the logger
    logger.setUseParentHandlers(false);
    logger.setLevel(DEFAULT_LEVEL);

    // Add console handler
    ConsoleHandler consoleHandler = new ConsoleHandler();
    consoleHandler.setFormatter(new CustomFormatter());
    consoleHandler.setLevel(DEFAULT_LEVEL);
    logger.addHandler(consoleHandler);

    // Add file handler
    try {
      createLogDirectoryIfNeeded();
      FileHandler fileHandler = new FileHandler(DEFAULT_LOG_DIR + File.separator + DEFAULT_LOG_FILE, true);
      fileHandler.setFormatter(new CustomFormatter());
      fileHandler.setLevel(DEFAULT_LEVEL);
      logger.addHandler(fileHandler);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to create file handler", e);
    }
  }

  /**
   * Sets the log level for this logger
   * 
   * @param level The log level to set
   */
  public void setLevel(LogLevel level) {
    logger.setLevel(level.getLevel());
    Arrays.stream(logger.getHandlers()).forEach(handler -> handler.setLevel(level.getLevel()));
  }

  /**
   * Creates the log directory if it doesn't exist
   */
  private void createLogDirectoryIfNeeded() throws IOException {
    Path logDir = Paths.get(DEFAULT_LOG_DIR);
    if (!Files.exists(logDir)) {
      Files.createDirectories(logDir);
    }
  }

  /**
   * Log a message at INFO level
   * 
   * @param message The message to log
   */
  public void info(String message) {
    logger.log(Level.INFO, message);
  }

  /**
   * Log a message at WARNING level
   * 
   * @param message The message to log
   */
  public void warn(String message) {
    logger.log(Level.WARNING, message);
  }

  /**
   * Log a message at SEVERE level
   * 
   * @param message The message to log
   */
  public void error(String message) {
    logger.log(Level.SEVERE, message);
  }

  /**
   * Log a message at SEVERE level with an exception
   * 
   * @param message   The message to log
   * @param throwable The exception to log
   */
  public void error(String message, Throwable throwable) {
    logger.log(Level.SEVERE, message, throwable);
  }

  /**
   * Log a message at DEBUG level (FINE)
   * 
   * @param message The message to log
   */
  public void debug(String message) {
    logger.log(Level.FINE, message);
  }

  /**
   * Log a message at TRACE level (FINEST)
   * 
   * @param message The message to log
   */
  public void trace(String message) {
    logger.log(Level.FINEST, message);
  }

  /**
   * Log a message at the specified level
   * 
   * @param level   The level to log at
   * @param message The message to log
   */
  public void log(LogLevel level, String message) {
    logger.log(level.getLevel(), message);
  }

  /**
   * Log a message at the specified level with an exception
   * 
   * @param level     The level to log at
   * @param message   The message to log
   * @param throwable The exception to log
   */
  public void log(LogLevel level, String message, Throwable throwable) {
    logger.log(level.getLevel(), message, throwable);
  }

  /**
   * Configure the logger to use a specific file
   * 
   * @param filePath The file path to log to
   * @return This logger instance
   */
  public Logger useLogFile(String filePath) {
    try {
      // Remove existing file handlers
      Arrays.stream(logger.getHandlers())
          .filter(handler -> handler instanceof FileHandler)
          .forEach(logger::removeHandler);

      // Create directory structure if needed
      Path path = Paths.get(filePath);
      Path parentDir = path.getParent();
      if (parentDir != null && !Files.exists(parentDir)) {
        Files.createDirectories(parentDir);
      }

      // Add new file handler
      FileHandler fileHandler = new FileHandler(filePath, true);
      fileHandler.setFormatter(new CustomFormatter());
      fileHandler.setLevel(logger.getLevel());
      logger.addHandler(fileHandler);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to configure log file: " + filePath, e);
    }
    return this;
  }

  /**
   * Enable or disable console logging
   * 
   * @param enabled True to enable console logging, false to disable
   * @return This logger instance
   */
  public Logger enableConsoleLogging(boolean enabled) {
    if (enabled) {
      // Add console handler if none exists
      if (Arrays.stream(logger.getHandlers()).noneMatch(handler -> handler instanceof ConsoleHandler)) {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new CustomFormatter());
        consoleHandler.setLevel(logger.getLevel());
        logger.addHandler(consoleHandler);
      }
    } else {
      // Remove console handlers
      Arrays.stream(logger.getHandlers())
          .filter(handler -> handler instanceof ConsoleHandler)
          .forEach(logger::removeHandler);
    }
    return this;
  }

  /**
   * Custom formatter for log messages
   */
  private static class CustomFormatter extends java.util.logging.Formatter {
    @Override
    public String format(LogRecord record) {
      LocalDateTime datetime = LocalDateTime.now();
      String timestamp = datetime.format(dateFormatter);

      StringBuilder sb = new StringBuilder();
      sb.append(timestamp)
          .append(" [")
          .append(record.getLevel())
          .append("] ")
          .append(record.getLoggerName())
          .append(": ")
          .append(record.getMessage())
          .append(System.lineSeparator());

      if (record.getThrown() != null) {
        sb.append(getStackTraceAsString(record.getThrown()));
      }

      return sb.toString();
    }

    private String getStackTraceAsString(Throwable throwable) {
      StringBuilder sb = new StringBuilder();
      sb.append(throwable.toString()).append(System.lineSeparator());

      for (StackTraceElement element : throwable.getStackTrace()) {
        sb.append("\tat ").append(element).append(System.lineSeparator());
      }

      if (throwable.getCause() != null) {
        sb.append("Caused by: ");
        sb.append(getStackTraceAsString(throwable.getCause()));
      }

      return sb.toString();
    }
  }
}
