package com.backend.dto.common;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
public class PagedResponse<T> {
    private List<T> content;
    private Meta meta;

    @Data
    @AllArgsConstructor
    public static class Meta {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean last;
    }

    public static <T> PagedResponse<T> fromPage(Page<T> page) {
        Meta meta = new Meta(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
        return new PagedResponse<>(page.getContent(), meta);
    }
}
