package by.andersen;

import by.andersen.config.AppConfig;
import by.andersen.loops.StartLoop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * Hello world!
 *
 */
@Configuration
public class App {
  @Autowired
  private StartLoop startLoop;

  public static void main(String[] args) {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

    App app = context.getBean(App.class);
    app.start();

    context.close();
  }

  public void start() {
    startLoop.run();
  }
}
