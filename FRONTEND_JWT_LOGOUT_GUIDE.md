# 前端JWT获取与登出指南

## JWT Token的存储方式

### 1. 登录时获取Token

当用户登录成功后，后端返回JWT token：

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": "2",
    "username": "test",
    "nickname": "测试用户",
    "avatar": null,
    "roles": ["ROLE_USER"],
    "permissions": ["task:list:view"],
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600
  },
  "timestamp": 1753954732931,
  "success": true
}
```

### 2. 前端存储Token的方式

#### 2.1 本地存储 (localStorage)
```javascript
// 登录成功后保存token
const handleLogin = async (loginData) => {
  try {
    const response = await fetch('/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(loginData)
    });
    
    const result = await response.json();
    
    if (result.success) {
      // 保存token到localStorage
      localStorage.setItem('jwt_token', result.data.token);
      localStorage.setItem('user_info', JSON.stringify(result.data));
      
      // 跳转到主页
      navigate('/dashboard');
    }
  } catch (error) {
    console.error('登录失败:', error);
  }
};
```

#### 2.2 会话存储 (sessionStorage)
```javascript
// 使用sessionStorage（页面关闭后清除）
sessionStorage.setItem('jwt_token', result.data.token);
```

#### 2.3 Cookie存储
```javascript
// 设置Cookie（可设置过期时间）
document.cookie = `jwt_token=${result.data.token}; max-age=${result.data.expiresIn}; path=/; secure; samesite=strict`;
```

#### 2.4 内存存储（推荐）
```javascript
// 使用状态管理（React/Vue）
const [authToken, setAuthToken] = useState(null);
const [userInfo, setUserInfo] = useState(null);

