package by.andersen.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class ReflectionUtils {
  public static Field[] getNonStaticFields(Class clazz) {
    return Arrays.stream(clazz.getDeclaredFields())
        .filter(f -> !Modifier.isStatic(f.getModifiers()))
        .toArray(Field[]::new);
  }

  public static Object getFieldValue(Field field, Object obj) {
    try {
      field.setAccessible(true);
      return field.get(obj);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
