package by.andersen;

import by.andersen.context.AppContext;
import by.andersen.loops.StartLoop;

/**
 * Hello world!
 *
 */
public class App {
  public static void main( String[] args ) {
    AppContext appContext = AppContext.init();
    new StartLoop(appContext).run();
  }
}
