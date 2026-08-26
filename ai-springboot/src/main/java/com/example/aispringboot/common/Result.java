package com.example.aispringboot.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private String code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data){
        return new Result<>(ResultCode.SUCCESS.getCode(),ResultCode.SUCCESS.getMessage(),data);
    }

    public static <T> Result<T> fail(String code,String message){
        return new Result<>(code,message,null);
    }

     public static <T> Result<T> success(){
        return new Result<>(ResultCode.SUCCESS.getCode(),ResultCode.SUCCESS.getMessage(),null);
     }

     public static <T> Result<T> error(String code,String message,T data){
        return new Result<>(code,message,data);
     }

}