// 登录成功后
setAuthToken(result.data.token);
setUserInfo(result.data);
```

## 登出时获取JWT的方式

### 1. 从存储中获取Token

#### 1.1 从localStorage获取
```javascript
const handleLogout = async () => {
  // 从localStorage获取token
  const token = localStorage.getItem('jwt_token');
  
  if (token) {
    try {
      // 调用登出接口
      const response = await fetch('/auth/logout', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      const result = await response.json();
      
      if (result.success) {
        // 清除本地存储
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('user_info');
        
        // 跳转到登录页
        navigate('/login');
      }
    } catch (error) {
      console.error('登出失败:', error);
    }
  }
};
```

#### 1.2 从状态管理获取
```javascript
// React示例
const handleLogout = async () => {
  if (authToken) {
    try {
      const response = await fetch('/auth/logout', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${authToken}`,
          'Content-Type': 'application/json'
        }
      });
      
      const result = await response.json();
      
      if (result.success) {
        // 清除状态
        setAuthToken(null);
        setUserInfo(null);
        
        // 清除本地存储
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('user_info');
        
        navigate('/login');
      }
    } catch (error) {
      console.error('登出失败:', error);
    }
  }
};
```

### 2. 自动获取Token的封装

#### 2.1 创建HTTP拦截器
```javascript
// axios拦截器示例
import axios from 'axios';

// 请求拦截器 - 自动添加token
axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('jwt_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器 - 处理token过期
axios.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      // token过期，自动登出
      localStorage.removeItem('jwt_token');
      localStorage.removeItem('user_info');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

#### 2.2 封装API请求
```javascript
// api.js
class ApiService {
  constructor() {
    this.baseURL = '/api';
  }
  
  // 获取token
  getToken() {
    return localStorage.getItem('jwt_token');
  }
  
  // 设置请求头
  getHeaders() {
    const token = this.getToken();
    return {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` })
    };
  }
  
  // 登出请求
  async logout() {
    const token = this.getToken();
    if (!token) {
      return { success: true, message: '未登录状态' };
    }
    
    try {
      const response = await fetch(`${this.baseURL}/auth/logout`, {
        method: 'POST',
        headers: this.getHeaders()
      });
      
      return await response.json();
    } catch (error) {
      console.error('登出请求失败:', error);
      return { success: false, message: '网络错误' };
    }
  }
}

export const apiService = new ApiService();
```

## 完整的登出流程

### 1. 前端登出组件示例

#### 1.1 React组件
```jsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const LogoutButton = () => {
  const [isLoggingOut, setIsLoggingOut] = useState(false);
  const navigate = useNavigate();
  
  const handleLogout = async () => {
    setIsLoggingOut(true);
    
    try {
      // 1. 获取token
      const token = localStorage.getItem('jwt_token');
      
      if (!token) {
        // 没有token，直接跳转登录页
        navigate('/login');
        return;
      }
      
      // 2. 调用登出接口
      const response = await fetch('/auth/logout', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      const result = await response.json();
      
      // 3. 清除本地数据
      localStorage.removeItem('jwt_token');
      localStorage.removeItem('user_info');
      sessionStorage.clear();
      
      // 4. 清除Cookie
      document.cookie = 'jwt_token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
      
      // 5. 跳转登录页
      navigate('/login');
      
    } catch (error) {
      console.error('登出失败:', error);
      // 即使请求失败，也要清除本地数据
      localStorage.removeItem('jwt_token');
      localStorage.removeItem('user_info');
      navigate('/login');
    } finally {
      setIsLoggingOut(false);
    }
  };
  
  return (
    <button 
      onClick={handleLogout} 
      disabled={isLoggingOut}
    >
      {isLoggingOut ? '登出中...' : '登出'}
    </button>
  );
};

export default LogoutButton;
```

#### 1.2 Vue组件
```vue
<template>
  <button @click="handleLogout" :disabled="isLoggingOut">
    {{ isLoggingOut ? '登出中...' : '登出' }}
  </button>
</template>

<script>
export default {
  data() {
    return {
      isLoggingOut: false
    };
  },
  
  methods: {
    async handleLogout() {
      this.isLoggingOut = true;
      
      try {
        // 获取token
        const token = localStorage.getItem('jwt_token');
        
        if (token) {
          // 调用登出接口
          await this.$http.post('/auth/logout', {}, {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          });
        }
        
        // 清除本地数据
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('user_info');
        
        // 跳转登录页
        this.$router.push('/login');
        
      } catch (error) {
        console.error('登出失败:', error);
        // 即使失败也要清除本地数据
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('user_info');
        this.$router.push('/login');
      } finally {
        this.isLoggingOut = false;
      }
    }
  }
};
</script>
```

## 安全考虑

### 1. Token存储安全
- **避免XSS攻击**: 使用HttpOnly Cookie
- **避免CSRF攻击**: 设置SameSite属性
- **HTTPS**: 生产环境必须使用HTTPS

### 2. 登出安全
- **双重清除**: 清除本地存储 + 调用后端接口
- **强制跳转**: 登出后强制跳转到登录页
- **状态重置**: 清除所有用户相关状态

### 3. 错误处理
- **网络错误**: 即使请求失败也要清除本地数据
- **Token过期**: 自动检测并处理过期情况
- **用户提示**: 给用户明确的反馈信息

## 最佳实践

### 1. 统一的Token管理
```javascript
// tokenManager.js
class TokenManager {
  static getToken() {
    return localStorage.getItem('jwt_token');
  }
  
  static setToken(token) {
    localStorage.setItem('jwt_token', token);
  }
  
  static removeToken() {
    localStorage.removeItem('jwt_token');
  }
  
  static isTokenValid() {
    const token = this.getToken();
    if (!token) return false;
    
    try {
      // 解析JWT（不验证签名）
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp * 1000 > Date.now();
    } catch {
      return false;
    }
  }
}

export default TokenManager;
```

### 2. 自动登出机制
```javascript
// 监听页面可见性变化
document.addEventListener('visibilitychange', () => {
  if (document.visibilityState === 'visible') {
    // 页面重新可见时检查token
    if (!TokenManager.isTokenValid()) {
      // token过期，自动登出
      handleAutoLogout();
    }
  }
});

// 定期检查token
setInterval(() => {
  if (!TokenManager.isTokenValid()) {
    handleAutoLogout();
  }
}, 60000); // 每分钟检查一次
```

## 总结

前端在登出时获取JWT token的方式：

1. **从本地存储获取**: localStorage、sessionStorage、Cookie
2. **从状态管理获取**: React state、Vue data、Redux等
3. **通过拦截器自动获取**: axios拦截器、fetch封装
4. **通过API服务获取**: 封装的API服务类

关键是要确保：
- **安全获取**: 从可信的存储位置获取
- **完整清除**: 清除所有相关的本地数据
- **错误处理**: 即使请求失败也要清除本地数据
- **用户体验**: 提供清晰的反馈和状态提示 