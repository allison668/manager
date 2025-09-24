import type { FileItem } from '@/types/FileItem.ts';

export interface ILoginInfo {
  account: string;
  password: string;
}

export type RoleType = 'admin' | 'user' | null;

export interface IUser {
  user: string;
  role: RoleType;
}

export interface IContextMenu {
  visible: boolean;
  x: number;
  y: number;
  target?: FileItem | null;
}

export interface IBreadcrumb {
  name: string;
  path: string;
}

export interface IProperties {
  visible: boolean;
  item: FileItem | null;
}

interface IForm {
  name: string;
  newName: string;
  currentFile: FileItem | null;
}

export interface IDialog {
  folderVisible: boolean;
  fileVisible: boolean;
  renameVisible: boolean;
  form: IForm;
}
