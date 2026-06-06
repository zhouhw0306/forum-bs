export const mixin = {
    methods: {
      // 提示信息
      notify (title, type) {
        this.$notify({
          title: title,
          type: type
        })
      },
      getUrl (url) {
        return `${this.$store.state.HOST}/${url}`
      },
      // 获取图片信息
      attachImageUrl (srcUrl) {
        return srcUrl ? (srcUrl.startsWith('http') ? srcUrl : this.$store.state.configure.HOST + srcUrl)  : ''
      },
      // 头像加载失败时生成字母头像
      avatarFallback(name, size = 40) {
        const canvas = document.createElement('canvas');
        canvas.width = size; canvas.height = size;
        const ctx = canvas.getContext('2d');
        const color = this._avatarColor(name);
        ctx.fillStyle = color;
        ctx.fillRect(0, 0, size, size);
        ctx.fillStyle = '#fff';
        ctx.font = `bold ${size * 0.45}px "Helvetica Neue",Arial,sans-serif`;
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        ctx.fillText((name || '?').charAt(0).toUpperCase(), size / 2, size / 2);
        return canvas.toDataURL();
      },
      _avatarColor(str) {
        const colors = [
          '#409eff','#67c23a','#e6a23c','#f56c6c','#9b59b6',
          '#1abc9c','#e74c3c','#3498db','#2ecc71','#e67e22',
          '#16a085','#c0392b','#2980b9','#27ae60','#f39c12',
        ];
        let hash = 0;
        for (let i = 0; i < (str || '').length; i++) {
          hash = str.charCodeAt(i) + ((hash << 5) - hash);
        }
        return colors[Math.abs(hash) % colors.length];
      },
      urlToLink (content){
        if (!content) {
          return "";
        }
        let urlPattern = /(https?:\/\/|www\.)[a-zA-Z_0-9\-@]+(\.\w[a-zA-Z_0-9\-:]+)+(\/[\(\)~#&\-=?\+\%/\.\w]+)?/g;
        content = content.replace(urlPattern, function (match) {
          let href = match;
          if (match.indexOf("http") === -1) {
            href = "http://" + match; //加粗样式
          }
          return '<a style="color: #409EFF" target="_blank" href="' + href + '">' + match + "</a>";
        });
        return content;
      }
    }
}