package swe.second.team_matching_server.common.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.ToString;
import swe.second.team_matching_server.common.enums.ResultCode;

@Getter
@ToString
public class    ApiResponse<T> {
    private final ResultCode code;
    private final String message;
    private final T data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final PageResponse page;

    private ApiResponse(ResultCode code, String message, T data, PageResponse page) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.page = page;
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(
            ResultCode.SUCCESS,
            ResultCode.SUCCESS.getMessage(),
            null,
            null
        );
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
            ResultCode.SUCCESS,
            ResultCode.SUCCESS.getMessage(),
            data,
            null
        );
    }

    public static <T> ApiResponse<List<T>> success(List<T> data) {
        return new ApiResponse<>(
            ResultCode.SUCCESS,
            ResultCode.SUCCESS.getMessage(),
            data,
            null
        );
    }

    public static <T> ApiResponse<List<T>> success(Page<T> data) {
        return new ApiResponse<>(  
            ResultCode.SUCCESS,
            ResultCode.SUCCESS.getMessage(),
            data.getContent(),
            PageResponse.from(data)
        );
    }

    public static <T> ApiResponse<T> failure(ResultCode code) {
        return new ApiResponse<>(
            code,
            code.getMessage(),
            null,
            null
        );
    }

    public static <T> ApiResponse<T> failure(ResultCode code, String message) {
        return new ApiResponse<>(
            code,
            message,
            null,
            null
        );
    }

    @Getter
    @ToString
    public static class PageResponse {
        private final int number;
        private final int size;
        private final int totalCount;
        private final boolean hasNext;
        private final boolean hasPrevious;

        private PageResponse(int number, int size, int totalCount, boolean hasNext, boolean hasPrevious) {
            this.number = number;
            this.size = size;
            this.totalCount = totalCount;
            this.hasNext = hasNext;
            this.hasPrevious = hasPrevious;
        }

        public static PageResponse from(Page<?> page) {
            return new PageResponse(
                page.getNumber(),
                page.getSize(),
                (int)page.getTotalElements(),
                page.hasNext(),
                page.hasPrevious()
            );
        }
    }
}