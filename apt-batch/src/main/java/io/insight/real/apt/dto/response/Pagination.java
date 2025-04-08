package io.insight.real.apt.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Description : 페이징 객체
 */
@Getter @Setter
@NoArgsConstructor
public class Pagination {
    private int totalItems;
    private long totalPages;
    private int currentPage;
    private int pageSize;

    private Pagination(
             int totalItems,
             long totalPages,
             int currentPage,
             int pageSize){
        this.totalItems = totalItems;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }
    public static Pagination by(int totalItems, int pageSize, int currentPage){
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        return new Pagination(totalItems, totalPages, currentPage+1, pageSize);
    }
}
