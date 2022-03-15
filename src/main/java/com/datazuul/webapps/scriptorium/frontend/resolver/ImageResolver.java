package com.datazuul.webapps.scriptorium.frontend.resolver;

import com.datazuul.webapps.scriptorium.domain.exceptions.ResolvingException;

public interface ImageResolver {

  public boolean isResolvable(String identifier);

  public byte[] getImageData(String identifier) throws ResolvingException;
}
