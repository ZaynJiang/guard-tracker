package cn.guard.tracker.bootstrap;

import java.net.URL;

public interface JarResolver {

    /**
     *  校验启动包，看是否能校验成功
     * @return 是否成功标识
     */
    boolean verify();

    BootstrapJarFile getBootstrapJarFile();
}
