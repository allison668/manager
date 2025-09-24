// src/main/java/com/filesystem/filesysbackend/entity/vo/ResponseVO.java
package com.example.filemanager.entity.vo;

import lombok.Data;

@Data
public class ResponseVO<T> {
	private String status;
	private Integer code;
	private String message;
	private T data;

	public ResponseVO(String status, Integer code, String message, T data) {
		this.status = status;
		this.code = code;
		this.message = message;
		this.data = data;
	}

	// 快捷成功方法
	public static <T> ResponseVO<T> success(String message) {
		return new ResponseVO<>("success", 200, message, null);
	}

	public static <T> ResponseVO<T> success(String message, T data) {
		return new ResponseVO<>("success", 200, message, data);
	}

	// 快捷错误方法
	public static <T> ResponseVO<T> error(Integer code, String message) {
		return new ResponseVO<>("error", code, message, null);
	}
}