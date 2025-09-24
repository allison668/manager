<script setup lang="ts">
  import { DocumentAdd, Folder, Upload } from '@element-plus/icons-vue';
  import { useFileStore } from '@/stores/fileStore.ts';
  import fileApi from '@/api/fileApi.ts';
  import { ElMessage } from 'element-plus';
  import { nextTick, ref } from 'vue';

  const fileStore = useFileStore();

  // 创建模板引用
  const folderInputRef = ref<InputInstance | null>(null);
  const fileInputRef = ref<InputInstance | null>(null);
  const renameInputRef = ref<InputInstance | null>(null);

  // 对话框打开后的回调函数
  const onFolderDialogOpened = () => {
    nextTick(() => {
      if (folderInputRef.value) {
        folderInputRef.value.focus();
      }
    });
  };

  const onFileDialogOpened = () => {
    nextTick(() => {
      if (fileInputRef.value) {
        fileInputRef.value.focus();
      }
    });
  };

  const onRenameDialogOpened = () => {
    nextTick(() => {
      if (renameInputRef.value) {
        renameInputRef.value.focus();
      }
    });
  };

  // 新建文件夹
  const showCreateFolderDialog = () => {
    fileStore.dialog.form.name = '';
    fileStore.dialog.folderVisible = true;
  };

  const confirmCreateFolder = async () => {
    if (!fileStore.dialog.form.name.trim()) {
      ElMessage.warning('请输入文件夹名称');
      return;
    }
    try {
      const newPath = fileStore.currentPath
        ? `${fileStore.currentPath}/${fileStore.dialog.form.name}`
        : fileStore.dialog.form.name;
      await fileApi.createDirectory(newPath);
      ElMessage.success('文件夹创建成功');
      fileStore.dialog.folderVisible = false;
      await fileStore.loadFiles(fileStore.currentPath); // 刷新
    } catch (error) {
      ElMessage.error('创建失败：' + (error as any).message);
    }
  };

  // 新建文件
  const showCreateFileDialog = () => {
    fileStore.dialog.form.name = '';
    fileStore.dialog.fileVisible = true;
  };

  const confirmCreateFile = async () => {
    if (!fileStore.dialog.form.name.trim()) {
      ElMessage.warning('请输入文件名');
      return;
    }
    try {
      const newPath = fileStore.currentPath
        ? `${fileStore.currentPath}/${fileStore.dialog.form.name}`
        : fileStore.dialog.form.name;
      await fileApi.createFile(newPath);
      ElMessage.success('文件创建成功');
      fileStore.dialog.fileVisible = false;
      await fileStore.loadFiles(fileStore.currentPath);
    } catch (error) {
      ElMessage.error('创建失败：' + (error as any).message);
    }
  };

  const confirmRename = async () => {
    if (!fileStore.dialog.form.newName.trim()) {
      ElMessage.warning('请输入新名称');
      return;
    }
    // 添加安全检查
    if (!fileStore.dialog.form.currentFile) {
      ElMessage.error('未选择文件');
      return;
    }

    const oldPath = fileStore.dialog.form.currentFile.path;
    const oldDir = oldPath.substring(0, oldPath.lastIndexOf('/') + 1);
    const newPath = oldDir + fileStore.dialog.form.newName;

    try {
      await fileApi.renameFile(oldPath, newPath);
      ElMessage.success('重命名成功');
      fileStore.dialog.renameVisible = false;
      await fileStore.loadFiles(fileStore.currentPath);
    } catch (error) {
      ElMessage.error('重命名失败：' + (error as any).message);
    }
  };

  // 自定义上传处理
  const handleUpload = async (options: any) => {
    const { file } = options;
    try {
      const uploadPath = fileStore.currentPath
        ? `${fileStore.currentPath}/${file.name}`
        : file.name;
      await fileApi.uploadFile(uploadPath, file);
      ElMessage.success('上传成功');
      await fileStore.loadFiles(fileStore.currentPath);
    } catch (error) {
      console.log(error);
      ElMessage.error('上传失败：' + (error as any).message);
    }
  };

  // 上传前检查
  const beforeUpload = (rawFile: File) => {
    if (!rawFile.name) {
      ElMessage.error('文件名无效');
      return false;
    }
    return true;
  };
</script>

