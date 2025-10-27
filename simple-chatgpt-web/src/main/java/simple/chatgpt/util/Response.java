package simple.chatgpt.util;

import java.util.Date;

public class Response<T> {
    private String status;
    private String message;
    private T data;
    private Date timestamp;
    private int httpStatus;

    public Response() {
        this.timestamp = new Date();
    }

    public static <T> Response<T> success(String message, T data, int httpStatus) {
        Response<T> response = new Response<>();
        response.setStatus("SUCCESS");
        response.setMessage(message);
        response.setData(data);
        response.setHttpStatus(httpStatus);
        return response;
    }

    public static <T> Response<T> error(String message, T data, int httpStatus) {
        Response<T> response = new Response<>();
        response.setStatus("ERROR");
        response.setMessage(message);
        response.setData(data);
        response.setHttpStatus(httpStatus);
        return response;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }
}
