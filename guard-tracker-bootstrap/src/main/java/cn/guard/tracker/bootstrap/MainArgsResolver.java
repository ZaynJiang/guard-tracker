package cn.guard.tracker.bootstrap;
import cn.guard.tracker.bootstrap.bootstrap.core.utils.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MainArgsResolver {
    public Map<String, String> parse(String args) {
        if (StringUtils.isEmpty(args)) {
            return Collections.emptyMap();
        }
        final Map<String, String> map = new HashMap<String, String>();
        Scanner scanner = new Scanner(args);
        scanner.useDelimiter("\\s*,\\s*");
        while (scanner.hasNext()) {
            String token = scanner.next();
            int assign = token.indexOf('=');
            if (assign == -1) {
                map.put(token, "");
            } else {
                String key = token.substring(0, assign);
                String value = token.substring(assign + 1);
                map.put(key, value);
            }
        }
        scanner.close();
        return Collections.unmodifiableMap(map);
    }
}
