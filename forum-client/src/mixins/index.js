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
        return srcUrl ? this.$store.state.configure.HOST + srcUrl  : ''
      },
      urlToLink (content){
        if (!content) {
          return "";
        }
        let urlPattern = /(https?:\/\/|www\.)[a-zA-Z_0-9\-@]+(\.\w[a-zA-Z_0-9\-:]+)+(\/[\(\)~#&\-=?\+\%/\.\w]+)?/g;
        content = content.replace(urlPattern, function (match) {
          let href = match;
          if (match.indexOf("http") == -1) {
            href = "http://" + match; //加粗样式
          }
          return '<a style="color: #409EFF" target="_blank" href="' + href + '">' + match + "</a>";
        });
        return content;
      }
    }
}