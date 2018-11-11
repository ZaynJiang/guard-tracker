package cn.guard.tracker.bootstrap;

import cn.guard.tracker.bootstrap.bootstrap.core.utils.CollectionUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  启动jar包的解析器
 */
public class BootstrapJarResolver implements JarResolver {

    private BootstrapJarFile bootstrapJarFile;

    static final String VERSION_PATTERN = "(-[0-9]+\\.[0-9]+\\.[0-9]+((\\-SNAPSHOT))?)?";
    static final Pattern DEFAULT_AGENT_PATTERN = compile("guard-tracker-bootstrap" +VERSION_PATTERN + ".jar");
    static final Pattern DEFAULT_AGENT_CORE_PATTERN = compile("guard-tracker-bootstrap-core" +VERSION_PATTERN + ".jar");
    static final Pattern DEFAULT_AGENT_EXTRA_PATTERN = compile("guard-tracker-bootstrap-extra" +VERSION_PATTERN + ".jar");
    static final Pattern DEFAULT_COLLECTOR_PATTERN = compile("guard-tracker-collector" +VERSION_PATTERN + ".jar");
    static final String ENVIRONMENT = "env";

    private final Map<String, String> mainArgs;

    private String classPath;
    private final Pattern agentPattern;
    private final Pattern agentCorePattern;
    private final Pattern agentExtraPattern;
    private final Pattern collectorPattern;

    private String agentJarName;
    private String agentJarFullPath;
    private String agentDirPath;

    private String bootstrapCoreJar;
    private String bootstrapExtraJar;

    private String collectorJar;


    public BootstrapJarResolver() {
        this(getClassPathFromSystemProperty(), null);
    }

    public BootstrapJarResolver (Map<String, String> mainArgs) {
        this(getClassPathFromSystemProperty(), mainArgs);
    }

    public BootstrapJarResolver(String classPath, Map<String, String> mainArgs) {
        this.classPath = classPath;
        this.mainArgs = mainArgs;
        this.agentPattern = DEFAULT_AGENT_PATTERN;
        this.agentCorePattern = DEFAULT_AGENT_CORE_PATTERN;
        this.agentExtraPattern = DEFAULT_AGENT_EXTRA_PATTERN;
        this.collectorPattern = DEFAULT_COLLECTOR_PATTERN;
    }

    public static String getClassPathFromSystemProperty() {
        return System.getProperty("java.class.path");
    }

    private static Pattern compile(String regex) {
        return Pattern.compile(regex);
    }

    @Override
    public boolean verify() {
        final BootstrapJarFile bootstrapJarFile = new BootstrapJarFile();
        final boolean agentJarNotFound = this.findAgentJar();
        if (!agentJarNotFound) {
            System.err.println("guard-tracker-bootstrap-x.x.x(-SNAPSHOT).jar not found.");
            return false;
        }
        // load core jars
        final String bootstrapCoreJarFile = getBootstrapCoreJar();
        if (bootstrapCoreJarFile == null) {
            System.err.println("guard-tracker-bootstrap-core-x.x.x(-SNAPSHOT).jar not found");
            return false;
        }
        final JarFile bootstrapCoreJar = getJarFile(bootstrapCoreJarFile);
        if (bootstrapCoreJar == null) {
            System.err.println("guard-tracker-bootstrap-core-x.x.x(-SNAPSHOT).jar not found");
            return false;
        }
        bootstrapJarFile.appendJarFile(bootstrapCoreJar);
        // load extra jars
        final String bootstrapExtraJarFile = getBootstrapExtraJar();
        if (bootstrapExtraJarFile == null) {
            System.err.println("guard-tracker-bootstrap-extra-x.x.x(-SNAPSHOT).jar not found");
            return false;
        }
        final JarFile bootstrapExtraJar = getJarFile(bootstrapExtraJarFile);
        if (bootstrapExtraJar == null) {
            System.err.println("guard-tracker-bootstrap-extra-x.x.x(-SNAPSHOT).jar not found");
            return false;
        }
        bootstrapJarFile.appendJarFile(bootstrapExtraJar);
        this.bootstrapJarFile = bootstrapJarFile;
        return true;
    }


