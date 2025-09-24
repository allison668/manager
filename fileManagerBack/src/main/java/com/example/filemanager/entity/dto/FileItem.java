package com.example.filemanager.entity.dto;

import lombok.Data;

@Data
public class FileItem {
	private String name;
	private String path;          // 相对 data/ 的路径
	private boolean directory;
	private long size;
	private long lastModified;
	private String type;
	private String extension;
	private long createdTime;
}