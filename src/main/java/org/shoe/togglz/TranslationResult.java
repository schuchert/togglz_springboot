package org.shoe.togglz;

public class TranslationResult {
    public String original;
    public String translated;
    public boolean active;

    public TranslationResult() {
    }

    public TranslationResult(String original, String translated, boolean active) {
        this.original = original;
        this.translated = translated;
        this.active = active;
    }
}
