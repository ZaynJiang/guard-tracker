package cn.guard.tracker.bootstrap;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

/**
 *  启动包的所有的文件
 */
public class BootstrapJarFile {

    private final List<JarFile> jarFiles = new ArrayList<JarFile>();

    public BootstrapJarFile() {}

    public List<JarFile> getJarFiles() {
        return jarFiles;
    }

    public void appendJarFile(JarFile jarFile) {
        if (jarFile == null) {
            throw new NullPointerException("appendJarFile exception, jarFile must not be null");
        }
        jarFiles.add(jarFile);
    }

    public List<String> getJarFileNames() {
        List<String> jarFileNames = new ArrayList<String>();
        for (JarFile jarFile : jarFiles) {
            jarFileNames.add(jarFile.getName());
        }
        return jarFileNames;
    }
}
