package com.example.filemanager.controller;

import com.example.filemanager.entity.dto.FileItem;
import com.example.filemanager.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/files")
@CrossOrigin(origins = "*") // 开发时方便前端调用，生产环境应限制
public class FileController {

	@Autowired
	private FileService fileService;

	// 获取目录列表
	@GetMapping("/list")
	public ResponseEntity<List<FileItem>> listFiles(@RequestParam(defaultValue = "") String path) {
		try {
			List<FileItem> items = fileService.listFiles(path);
			return ResponseEntity.ok(items);
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	// 新建文件夹
	@PostMapping("/mkdir")
	public ResponseEntity<String> createDirectory(@RequestParam String path) {
		try {
			fileService.createDirectory(path);
			return ResponseEntity.ok("目录创建成功");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// 重命名
	@PostMapping("/rename")
	public ResponseEntity<String> rename(@RequestParam String oldPath, @RequestParam String newPath) {
		try {
			fileService.rename(oldPath, newPath);
			return ResponseEntity.ok("重命名成功");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// 删除
	@PostMapping("/delete")
	public ResponseEntity<String> delete(@RequestParam String path) {
		try {
			fileService.delete(path);
			return ResponseEntity.ok("删除成功");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// 移动
	@PostMapping("/move")
	public ResponseEntity<String> move(@RequestParam String sourcePath, @RequestParam String targetPath) {
		try {
			fileService.move(sourcePath, targetPath);
			return ResponseEntity.ok("移动成功");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// 上传文件
	@PostMapping("/upload")
	public ResponseEntity<String> uploadFile(@RequestParam String path, @RequestParam("file") MultipartFile file) {
		try {
			fileService.uploadFile(path, file);
			return ResponseEntity.ok("上传成功");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// 下载文件
	@GetMapping("/download")
	public ResponseEntity<Resource> downloadFile(@RequestParam String path) {
		try {
			Resource resource = fileService.downloadFile(path);

			String filename = path.substring(path.lastIndexOf('/') + 1);
			String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

			return ResponseEntity.ok()
			                     .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"")
			                     .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
			                     .body(resource);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	// 新建空文件
	@PostMapping("/createFile")
	public ResponseEntity<String> createFile(@RequestParam String path) {
		try {
			fileService.createFile(path);
			return ResponseEntity.ok("文件创建成功");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}