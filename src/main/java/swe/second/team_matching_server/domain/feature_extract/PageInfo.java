import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private String resultCode;
    private String message;
    private T data;
    private PageInfo page;
}

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageInfo {
    private int number;
    private int size;
    private long totalCount;
}

public class ResultCode {
    public static final String SUCCESS = "SUCCESS";
    public static final String BAD_REQUEST = "BAD_REQUEST";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
}