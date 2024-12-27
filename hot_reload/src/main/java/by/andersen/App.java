package by.andersen;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class App {
  public static void main(String[] args)
      throws InterruptedException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    while (true) {
      Class<?> userClass =
          new DynamicClassLoader("target/classes").load("by.andersen.App$User");
      Method method = userClass.getMethod("play");
      method.invoke(null);
      Thread.sleep(10000);
    }
  }

  public static class User {
    public static void play() {
      playFootball();
//			playBasketball();
    }

    public static void playFootball() {
      System.out.println("Play Football");
    }

    public static void playBasketball() {
      System.out.println("Play Basketball");
    }
  }
}
