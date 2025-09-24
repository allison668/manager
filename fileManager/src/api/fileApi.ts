// src/api/fileApi.ts

import type { FileItem } from '@/types/FileItem';
import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
});

// 获取文件列表
const getFileList = async (path: string = '') => {
  return api
    .get<FileItem[]>('/files/list', {
      params: { path },
    })
    .then((res) => res.data);
};

// 创建文件夹
const createDirectory = async (path: string) => {
  return api
    .post('/files/mkdir', null, {
      params: { path },
    })
    .then((res) => res.data);
};

// 重命名
const renameFile = async (oldPath: string, newPath: string) => {
  return api
    .post('/files/rename', null, {
      params: { oldPath, newPath },
    })
    .then((res) => res.data);
};

// 删除
const deleteFile = async (path: string) => {
  return api
    .post('/files/delete', null, {
      params: { path },
    })
    .then((res) => res.data);
};

// 移动
const moveFile = async (sourcePath: string, targetPath: string) => {
  return api
    .post('/files/move', null, {
      params: { sourcePath, targetPath },
    })
    .then((res) => res.data);
};

// 上传文件
const uploadFile = async (path: string, file: File) => {
  const formData = new FormData();
  formData.append('file', file);
  return api
    .post('/files/upload', formData, {
      params: { path },
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
    .then((res) => res.data);
};

// 下载文件
const downloadFile = (path: string) => {
  // 使用 window.open 或创建 a 标签下载
  const url = `/api/files/download?path=${encodeURIComponent(path)}`;
  window.open(url, '_blank');
};

// 新建空文件
const createFile = async (path: string) => {
  return api
    .post('/files/createFile', null, {
      params: { path },
    })
    .then((res) => res.data);
};

export default {
  getFileList,
  createDirectory,
  renameFile,
  deleteFile,
  moveFile,
  uploadFile,
  downloadFile,
  createFile,
};
