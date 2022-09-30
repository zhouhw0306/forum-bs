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
        return srcUrl ? this.$store.state.configure.HOST + srcUrl || '../assets/img/user.jpg' : ''
      },
    }
}