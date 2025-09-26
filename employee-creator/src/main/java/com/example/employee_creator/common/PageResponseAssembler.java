package com.example.employee_creator.common;

import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.example.employee_creator.common.dtos.PageResponse;

@Component
public class PageResponseAssembler {

    public <T, R> PageResponse<R> toPageResponse(Page<T> page, Function<T, R> mapper) {
        // page is 0 index - we want 1 index
        int currentPage = page.getNumber() + 1;
        Integer nextPage = currentPage < page.getTotalPages() ? currentPage + 1 : null;
        Integer prevPage = currentPage > 1 ? currentPage - 1 : null;

        return new PageResponse<R>(
                currentPage,
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize(),
                nextPage,
                prevPage,
                page.map(mapper).getContent());
    }
}
