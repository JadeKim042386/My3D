package joo.project.my3d.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T>{
    private static final String SUCCESS = "success";
    private static final String INVALID = "invalid";
    private static final String ERROR = "error";

    private String status;
    private T data;
    private String message;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(SUCCESS, data,null);
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<>(SUCCESS, null, null);
    }

    public static <T> ApiResponse<T> invalid(T data) {
        return new ApiResponse<>(INVALID, data, null);
    }

    public static ApiResponse<String> error(String message) {
        return new ApiResponse<>(ERROR, null, message);
    }
}
