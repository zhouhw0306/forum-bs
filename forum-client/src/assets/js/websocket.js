class Websocket {
  constructor(userId) {
    this.userId = userId;
    this.ws = null;
    this.reconnectInterval = 5000;
    this.maxReconnectAttempts = 5;
    this.reconnectAttempts = 0;
    this.isManuallyClosed = false;
    this.isInitialized = false;
    // 使用回调数组支持多个监听者
    this.listeners = [];
  }

  canInitialize() {
    return this.userId && this.userId !== '';
  }

  // 注册消息监听回调
  onMessage(callback) {
    if (typeof callback === 'function') {
      this.listeners.push(callback);
    }
  }

  init() {
    if (!this.canInitialize()) {
      console.warn('用户未登录，无法初始化WebSocket连接');
      return false;
    }

    if (typeof WebSocket === 'undefined') {
      console.error('您的浏览器不支持WebSocket');
      return false;
    }

    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const host = 'localhost:8888';
    const wsUrl = `${protocol}//${host}/websocket/${this.userId}`;

    this.ws = new WebSocket(wsUrl);
    this.isInitialized = true;

    this.ws.onopen = () => {
      console.log('WebSocket连接已建立');
      this.reconnectAttempts = 0;
    };

    this.ws.onmessage = (event) => {
      console.log('收到WebSocket消息:', event.data);
      try {
        const raw = JSON.parse(event.data);
        // 通知消息嵌套在 data 字段中
        const msg = raw.data || raw;
        // 通知所有监听者
        this.listeners.forEach(fn => {
          try { fn(msg); } catch (e) { console.error('通知回调执行失败:', e); }
        });
      } catch (error) {
        console.error('解析WebSocket消息失败:', error);
      }
    };

    this.ws.onclose = () => {
      console.log('WebSocket连接已关闭');
      this.isInitialized = false;
      if (!this.isManuallyClosed && this.reconnectAttempts < this.maxReconnectAttempts) {
        this.reconnectAttempts++;
        console.log(`尝试重连(${this.reconnectAttempts}/${this.maxReconnectAttempts})...`);
        setTimeout(() => this.init(), this.reconnectInterval);
      }
    };

    this.ws.onerror = (error) => {
      console.error('WebSocket连接发生错误:', error);
    };

    return true;
  }

  close() {
    this.isManuallyClosed = true;
    this.listeners = [];
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.close();
    }
    this.isInitialized = false;
    console.log('WebSocket连接已关闭');
  }
}
export default Websocket;