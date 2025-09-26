package com.example.employee_creator.common.dtos;

import java.util.List;

public class PageResponse<T> {
    private int currentPage;
    private int totalPages;
    private long totalResults;
    private int resultsPerPage;
    private Integer nextPage;
    private Integer previousPage;
    private List<T> data;

    public PageResponse(int currentPage,
            int totalPages,
            long totalResults,
            int resultsPerPage,
            Integer nextPage,
            Integer previousPage,
            List<T> data) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalResults = totalResults;
        this.resultsPerPage = resultsPerPage;
        this.nextPage = nextPage;
        this.previousPage = previousPage;
        this.data = data;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalResults() {
        return totalResults;
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public Integer getNextPage() {
        return nextPage;
    }

    public Integer getPreviousPage() {
        return previousPage;
    }

    public List<T> getData() {
        return data;
    }

}
