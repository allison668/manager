<script setup lang="ts">
  import { ArrowRight } from '@element-plus/icons-vue';
  import { useFileStore } from '@/stores/fileStore.ts';

  const fileStore = useFileStore();
</script>

<template>
  <div class="breadcrumb-container">
    <el-breadcrumb :separator-icon="ArrowRight" class="custom-breadcrumb">
      <el-breadcrumb-item
        v-for="(part, index) in fileStore.breadcrumb"
        :key="index"
        @click="fileStore.navigateTo(part.path)"
        class="breadcrumb-item"
      >
        {{ part.name || '根目录' }}
      </el-breadcrumb-item>
    </el-breadcrumb>
  </div>
</template>

<style scoped>
  /* 面包屑 */
  .breadcrumb-container {
    margin-bottom: 24px;
  }

  .custom-breadcrumb :deep(.el-breadcrumb__item) {
    font-size: 14px;
  }

  .breadcrumb-item {
    cursor: pointer;
    color: #3b82f6;
    transition: color 0.2s;
  }

  .breadcrumb-item:hover {
    color: #1d4ed8;
    text-decoration: underline;
  }
</style>
