class Websocket {
  constructor(userId) {
    this.userId = userId;
    this.ws = null;
    this.reconnectInterval = 5000; // 重连间隔5秒
    this.maxReconnectAttempts = 5; // 最大重连次数
    this.reconnectAttempts = 0;
    this.isManuallyClosed = false;
    this.isInitialized = false; // 添加初始化状态标志
  }

  // 检查是否可以初始化
  canInitialize() {
    return this.userId && this.userId !== '';
  }

  // 初始化WebSocket连接
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
    const host = 'localhost:8888'
    const wsUrl = `${protocol}//${host}/websocket/${this.userId}`;

    this.ws = new WebSocket(wsUrl);
    this.isInitialized = true;

    // 连接建立成功的回调
    this.ws.onopen = (event) => {
      console.log('WebSocket连接已建立', event);
      this.reconnectAttempts = 0; // 重置重连次数
    };

    // 接收到消息的回调
    this.ws.onmessage = (event) => {
      console.log('收到WebSocket消息:', event.data);
      try {
        const data = JSON.parse(event.data);
        this.handleMessage(data?.data);
      } catch (error) {
        console.error('解析WebSocket消息失败:', error);
      }
    };

    // 连接关闭的回调
    this.ws.onclose = (event) => {
      console.log('WebSocket连接已关闭', event);
      this.isInitialized = false;
      if (!this.isManuallyClosed && this.reconnectAttempts < this.maxReconnectAttempts) {
        console.log(`尝试重连(${this.reconnectAttempts + 1}/${this.maxReconnectAttempts})...`);
        setTimeout(() => {
          this.reconnectAttempts++;
          this.init();
        }, this.reconnectInterval);
      }
    };

    // 连接错误的回调
    this.ws.onerror = (error) => {
      console.error('WebSocket连接发生错误:', error);
    };

    return true;
  }
  handleMessage(data) {
    // 使用绑定的Vue实例的$notify方法显示通知
    if (this.$notify) {
      this.$notify({
        title: data.title || '通知',
        message: data.content || '您有一条新消息',
        type: data.type || 'info',
        duration: 5000,
        position: 'top-right'
      });
    } else {
      console.warn('Vue实例未绑定，无法显示通知');
    }
  }

  // 关闭连接
  close() {
    this.isManuallyClosed = true;
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.close();
    }
    this.isInitialized = false;
    console.log('WebSocket连接已关闭');
  }
}
export default Websocket;