    private JarFile getJarFile(String jarFilePath) {
        try {
            return new JarFile(jarFilePath);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    public String getBootstrapCoreJar() {
        return bootstrapCoreJar;
    }

    public String getCollectorJar(){
        return collectorJar;
    }

    public String getBootstrapExtraJar(){
        return bootstrapExtraJar;
    }

    private boolean findAgentJar() {
        Matcher matcher = agentPattern.matcher(classPath);
        if (!matcher.find()) {
            return false;
        }
        this.agentJarName = parseAgentJar(matcher);
        this.agentJarFullPath = parseAgentJarPath(classPath, agentJarName);
        if (agentJarFullPath == null) {
            return false;
        }
        this.agentDirPath = parseAgentDirPath(agentJarFullPath);
        if (agentDirPath == null) {
            return false;
        }
        System.out.println("Agent original-path:" + agentDirPath);
        this.agentDirPath = toCanonicalPath(agentDirPath);

        this.bootstrapCoreJar = findFromBootDir("guard-tracker-bootstrap-core.jar", agentCorePattern);
        this.bootstrapExtraJar = findFromExtraDir("guard-tracker-bootstrap-extra.jar", agentExtraPattern);
        this.collectorJar = findFromAgentDir("guard-tracker-collector.jar", collectorPattern);
        return true;
    }

    private String findFromBootDir(final String name, final Pattern pattern) {
        String bootDirPath = getAgentBootPath();
        final File[] files = listFiles(name, pattern, bootDirPath);
        if (CollectionUtils.isEmpty(files)) {
            System.out.println(name + " not found.");
            return null;
        }
        if (files.length == 1) {
            File file = files[0];
            return toCanonicalPath(file);
        } else {
            System.out.println("too many " + name + " found. " + Arrays.toString(files));
            return null;
        }
    }

    private File[] listFiles(final String name, final Pattern pattern, String bootDirPath) {
        File bootDir = new File(bootDirPath);
        return bootDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String fileName) {
                Matcher matcher = pattern.matcher(fileName);
                if (matcher.matches()) {
                    System.out.println("found " + name + ". " + dir.getAbsolutePath() + File.separator + fileName);
                    return true;
                }
                return false;
            }
        });
    }

    public String getAgentJarFullPath() {
        return agentJarFullPath;
    }

    public String getAgentBootPath() {
        return this.agentDirPath + File.separator + "boot";
    }

    public String getAgentExtraPath() {
        return this.agentDirPath + File.separator + "extra";
    }

    private String findFromExtraDir(final String name, final Pattern pattern) {
        String bootDirPath = getAgentExtraPath();
        final File[] files = listFiles(name, pattern, bootDirPath);
        if (CollectionUtils.isEmpty(files)) {
            System.out.println(name + " not found.");
            return null;
        } else if (files.length == 1) {
            File file = files[0];
            return toCanonicalPath(file);
        } else {
            System.out.println("too many " + name + " found. " + Arrays.toString(files));
            return null;
        }
    }

    private String findFromAgentDir(final String name, final Pattern pattern) {
        final File[] files = listFiles(name, pattern, this.agentDirPath);
        if (CollectionUtils.isEmpty(files)) {
            System.out.println(name + " not found.");
            return null;
        } else if (files.length == 1) {
            File file = files[0];
            return toCanonicalPath(file);
        } else {
            System.out.println("too many " + name + " found. " + Arrays.toString(files));
            return null;
        }
    }

    private String parseAgentDirPath(String agentJarFullPath) {
        int index1 = agentJarFullPath.lastIndexOf("/");
        int index2 = agentJarFullPath.lastIndexOf("\\");
        int max = Math.max(index1, index2);
        if (max == -1) {
            return null;
        }
        return agentJarFullPath.substring(0, max);
    }

    private String toCanonicalPath(String path) {
        final File file = new File(path);
        return toCanonicalPath(file);
    }

    private String toCanonicalPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            System.err.println(file.getPath() + " getCanonicalPath() error. Error:" + e.getMessage());
            e.printStackTrace();
            return file.getAbsolutePath();
        }
    }

    /**
     * 获取启动包的全路径
     * @param classPath
     * @param agentJar
     * @return
     */
    private String parseAgentJarPath(String classPath, String agentJar) {
        String[] classPathList = classPath.split(File.pathSeparator);
        for (String findPath : classPathList) {
            boolean find = findPath.contains(agentJar);
            if (find) {
                return findPath;
            }
        }
        return null;
    }

    private String parseAgentJar(Matcher matcher) {
        int start = matcher.start();
        int end = matcher.end();
        return this.classPath.substring(start, end);
    }

    public BootstrapJarFile getBootstrapJarFile() {
        return bootstrapJarFile;
    }
}
