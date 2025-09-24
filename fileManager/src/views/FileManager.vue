<!-- src/components/FileBrowser.vue -->

<template>
  <div class="file-browser-page">
    <!-- 页面头部 -->
    <FileBrowserHeader></FileBrowserHeader>

    <!-- 面包屑导航 -->
    <FileBrowserBreadcrumb></FileBrowserBreadcrumb>

    <!-- 工具栏 -->
    <!-- 对话框们 -->
    <FileBrowserActions></FileBrowserActions>

    <!-- 文件列表容器 -->
    <FileBrowserListTable></FileBrowserListTable>

    <!-- 属性对话框 -->
    <FileBrowserPropertiesDialog></FileBrowserPropertiesDialog>
    <!-- 右键菜单 -->
    <FileBrowserContextMenu></FileBrowserContextMenu>
  </div>
</template>

<script setup lang="ts">
  import FileBrowserHeader from '@/components/FileBrowserHeader.vue';

  import { onMounted } from 'vue';
  import { useFileStore } from '@/stores/fileStore.ts';
  import FileBrowserBreadcrumb from '@/components/FileBrowserBreadcrumb.vue';
  import FileBrowserActions from '@/components/FileBrowserActions.vue';
  import FileBrowserPropertiesDialog from '@/components/FileBrowserPropertiesDialog.vue';
  import FileBrowserContextMenu from '@/components/FileBrowserContextMenu.vue';
  import FileBrowserListTable from '@/components/FileBrowserListTable.vue';

  const fileStore = useFileStore();
  // ======== 右键菜单逻辑 =========

  // 初始化加载根目录
  onMounted(() => {
    fileStore.loadFiles('');
  });
</script>

<style scoped>
  .file-browser-page {
    padding: 24px 32px;
    background-color: #f8fafc;
    min-height: 100vh;
  }
</style>
