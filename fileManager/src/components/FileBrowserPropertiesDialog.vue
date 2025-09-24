<script setup lang="ts">
  // 属性弹窗
  import { formatDate, formatSize } from '@/utils/format.ts';
  import { useFileStore } from '@/stores/fileStore.ts';

  const fileStore = useFileStore();
</script>

<template>
  <!-- 属性对话框 -->
  <el-dialog
    v-model="fileStore.propertiesDialog.visible"
    title="📄 文件属性"
    width="400px"
    :close-on-click-modal="false"
  >
    <div v-if="fileStore.propertiesDialog.item" class="properties-content">
      <div class="prop-item">
        <label>名称：</label>
        <span>{{ fileStore.propertiesDialog.item.name }}</span>
      </div>
      <div class="prop-item">
        <label>路径：</label>
        <span class="path-text">{{ fileStore.propertiesDialog.item.path }}</span>
      </div>
      <div class="prop-item">
        <label>类型：</label>
        <span>{{
          fileStore.propertiesDialog.item.directory
            ? '文件夹'
            : fileStore.propertiesDialog.item.type
        }}</span>
      </div>
      <div class="prop-item" v-if="!fileStore.propertiesDialog.item.directory">
        <label>大小：</label>
        <span>{{ formatSize(fileStore.propertiesDialog.item.size) }}</span>
      </div>
      <div class="prop-item">
        <label>修改时间：</label>
        <span>{{ formatDate(fileStore.propertiesDialog.item.lastModified) }}</span>
      </div>
      <div class="prop-item">
        <label>创建时间：</label>
        <span>{{ formatDate(fileStore.propertiesDialog.item.lastModified) }}</span>
        <!-- 如有真实创建时间可替换 -->
      </div>
    </div>
    <template #footer>
      <el-button @click="fileStore.propertiesDialog.visible = false" type="primary">关闭</el-button>
    </template>
  </el-dialog>
</template>
