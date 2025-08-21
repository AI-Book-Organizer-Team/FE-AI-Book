package com.example.fe_ai_book.service.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class TextNormalizer {
    private TextNormalizer() {}

    public static String normalizeForContains(String s) {
        if (s == null) return "";
        return s.replaceAll("[\\s·,;:/]+", "").toLowerCase();
    }

    public static String extractPrimaryName(String raw) {
        if (raw == null) return "";
        String t = raw.replace("지은이:", "").replace("옮긴이:", "")
                .replace("지음", "").replace("옮김", "");
        List<String> tokens = splitTokens(t);
        for (String tok : tokens) {
            if (!tok.isBlank()) return tok.trim();
        }
        return raw.trim();
    }

    public static List<String> extractNameTokens(String raw) {
        if (raw == null) return new ArrayList<>();
        String t = raw.replace("지은이:", "").replace("옮긴이:", "")
                .replace("지음", "").replace("옮김", "");
        List<String> tokens = splitTokens(t);
        List<String> out = new ArrayList<>();
        for (String tok : tokens) if (!tok.isBlank()) out.add(tok.trim());
        if (out.isEmpty() && raw != null) out.add(raw.trim());
        return out;
    }

    private static List<String> splitTokens(String s) {
        return Arrays.asList(s.split("[·,;|/&]+"));
    }
}
