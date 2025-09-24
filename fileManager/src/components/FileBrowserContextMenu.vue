<script setup lang="ts">
  import { Delete, Download, Edit, FolderOpened, InfoFilled } from '@element-plus/icons-vue';
  import fileApi from '@/api/fileApi.ts';
  import { ref } from 'vue';
  import type { FileItem } from '@/types/FileItem.ts';
  import { onClickOutside, onKeyStroke } from '@vueuse/core';
  import { useFileStore } from '@/stores/fileStore.ts';
  import { ElMessage, ElMessageBox } from 'element-plus';

  const fileStore = useFileStore();
  // 右键菜单状态

  const handleDownload = () => {
    const item = props.target;
    if (!item) return;
    hideContextMenu();
    if (!item.directory) {
      fileApi.downloadFile(item.path);
    }
  };

  const contextMenuRef = ref<HTMLElement | null>(null);

  // 隐藏菜单
  const hideContextMenu = () => {
    fileStore.contextMenu.visible = false;
  };

  // 监听点击外部关闭
  onClickOutside(
    contextMenuRef, // 我们稍后绑定到菜单容器
    () => {
      hideContextMenu();
    }
  );

  // 监听 ESC 键
  onKeyStroke('Escape', () => {
    hideContextMenu();
  });

  // 菜单操作
  const handleOpen = () => {
    const item = fileStore.contextMenu.target;
    if (!item) return;
    hideContextMenu();
    if (item.directory) {
      fileStore.loadFiles(item.path);
    }
  };

  const handleRename = () => {
    const item = fileStore.contextMenu.target;
    if (!item) return;
    hideContextMenu();
    renameFileAction(item);
  };

  const handleDelete = () => {
    const item = fileStore.contextMenu.target;
    if (!item) return;
    hideContextMenu();
    deleteFileAction(item);
  };

  const handleProperties = () => {
    const item = fileStore.contextMenu.target;
    if (!item) return;
    hideContextMenu();
    fileStore.showProperties(item);
  };

  // 重命名
  const renameFileAction = (row: FileItem) => {
    fileStore.dialog.form.newName = row.name;
    fileStore.dialog.form.currentFile = row;
    showRenameDialog();
  };

  const showRenameDialog = () => {
    // 显示重命名对话框
    fileStore.dialog.renameVisible = true;
  };

  // 删除
  const deleteFileAction = (row: FileItem) => {
    ElMessageBox.confirm(`确定删除 "${row.name}" 吗？`, '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(async () => {
      try {
        await fileApi.deleteFile(row.path);
        ElMessage.success('删除成功');
        await fileStore.loadFiles(fileStore.currentPath);
      } catch (error) {
        ElMessage.error('删除失败：' + (error as any).message);
      }
    });
  };
</script>

<template>
  <div
    v-if="fileStore.contextMenu.visible"
    class="context-menu"
    :style="{ left: fileStore.contextMenu.x + 'px', top: fileStore.contextMenu.y + 'px' }"
    ref="contextMenuRef"
  >
    <div
      class="context-menu-item"
      @click="handleOpen"
      v-if="fileStore.contextMenu.target?.directory"
    >
      <FolderOpened class="menu-icon" />
      打开
    </div>
    <div class="context-menu-item" @click="handleDownload" v-else>
      <Download class="menu-icon" />
      下载
    </div>
    <div class="context-menu-divider"></div>
    <div class="context-menu-item" @click="handleRename">
      <Edit class="menu-icon" />
      重命名
    </div>
    <div class="context-menu-item" @click="handleProperties">
      <InfoFilled class="menu-icon" />
      属性
    </div>
    <div class="context-menu-divider"></div>
    <div class="context-menu-item danger" @click="handleDelete">
      <Delete class="menu-icon" />
      删除
    </div>
  </div>
</template>

<style scoped>
  /* ========== 右键菜单样式 ========== */
  .context-menu {
    position: fixed;
    z-index: 2000;
    background: #fff;
    border: 1px solid #ebeef5;
    border-radius: 8px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
    padding: 8px 0;
    min-width: 180px;
    font-size: 15px;
  }

  /* 右键菜单项 */
  .context-menu-item {
    display: flex;
    align-items: center;
    padding: 12px 18px;
    color: #475569;
    cursor: pointer;
    transition: all 0.2s ease;
    border-radius: 6px;
    margin: 0 4px;
  }

  .context-menu-item:hover {
    background-color: #f1f5f9;
    color: #3b82f6;
  }

  .context-menu-item.danger:hover {
    color: #ef4444;
    background-color: #fee2e2;
  }

  .menu-icon {
    width: 18px;
    height: 18px;
    margin-right: 12px;
    color: inherit;
  }

  .context-menu-divider {
    height: 1px;
    background-color: #e2e8f0;
    margin: 6px 0;
  }
</style>