<template>
  <div class="toolbar-container">
    <div class="actions-toolbar">
      <div class="primary-actions">
        <el-button
          type="primary"
          @click="showCreateFolderDialog"
          size="default"
          class="action-btn create-folder-btn"
        >
          <Folder class="icon" />
          <span>新建文件夹</span>
        </el-button>
        <el-button
          type="success"
          @click="showCreateFileDialog"
          size="default"
          class="action-btn create-file-btn"
        >
          <DocumentAdd class="icon" />
          <span>新建文件</span>
        </el-button>
      </div>
      <div class="secondary-actions">
        <el-upload
          :show-file-list="false"
          :http-request="handleUpload"
          :before-upload="beforeUpload"
          class="upload-wrapper"
        >
          <el-button type="primary" size="default" class="action-btn upload-btn">
            <Upload class="icon" />
            <span>上传文件</span>
          </el-button>
        </el-upload>
      </div>
    </div>
  </div>

  <!-- 新建文件夹对话框 -->
  <!-- 新建文件夹对话框 -->
  <el-dialog
    v-model="fileStore.dialog.folderVisible"
    title="📂 新建文件夹"
    width="30%"
    @opened="onFolderDialogOpened"
  >
    <el-form :model="fileStore.dialog.form" label-width="80px">
      <el-form-item label="文件夹名">
        <el-input
          ref="folderInputRef"
          v-model="fileStore.dialog.form.name"
          placeholder="请输入文件夹名称"
          @keydown.enter.prevent="confirmCreateFolder"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="fileStore.dialog.folderVisible = false">取消</el-button>
      <el-button type="primary" @click="confirmCreateFolder">确定</el-button>
    </template>
  </el-dialog>

  <!-- 新建文件对话框 -->
  <el-dialog
    v-model="fileStore.dialog.fileVisible"
    title="📄 新建文件"
    width="30%"
    @opened="onFileDialogOpened"
  >
    <el-form :model="fileStore.dialog.form" label-width="80px">
      <el-form-item label="文件名">
        <el-input
          ref="fileInputRef"
          v-model="fileStore.dialog.form.name"
          placeholder="例如：readme.txt"
          @keydown.enter.prevent="confirmCreateFile"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="fileStore.dialog.fileVisible = false">取消</el-button>
      <el-button type="primary" @click="confirmCreateFile">确定</el-button>
    </template>
  </el-dialog>

  <el-dialog
    v-model="fileStore.dialog.renameVisible"
    title="✏️ 重命名"
    width="30%"
    @opened="onRenameDialogOpened"
  >
    <el-form :model="fileStore.dialog.form" label-width="80px">
      <el-form-item label="新名称">
        <el-input
          ref="renameInputRef"
          v-model="fileStore.dialog.form.newName"
          :placeholder="`请输入新名称`"
          @keydown.enter.prevent="confirmRename"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="fileStore.dialog.renameVisible = false">取消</el-button>
      <el-button type="primary" @click="confirmRename">确定</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
  /* 工具栏 - 重新设计布局 */
  .toolbar-container {
    margin-bottom: 24px;
  }

  .actions-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 20px;
    flex-wrap: wrap;
  }

  .primary-actions {
    display: flex;
    gap: 16px;
    flex-wrap: wrap;
  }

  .secondary-actions {
    display: flex;
    gap: 16px;
    flex-wrap: wrap;
  }

  .action-btn {
    border-radius: 8px;
    font-weight: 500;
    font-size: 15px;
    padding: 12px 20px;
    height: auto;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s ease;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
    min-width: 120px;
    text-align: center;
  }

  .action-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }

  .action-btn:active {
    transform: translateY(0);
  }

  .action-btn .icon {
    width: 18px;
    height: 18px;
    margin-right: 8px;
    vertical-align: middle;
  }

  /* 不同类型的按钮样式 */
  .create-folder-btn {
    background-color: #3b82f6;
    border-color: #3b82f6;
    color: white;
  }

  .create-folder-btn:hover {
    background-color: #2563eb;
    border-color: #2563eb;
  }

  .create-file-btn {
    background-color: #10b981;
    border-color: #10b981;
    color: white;
  }

  .create-file-btn:hover {
    background-color: #059669;
    border-color: #059669;
  }

  .upload-btn {
    background-color: #6366f1;
    border-color: #6366f1;
    color: white;
  }

  .upload-btn:hover {
    background-color: #4f46e5;
    border-color: #4f46e5;
  }
</style>
