package cn.guard.tracker.bootstrap;


import java.lang.instrument.Instrumentation;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;


public class TrackerBootstrap {

	public static void premain(String agentArgs, Instrumentation instrumentation) {
		if(agentArgs == null){
			agentArgs = "";
		}
		MainArgsResolver argsResolver = new  MainArgsResolver();
		Map<String, String> mainArgs = argsResolver.parse(agentArgs);

		JarResolver bootstrapJarResolver = new BootstrapJarResolver(mainArgs);

		if (!bootstrapJarResolver.verify()) {
			System.err.println("bootstrapJar resolver failed");
			agentFailed();
			return;
		}

		//获得所有埋点jar包
		BootstrapJarFile bootJarFile = bootstrapJarResolver.getBootstrapJarFile();
		appendToBootstrapClassLoader(instrumentation, bootJarFile);

		// 启动
		TrackerStarter trackerStarter = new TrackerStarter(bootstrapJarResolver, instrumentation, bootJarFile);
		if (!trackerStarter.start()) {
			System.err.println("trackerStarter start failed");
			agentFailed();
			return;
		}


	}

	/**
	 * 将增强代码植入到虚拟机字节码中去
	 * @param instrumentation
	 * @param bootJarFile
	 */
	private static void appendToBootstrapClassLoader(Instrumentation instrumentation, BootstrapJarFile bootJarFile) {
		List<JarFile> jarFiles = bootJarFile.getJarFiles();
		for (JarFile jarFile : jarFiles) {
			instrumentation.appendToBootstrapClassLoaderSearch(jarFile);
		}
		System.out.println("appendToBootstrapClassLoader success, jarFile's name is " + bootJarFile.getJarFileNames());
    }

	/**
	 * 启动失败后执行
	 */
	private static void agentFailed() {
		final String errorLog = 
				"\n*****************************************************************************\n"
				+ "* guard tracker Agent load failure\n"
				+ "*****************************************************************************";
		System.err.println(errorLog);
	}

}
