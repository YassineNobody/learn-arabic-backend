package com.backend.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class RequestUtil {

    private static final int MAX_PAGE_SIZE = 100; // ✅ sécurité contre les abus API

    public static Pageable getPageable(int page, int size, Boolean sortDesc) {
        // ✅ Empêche les tailles excessives
        int safeSize = Math.min(size, MAX_PAGE_SIZE);
        int safePage = Math.max(page, 0);

        Sort sort = (sortDesc == null || sortDesc)
                ? Sort.by("createdAt").descending()
                : Sort.by("createdAt").ascending();

        return PageRequest.of(safePage, safeSize, sort);
    }
}
