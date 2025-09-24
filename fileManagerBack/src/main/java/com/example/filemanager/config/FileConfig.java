package com.example.filemanager.config;


import com.example.filemanager.FileManagerApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class FileConfig {

	@Bean
	public File dataRootDirectory() {
		// 获取当前 JAR 包所在目录
		String jarPath = FileManagerApplication.class.getProtectionDomain()
		                                             .getCodeSource().getLocation().getPath();
		File jarFile = new File(jarPath);
		File rootDir = jarFile.isFile() ? jarFile.getParentFile() : jarFile;

		// 创建 data 目录
		File dataDir = new File(rootDir, "data");
		if (!dataDir.exists()) {
			dataDir.mkdirs();
		}

		return dataDir;
	}
}
