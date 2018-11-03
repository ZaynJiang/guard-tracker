package cn.guard.tracker.bootstrap;


import java.lang.instrument.Instrumentation;
import java.util.Map;


public class TrackerBootstrap {

	public static void premain(String agentArgs, Instrumentation instrumentation) {
		if(agentArgs == null){
			agentArgs = "";
		}
		MainArgsResolver argsResolver = new  MainArgsResolver();
		Map<String, String> mainArgs = argsResolver.parse(agentArgs);

	}

	/**
	 * 将增强代码植入到虚拟机字节码中去
	 * @param instrumentation
	 * @param agentJarFile
	 */
	private static void appendToBootstrapClassLoader(Instrumentation instrumentation) {
            instrumentation.appendToBootstrapClassLoaderSearch(null);
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
