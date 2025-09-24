<template>
  <div class="login-main">
    <div class="login-box">
      <h3 class="title">欢迎登录-文件管理系统</h3>

      <el-form
        ref="loginFormRef"
        :model="form"
        :rules="rules"
        label-position="left"
        label-width="60px"
        hide-required-asterisk
        @submit.prevent="handleLogin"
        @keyup.enter="handleLogin"
      >
        <!-- 账号 -->
        <el-form-item label="账号" prop="account">
          <el-input
            v-model="form.account"
            placeholder="请输入用户名"
            autocomplete="username"
            clearable
          />
        </el-form-item>

        <!-- 密码 -->
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            autocomplete="current-password"
            show-password
            clearable
          />
        </el-form-item>

        <!-- 登录按钮 -->
        <el-form-item>
          <div class="btn-container">
            <el-button type="primary" @click="handleLogin" :loading="loading" class="login-btn">
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script lang="ts" setup>
  import { useUserStore } from '@/stores/userStore';
  import type { FormInstance } from 'element-plus';
  import { ElMessage } from 'element-plus';
  import { reactive, ref } from 'vue';
  import { useRouter } from 'vue-router';

  const router = useRouter();
  // 表单数据
  const form = reactive({
    account: '',
    password: '',
  });

  // 表单验证规则
  const rules = reactive({
    account: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      {
        pattern: /^[a-zA-Z0-9_@.-]+$/,
        message: '用户名只能包含字母、数字和特殊字符(_@.-)',
        trigger: 'blur',
      },
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, message: '密码至少3位', trigger: 'blur' },
    ],
  });

  // 控制加载状态
  const loading = ref(false);

  // 表单实例
  const loginFormRef = ref<FormInstance>();

  // 登录处理
  const handleLogin = async () => {
    if (!loginFormRef.value || loading.value) return;
    //模拟登录处理
    await loginFormRef.value.validate(async (valid) => {
      if (!valid || loading.value) return;
      loading.value = true;

      const userStore = useUserStore();
      if (userStore.login(form)) {
        await router.push('/file-manager');
        ElMessage.success('登录成功');
        loading.value = false;
        return;
      }
      ElMessage.error('登录失败，请重新登录');
      loading.value = false;
    });
  };
</script>

<style scoped>
  .login-main {
    width: 100%;
    height: 100vh;
    background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
    background-size: cover;
    display: flex;
    align-items: center;
    justify-content: center;
    font-family:
      -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans',
      'Helvetica Neue', sans-serif;
  }

  .login-box {
    width: 380px;
    padding: 30px 25px;
    background-color: #ffffff;
    border-radius: 12px;
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.3);
  }

  .title {
    text-align: center;
    font-size: 20px;
    color: #333;
    margin-bottom: 25px;
    font-weight: 600;
  }

  .captcha {
    width: 100%;
    height: 100%;
    display: flex;
  }

  .captcha-input {
    width: 70%;
    margin-right: 20px;
  }

  .captcha-container {
    width: 30%;
    min-height: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f5f7fa;
    border-radius: 4px;
    overflow: hidden;
  }

  .captcha-img {
    max-width: 100%;
    max-height: 100%;
    cursor: pointer;
    user-select: none;
    transition: transform 0.2s ease;
  }

  .captcha-img:hover {
    transform: scale(1.1);
  }

  .captcha-loading {
    color: #999;
    font-size: 12px;
  }

  .extra-links {
    text-align: right;
    font-size: 14px;
    margin-top: 10px;
  }

  .extra-links a {
    color: #666;
    text-decoration: none;
    transition: color 0.3s;
  }

  .extra-links a:hover {
    color: #4abf8a;
  }

  .btn-container {
    width: 100%;
  }

  .login-btn {
    width: 100%;
  }
</style>
