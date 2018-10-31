package cn.guard.tracker;


import java.lang.instrument.Instrumentation;


public class TrackerBootstrap {

	public static void premain(String agentArgs, Instrumentation instrumentation) {
		if(agentArgs == null){
			agentArgs = "";
		}

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
