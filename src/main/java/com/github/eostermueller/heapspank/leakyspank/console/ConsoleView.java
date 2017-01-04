package com.github.eostermueller.heapspank.leakyspank.console;

import com.github.eostermueller.heapspank.leakyspank.LeakySpankContext;

/**
 * @stolenFromo:  https://github.com/patric-r/jvmtop/blob/1d38fb29698cf396042a08be08e9a1024f95fd2a/src/main/java/com/jvmtop/view/ConsoleView.java
 * @author erikostermueller
 *
 */
public interface ConsoleView
{
  public void init();
  /**
   * Prints the view to STDOUT.
   *
   * @throws Exception
   */
  public void printView() throws Exception;

  /**
   * Notifies that this view encountered issues
   * and should be called again (e.g. due to exceptions)
   *
   * TODO: remove this method and use proper exception instead.
   *
   * @return
   */
  public boolean shouldExit();

  /**
   * Requests the view to sleep (defined as "not outputting anything").
   * However, the view is allowed to do some work / telemtry retrieval during sleep.
   *
   */
  public void sleep(long millis) throws Exception;
  
  public void setLeakySpankContext(LeakySpankContext ctx);

	LeakySpankContext getLeakySpankContext();
	
	public void setDisplayUpdateListener(DisplayUpdateListener displayUpdateListener);
	
	public DisplayUpdateListener getDisplayUpdateListener();
	public int getDisplayRowCount();
	public void setDisplayRowCount(int rows);
}