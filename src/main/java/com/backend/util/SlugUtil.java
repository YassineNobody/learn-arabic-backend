package com.backend.util;

import com.github.slugify.Slugify;

public class SlugUtil {

    private static final Slugify slugify = Slugify.builder()
            .lowerCase(true)
            .build();

    public static String generateSlug(String input) {
        if (input == null || input.isBlank()) return "";
        return slugify.slugify(input);
    }

    // ✅ Génère un slug unique (si doublon dans la BDD)
    public static String generateUniqueSlug(String input, java.util.function.Function<String, Boolean> existsBySlug) {
        String baseSlug = generateSlug(input);
        String slug = baseSlug;
        int counter = 1;

        while (existsBySlug.apply(slug)) {
            slug = baseSlug + "-" + counter++;
        }

        return slug;
    }
}
