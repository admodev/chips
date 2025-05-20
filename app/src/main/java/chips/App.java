package chips;

import java.util.ArrayList;
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

          options = new ArrayList<>();
          params.put(arg.substring(1), options);
        } else if (options != null) {
          options.add(arg);
        } else {
          logger.error("Error occurred while processing argument ", new Error("Fatal error."));
          return;
        }
      }
    } catch (Exception e) {
      logger.error("A fatal error has occurred ", e);
    }
  }
}
