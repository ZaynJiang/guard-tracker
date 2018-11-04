package cn.guard.tracker.bootstrap;

import java.lang.instrument.Instrumentation;

public class TrackerStarter {

    private final JarResolver jarResolver;
    private final Instrumentation instrumentation;
    private final BootstrapJarFile bootstrapJarFile;

    public TrackerStarter (JarResolver jarResolver, Instrumentation instrumentation, BootstrapJarFile bootstrapJarFile) {
        if (jarResolver == null) {
            throw new NullPointerException("TrackerStarter, jarResolver must not be null");
        }
        if (instrumentation == null) {
            throw new NullPointerException("TrackerStarter, instrumentation must not be null");
        }
        this.jarResolver = jarResolver;
        this.instrumentation = instrumentation;
        this.bootstrapJarFile = bootstrapJarFile;
    }

    public boolean start() {
        return true;
    }
}
