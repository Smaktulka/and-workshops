package by.andersen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

public class DynamicClassLoader extends ClassLoader {
  private final String classPath;
  private final Set<String> loadedClasses = new HashSet<>();
  private final Set<String> unavailableClasses = new HashSet<>();
  private final ClassLoader parent = DynamicClassLoader.class.getClassLoader();

  public DynamicClassLoader(String classPath) {
    this.classPath = classPath;
  }

  public Class<?> load(String name) {
    try {
      return this.loadClass(name);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    if (loadedClasses.contains(name) || unavailableClasses.contains(name)) {
      return super.loadClass(name);
    }

    byte[] newClassBytes;
    try {
      newClassBytes = getNewClassFileBytes(name);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    if (newClassBytes != null) {
      loadedClasses.add(name);
      return loadNewClass(newClassBytes, name);
    } else {
      unavailableClasses.add(name);
      return parent.loadClass(name);
    }
  }

  private byte[] getNewClassFileBytes(String className) throws IOException {
    String classNamePath = className.replaceAll("\\.", "/") + ".class";
    File classFile = new File(classPath, classNamePath);
    if (!classFile.exists()) {
      return null;
    }

    System.out.println("Loading class " + className);
    return Files.readAllBytes(classFile.toPath());
  }

  private Class<?> loadNewClass(byte[] classFileBytes, String className) {
    Class<?> clazz = super.defineClass(className, classFileBytes, 0, classFileBytes.length);
    if (clazz != null) {
      if (clazz.getPackage() == null) {
        int lastDotIndex = className.lastIndexOf('.');
        this.definePackage(className.substring(0, lastDotIndex));
      }

      super.resolveClass(clazz);
    }

    return clazz;
  }

  private void definePackage(String className) {
    super.definePackage(className.replaceAll("\\.\\w+$", ""), null, null, null, null, null, null,
        null);
  }
}
