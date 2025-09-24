<script setup lang="ts">
  import { Document, Folder } from '@element-plus/icons-vue';
  import { ref } from 'vue';
  import type { FileItem } from '@/types/FileItem.ts';
  import { ElMessage } from 'element-plus';
  import fileApi from '@/api/fileApi.ts';
  import { useFileStore } from '@/stores/fileStore.ts';

  const fileStore = useFileStore();

  // 拖拽状态
  const dragState = ref({
    isDragging: false,
    draggedItem: null as FileItem | null,
    targetPath: '',
    dragOverItem: null as FileItem | null,
  });

  // 处理拖拽开始
  const handleDragStart = (event: DragEvent, item: FileItem) => {
    if (event.dataTransfer) {
      event.dataTransfer.effectAllowed = 'move';
      event.dataTransfer.setData('text/plain', item.path);
      dragState.value.draggedItem = item;
      dragState.value.isDragging = true;
      // 设置拖拽图标
      const img = new Image();
      img.src = 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7';
      event.dataTransfer.setDragImage(img, 0, 0);
    }
  };

  // 处理拖拽经过
  const handleDragOver = (event: DragEvent, item: FileItem) => {
    event.preventDefault();
    if (event.dataTransfer) {
      event.dataTransfer.dropEffect = 'move';
    }

    // 只允许拖拽到文件夹
    if (!item.directory) {
      return;
    }

    // 更新拖拽目标
    dragState.value.targetPath = item.path;
    dragState.value.dragOverItem = item;
  };

  // 处理拖拽进入
  const handleDragEnter = (event: DragEvent, item: FileItem) => {
    event.preventDefault();
    // 只允许拖拽到文件夹
    if (!item.directory) {
      return;
    }
  };

  // 处理拖拽离开
  const handleDragLeave = (event: DragEvent, item: FileItem) => {
    // 清除目标路径
    if (dragState.value.targetPath === item.path) {
      dragState.value.targetPath = '';
      dragState.value.dragOverItem = null;
    }
  };

  // 处理拖拽释放
  const handleDrop = async (event: DragEvent, targetItem: FileItem) => {
    event.preventDefault();

    // 只允许拖拽到文件夹
    if (!targetItem.directory) {
      ElMessage.warning('只能拖拽到文件夹内');
      dragState.value.isDragging = false;
      dragState.value.targetPath = '';
      dragState.value.dragOverItem = null;
      return;
    }

    const draggedItem = dragState.value.draggedItem;
    if (!draggedItem) {
      dragState.value.isDragging = false;
      dragState.value.targetPath = '';
      dragState.value.dragOverItem = null;
      return;
    }

    // 不能拖拽到自身或自身子目录
    if (
      draggedItem.path === targetItem.path ||
      targetItem.path.startsWith(draggedItem.path + '/')
    ) {
      ElMessage.warning('不能将文件夹拖拽到自身或其子目录中');
      dragState.value.isDragging = false;
      dragState.value.targetPath = '';
      dragState.value.dragOverItem = null;
      return;
    }

    // 构建目标路径
    const fileName = draggedItem.name;
    const targetPath = targetItem.path + '/' + fileName;

    try {
      await fileApi.moveFile(draggedItem.path, targetPath);
      ElMessage.success('移动成功');
      await fileStore.loadFiles(fileStore.currentPath);
    } catch (error) {
      ElMessage.error('移动失败：' + (error as any).message);
    } finally {
      dragState.value.isDragging = false;
      dragState.value.targetPath = '';
      dragState.value.dragOverItem = null;
    }
  };

  const loading = ref(false);

  // 点击文件/文件夹
  const handleClick = (row: FileItem) => {
    if (row.directory) {
      fileStore.loadFiles(row.path);
    } else {
      // 可预览或直接下载
      fileApi.downloadFile(row.path);
    }
  };

  // 格式化大小
  const formatSize = (bytes: number): string => {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  // 格式化日期
  const formatDate = (timestamp: number): string => {
    return new Date(timestamp).toLocaleString();
  };

  const showContextMenu = (event: MouseEvent, row: FileItem) => {
    event.preventDefault();
    fileStore.showContextMenu(event, row);
  };
</script>

<template>
  <div class="file-table-container">
    <el-table
      :data="fileStore.fileList"
      class="file-table"
      v-loading="loading"
      row-key="path"
      border
      :default-sort="{ prop: 'name', order: 'ascending' }"
    >
      <el-table-column prop="name" label="名称" width="320" sortable>
        <template #default="{ row }">
          <div
            @click="handleClick(row)"
            @contextmenu.prevent="showContextMenu($event, row)"
            class="file-name cursor-pointer"
            draggable="true"
            @dragstart="handleDragStart($event, row)"
            @dragover.prevent="handleDragOver($event, row)"
            @drop="handleDrop($event, row)"
            @dragenter="handleDragEnter($event, row)"
            @dragleave="handleDragLeave($event, row)"
          >
            <component :is="row.directory ? Folder : Document" class="file-icon" />
            {{ row.name }}
            <div
              v-if="dragState.isDragging && dragState.targetPath === row.path"
              class="drop-indicator"
            >
              <div class="drop-arrow">↓</div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="type" label="类型" width="140" />
      <el-table-column prop="size" label="大小" width="120" sortable>
        <template #default="{ row }">
          {{ formatSize(row.size) }}
        </template>
      </el-table-column>
      <el-table-column prop="createdTime" label="创建时间" width="180" sortable>
        <template #default="{ row }">
          {{ formatDate(row.createdTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="lastModified" label="修改时间" width="180" sortable>
        <template #default="{ row }">
          {{ formatDate(row.lastModified) }}
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
  /* 文件表格容器 */
  .file-table-container {
    background: #ffffff;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    overflow: hidden;
    padding: 20px;
  }

  .file-table :deep(.el-table) {
    font-size: 14px;
  }

  .file-table :deep(.el-table th) {
    background-color: #f8fafc;
    font-weight: 600;
    color: #334155;
  }

  /* 文件名样式 */
  .file-name {
    cursor: pointer;
    display: inline-flex;
    align-items: center;
    gap: 8px;
    color: #1e293b;
    font-weight: 500;
    transition: all 0.2s ease;
    position: relative;
    padding: 8px 0;
  }

  .file-name:hover {
    color: #3b82f6;
    text-decoration: underline;
    transform: scale(1.02);
  }

  .file-icon {
    width: 18px;
    height: 18px;
    color: #64748b;
  }

  .file-name:hover .file-icon {
    color: #3b82f6;
  }

  /* 拖拽效果 */
  .file-name[draggable='true'] {
    cursor: grab;
  }

  .file-name[draggable='true']:active {
    cursor: grabbing;
  }

  /* 拖拽目标高亮 */
  .file-name.drag-over {
    background-color: #dbeafe;
    border-radius: 4px;
  }

  /* 拖拽指示器 */
  .drop-indicator {
    position: absolute;
    right: 0;
    top: 50%;
    transform: translateY(-50%);
    color: #3b82f6;
    font-weight: bold;
    font-size: 18px;
    animation: bounce 0.6s infinite;
  }

  @keyframes bounce {
    0%,
    100% {
      transform: translateY(-50%) scale(1);
    }
    50% {
      transform: translateY(-50%) scale(1.2);
    }
  }
</style>
