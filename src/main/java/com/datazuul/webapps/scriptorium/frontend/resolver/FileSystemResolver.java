package com.datazuul.webapps.scriptorium.frontend.resolver;

import com.datazuul.webapps.scriptorium.domain.exceptions.ResolvingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class FileSystemResolver extends PatternResolver {

  public FileSystemResolver(String pattern, String replacement) {
    super(pattern, replacement);
  }

  public FileSystemResolver(Pattern pattern, String replacement) {
    super(pattern, replacement);
  }

  @Override
  public byte[] getImageData(String identifier) throws ResolvingException {
    Path path = Paths.get(getSourcePath(identifier));
    try {
      return Files.readAllBytes(path);
    } catch (IOException e) {
      throw new ResolvingException(path.toString(), e);
    }
  }
}
