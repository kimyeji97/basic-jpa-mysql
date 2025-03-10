package com.techlabs.platform.core.http.response.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

@Data
@NoArgsConstructor
public class PaginationResponse {
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
    private int numberOfElements;
    private boolean first;
    private boolean last;
    
    public PaginationResponse(Pageable page , Integer rowCount , Integer totalElements) {
        this(page.getPageNumber() , page.getPageSize() , rowCount , totalElements);
    }
    
    public PaginationResponse(Integer page , Integer size , Integer numberOfElements , Integer totalElements) {
        this.page = page;
        this.size = size;
        this.numberOfElements = numberOfElements;
        this.totalElements = totalElements;
        this.totalPages = ( totalElements % size ) == 0 ? totalElements / size : totalElements / size + 1;
        this.first = page < 1;
        this.last = (page+1) * size >= totalElements;
    }
}
