package com.datazuul.webapps.scriptorium.frontend.resolver;

import com.datazuul.webapps.scriptorium.domain.exceptions.ResolvingException;
import java.util.regex.Pattern;

public abstract class PatternResolver implements ImageResolver {

    private Pattern pattern;
    private String replacement;

    public PatternResolver(String pattern, String replacement) {
        this(Pattern.compile(pattern), replacement);
    }

    public PatternResolver(Pattern pattern, String replacement) {
        this.pattern = pattern;
        this.replacement = replacement;
    }

    protected String getSourcePath(String identifier) throws ResolvingException {
        String sourcePath = pattern.matcher(identifier).replaceAll(replacement);
        return sourcePath;
    }

    @Override
    public boolean isResolvable(String identifier) {
        return pattern.matcher(identifier).matches();
    }
}
