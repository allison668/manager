// src/types/FileItem.ts

export interface FileItem {
  name: string;
  path: string;
  directory: boolean;
  size: number;
  lastModified: number;
  type: string;
  extension: string;
  createdTime: number;
}
