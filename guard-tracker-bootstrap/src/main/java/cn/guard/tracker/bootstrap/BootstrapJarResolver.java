package cn.guard.tracker.bootstrap;

import java.util.Map;

/**
 *  启动jar包的解析器
 */
public class BootstrapJarResolver implements JarResolver {

    public BootstrapJarResolver (Map<String, String> mainArgs) {

    }

    @Override
    public boolean verify() {
        return false;
    }

    @Override
    public BootstrapJarFile getBootstrapJarFile() {
        return null;
    }
}
