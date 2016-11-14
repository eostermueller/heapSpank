package com.github.eostermueller.heapspank.leakyspank.console;

import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.github.eostermueller.heapspank.leakyspank.LeakySpankContext;
import com.github.eostermueller.heapspank.leakyspank.Model;
import com.github.eostermueller.heapspank.util.LimitedSizeQueue;

public class LeakySpankConsole implements DisplayUpdateListener {

	private static final String VERSION = "v0.1";
	private static final String BANNER_FORMAT = "  %4ds   leakySpank memory leak detector version [%s]%n";
	private static final String BANNER_FORMAT_ALT = "# %4ds   leakySpank memory leak detector version [%s] ##%n";
	Queue<Model> jmapHistoOutputQueue = new ConcurrentLinkedQueue<Model>();
	JMapHistoRunner jMapHistoRunner = null;
	LeakySpankContext leakySpankContext = null;
	ConsoleView consoleView = null;
	int screenRefreshIntervalSeconds = -1;
	private int maxIterations_;
	private Config config;
	private long displayUpdatedTimestampMs;
	LimitedSizeQueue<String> debug = new LimitedSizeQueue<String>(10);

	public static void main(String args[]) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		LeakySpankConsole leakySpankConsole = new LeakySpankConsole();
		Config config = new DefaultConfig(args);
		leakySpankConsole.init(config);
		leakySpankConsole.loopForever(leakySpankConsole.getConsoleView());
	}

	private void loopForever(ConsoleView view) {

		try {
			System.setOut(new PrintStream(new BufferedOutputStream(
					new FileOutputStream(FileDescriptor.out)), false));
			int iterations = 0;
			while (!view.shouldExit()) {
				clearConsole();

				Model m = this.jmapHistoOutputQueue.poll();
				if (m != null)
					this.leakySpankContext.addJMapHistoRun(m);
				printTopBar();
				view.printView();
				//printDebug();
				System.out.flush();
				if (iterations >= maxIterations_ && maxIterations_ > 0) {
					break;
				}
				view.sleep((int) (screenRefreshIntervalSeconds * 1000));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void printDebug() {
		System.out.println("                    -- ===================================== --");
		for(String item : this.debug ){
		    System.out.println(item.toString());
		}
		
	}

	private void printTopBar() {
		long screenRefreshDelayMs = this.getConfig().getjMapCountPerWindow()
				* this.getConfig().getjMapHistoIntervalSeconds() * 1000;

		long estimatedRefreshTimestampMs = this.getDisplayUpdatedTimestampMs()
				+ screenRefreshDelayMs;
		// estimatedRefreshTimestampMs -= 2000;//correction
		long msUntilRefresh = estimatedRefreshTimestampMs
				- System.currentTimeMillis();
		double secondsUntilRefresh = Math.round(msUntilRefresh / 1000);
		int intSecondsUntilRefresh = (int) secondsUntilRefresh;
		if (intSecondsUntilRefresh < 6) {

			if (intSecondsUntilRefresh % 2 == 0)
				System.out.format(BANNER_FORMAT, intSecondsUntilRefresh,
						VERSION);
			else
				System.out.format(BANNER_FORMAT_ALT, intSecondsUntilRefresh,
						VERSION);

		} else
			System.out.format(BANNER_FORMAT, intSecondsUntilRefresh, VERSION);
	}

	private void init(Config config2) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		screenRefreshIntervalSeconds = config2
				.getScreenRefreshIntervalSeconds();
		this.maxIterations_ = config2.getMaxIterations();
		this.setLeakySpankContext(new LeakySpankContext(config2.getPid(),
				config2.getjMapHistoIntervalSeconds(), config2
						.getjMapCountPerWindow(), config2
						.getSuspectCountPerWindow()));

		this.getLeakySpankContext().setTopNSuspects(
				config2.getSuspectCountPerWindow());
		this.getLeakySpankContext().setDisplayQueue(debug);

		jMapHistoRunner = new JMapHistoRunner(config2.getPid(),
				config2.getjMapHistoIntervalSeconds(),
				this.jmapHistoOutputQueue);

		jMapHistoRunner.launchJMapHistoExecutor();

		Class<ConsoleView> c = (Class<ConsoleView>) Class.forName(config2
				.getViewClass());
		ConsoleView view = c.newInstance();
		view.setLeakySpankContext(this.getLeakySpankContext());
		view.setDisplayUpdateListener((DisplayUpdateListener) this);
		this.debug(String.format("just set context [%s] for view [%s]",
				this.getLeakySpankContext(), view));
		this.setConsoleView(view);
		setConfig(config2);
	}

	private void setConfig(Config config2) {
		this.config = config2;
	}

	private Config getConfig() {
		return this.config;
	}

	private void debug(String msg) {
		System.out.println(DefaultView.LEAKY_SPANK + msg);
	}

	/**
	 * @stolenFrom: https://github.com/patric-r/jvmtop/blob/1d38f
	 *              b29698cf396042a08be08e9a1024f95fd2a
	 *              /src/main/java/com/jvmtop/JvmTop.java
	 */
	private void clearConsole() {
		if (System.getProperty("os.name").contains("Windows")) {
			// hack
			System.out
					.printf("%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n%n");
		} else if (System.getProperty("jvmtop.altClear") != null) {
			System.out.print('\f');
		} else {
			System.out.print(CLEAR_TERMINAL_ANSI_CMD);
		}
	}

	public ConsoleView getConsoleView() {
		return consoleView;
	}

	public void setConsoleView(ConsoleView consoleView) {
		this.consoleView = consoleView;
	}

	public LeakySpankContext getLeakySpankContext() {
		return this.leakySpankContext;
	}

	public void setLeakySpankContext(LeakySpankContext val) {
		this.leakySpankContext = val;
	}

	private final static String CLEAR_TERMINAL_ANSI_CMD = new String(
			new byte[] { (byte) 0x1b, (byte) 0x5b, (byte) 0x32, (byte) 0x4a,
					(byte) 0x1b, (byte) 0x5b, (byte) 0x48 });

	@Override
	public void updated() {
		this.setDisplayUpdatedTimestampMs(System.currentTimeMillis());
	}

	private void setDisplayUpdatedTimestampMs(long currentTimeMillis) {
		this.displayUpdatedTimestampMs = currentTimeMillis;
	}

	private long getDisplayUpdatedTimestampMs() {
		return this.displayUpdatedTimestampMs;
	}
}
