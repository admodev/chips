package chips;

import java.lang.Exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chips.shared.utils.Logger;

public class App {
  public static void main(String[] args) {
    final Logger logger = Logger.getLogger(App.class);

    try {
      final Map<String, List<String>> params = new HashMap();
      List<String> options = null;

      for (int i = 0; i < args.length; i++) {
        final String arg = args[i];

        if (arg.charAt(0) == '-') {
          if (arg.length() < 2) {
            logger.error("Error occurred while processing argument ", new Error(arg));

            return;
          }
        }
      }
    } catch (Exception e) {
      logger.error("A fatal error has occurred ", e);
    }
  }
}
