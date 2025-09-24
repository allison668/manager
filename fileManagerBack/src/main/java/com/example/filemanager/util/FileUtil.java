package com.example.filemanager.util;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileUtil {

	private final Path dataRoot;

	public FileUtil() {
		// 获取当前工作目录（开发时是项目根目录，打包后是jar所在目录）
		String userDir = System.getProperty("user.dir");
		this.dataRoot = Paths.get(userDir, "data").toAbsolutePath().normalize();

		// 确保 data 目录存在
		try {
			Files.createDirectories(dataRoot);
		} catch (IOException e) {
			throw new RuntimeException("无法创建 data 目录: " + dataRoot, e);
		}
	}

	/**
	 * 根据相对路径获取绝对路径（防止路径穿越）
	 */
	public Path resolve(String relativePath) {
		if (relativePath == null) relativePath = "";
		Path path = dataRoot.resolve(relativePath).normalize();

		// 安全检查：确保路径在 dataRoot 内
		if (!path.startsWith(dataRoot)) {
			throw new SecurityException("非法路径访问: " + relativePath);
		}
		return path;
	}

	public Path getDataRoot() {
		return dataRoot;
	}

	public Resource getResource(Path path) {
		return new FileSystemResource(path.toFile());
	}
}