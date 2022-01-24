package com.cocotalk.auth.dto.common;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
@JsonPropertyOrder({ "currentPage", "pageSize", "totalPages", "totalElements" })
public class PaginationDto {
    @NonNull
    protected Integer currentPage;
    @NonNull
    protected Integer pageSize;
    @NonNull
    protected Integer totalPages;
    @NonNull
    protected Long totalElements;

    public static PaginationDto of(Page pages) {
        return of(pages.getNumber(), pages.getSize(), pages.getTotalPages(), pages.getTotalElements());
    }
}
