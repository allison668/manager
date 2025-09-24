package com.example.filemanager.service;

import com.example.filemanager.entity.dto.FileItem;
import com.example.filemanager.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

	@Autowired
	private FileUtil fileUtil;

	// 获取目录列表
	// 在 FileService.java 中修改 listFiles 方法如下：

	public List<FileItem> listFiles(String path) throws IOException {
		Path dir = fileUtil.resolve(path);
		if (!Files.exists(dir) || !Files.isDirectory(dir)) {
			throw new IllegalArgumentException("路径不存在或不是目录: " + path);
		}

		List<FileItem> items = new ArrayList<>();
		try (var stream = Files.list(dir)) {
			stream.forEach(p -> {
				try {
					FileItem item = new FileItem();
					item.setName(p.getFileName().toString());
					item.setPath(fileUtil.getDataRoot().relativize(p).toString().replace("\\", "/"));

					BasicFileAttributes attrs = Files.readAttributes(p, BasicFileAttributes.class);
					item.setDirectory(attrs.isDirectory());
					item.setSize(item.isDirectory() ? 0 : attrs.size());
					item.setLastModified(attrs.lastModifiedTime().toMillis());
					item.setCreatedTime(attrs.creationTime().toMillis());

					if (item.isDirectory()) {
						item.setType("folder");
						item.setExtension("");
					}
					else {
						String fileName = item.getName();
						String ext = fileName.lastIndexOf('.') > 0
								? fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase()
								: "";
						item.setExtension(ext);
						// 尝试探测 MIME 类型
						String mimeType = Files.probeContentType(p);
						item.setType(mimeType != null ? mimeType : "application/octet-stream");
					}

					items.add(item);
				} catch (IOException e) {
					// 忽略个别文件读取失败，不影响整体列表
					System.err.println("无法读取文件: " + p + " - " + e.getMessage());
				}
			});
		}
		return items;
	}

	// 新建文件夹
	public void createDirectory(String path) throws IOException {
		Path dir = fileUtil.resolve(path);
		if (Files.exists(dir)) {
			throw new IllegalArgumentException("目录已存在: " + path);
		}
		Files.createDirectories(dir);
	}

	// 重命名（文件或文件夹）
	public void rename(String oldPath, String newPath) throws IOException {
		Path oldFile = fileUtil.resolve(oldPath);
		Path newFile = fileUtil.resolve(newPath);
		if (!Files.exists(oldFile)) {
			throw new IllegalArgumentException("原路径不存在: " + oldPath);
		}
		if (Files.exists(newFile)) {
			throw new IllegalArgumentException("目标路径已存在: " + newPath);
		}
		Files.move(oldFile, newFile, StandardCopyOption.REPLACE_EXISTING);
	}

	// 删除（文件或文件夹）
	public void delete(String path) throws IOException {
		Path target = fileUtil.resolve(path);
		if (!Files.exists(target)) {
			throw new IllegalArgumentException("路径不存在: " + path);
		}
		if (Files.isDirectory(target)) {
			Files.walk(target)
			     .sorted((a, b) -> -a.compareTo(b)) // 倒序删除，先删文件再删目录
			     .forEach(p -> {
				     try {
					     Files.delete(p);
				     } catch (IOException e) {
					     throw new RuntimeException(e);
				     }
			     });
		}
		else {
			Files.delete(target);
		}
	}

	// 移动（文件或文件夹）
	public void move(String sourcePath, String targetPath) throws IOException {
		Path source = fileUtil.resolve(sourcePath);
		Path target = fileUtil.resolve(targetPath);
		if (!Files.exists(source)) {
			throw new IllegalArgumentException("源路径不存在: " + sourcePath);
		}
		if (Files.exists(target)) {
			throw new IllegalArgumentException("目标路径已存在: " + targetPath);
		}
		Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
	}

	// 上传文件
	public void uploadFile(String path, MultipartFile file) throws IOException {
		if (file.isEmpty()) {
			throw new IllegalArgumentException("文件为空");
		}
		Path filePath = fileUtil.resolve(path);
		Files.createDirectories(filePath.getParent()); // 确保父目录存在
		file.transferTo(filePath.toFile());
	}

	// 下载文件资源
	public Resource downloadFile(String path) {
		Path filePath = fileUtil.resolve(path);
		if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
			throw new IllegalArgumentException("文件不存在或不是文件: " + path);
		}
		return fileUtil.getResource(filePath);
	}

	// 新建空文件
	public void createFile(String path) throws IOException {
		Path filePath = fileUtil.resolve(path);
		if (Files.exists(filePath)) {
			throw new IllegalArgumentException("文件已存在: " + path);
		}
		Files.createDirectories(filePath.getParent());
		Files.createFile(filePath);
	}
}