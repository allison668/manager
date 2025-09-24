import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import type { IBreadcrumb, IContextMenu, IDialog } from '@/types/interface.ts';
import fileApi from '@/api/fileApi.ts';
import { ElMessage } from 'element-plus';
import type { FileItem } from '@/types/FileItem.ts';

export const useFileStore = defineStore('file', () => {
  const currentPath = ref<string>('');
  const fileList = ref<FileItem[]>([]);

  const propertiesDialog = ref({
    visible: false,
    item: null as FileItem | null,
  });
  const showProperties = (item: FileItem) => {
    propertiesDialog.value.item = item;
    propertiesDialog.value.visible = true;
  };
  const dialog = ref<IDialog>({
    folderVisible: false,
    fileVisible: false,
    renameVisible: false,
    form: {
      name: '',
      newName: '',
      currentFile: null as FileItem | null,
    },
  });

  const contextMenu = ref<IContextMenu>({
    visible: false,
    x: 0,
    y: 0,
    target: null as FileItem | null,
  });

  const showContextMenu = (event: MouseEvent, row: FileItem) => {
    event.preventDefault();
    contextMenu.value.target = row;
    // 计算菜单位置（防止超出视口）
    const menuWidth = 200;
    const menuHeight = 220; // 菜单高度增加，因为增加了更多选项
    const x =
      event.clientX + menuWidth > window.innerWidth ? event.clientX - menuWidth : event.clientX;
    const y =
      event.clientY + menuHeight > window.innerHeight ? event.clientY - menuHeight : event.clientY;
    contextMenu.value.x = x;
    contextMenu.value.y = y;
    contextMenu.value.visible = true;
  };

  // 面包屑
  const breadcrumb = computed<IBreadcrumb[]>(() => {
    const parts = currentPath.value.split('/').filter(Boolean);
    const crumbs = [{ name: '', path: '' }];
    let path = '';
    parts.forEach((part, index) => {
      path += (index > 0 ? '/' : '') + part;
      crumbs.push({
        name: part,
        path: path,
      });
    });
    return crumbs;
  });

  // 加载当前目录
  const loadFiles = async (path = '') => {
    // loading.value = true;
    try {
      currentPath.value = path;
      fileList.value = await fileApi.getFileList(path);
    } catch (error) {
      console.log(error);
      ElMessage.error('加载文件列表失败');
    } finally {
      // loading.value = false;
    }
  };

  // 导航到指定路径
  const navigateTo = async (path: string) => {
    await loadFiles(path);
  };

  return {
    fileList,
    currentPath,
    dialog,
    propertiesDialog,
    contextMenu,

    breadcrumb,
    loadFiles,
    navigateTo,
    showProperties,
    showContextMenu,
  };
});
