const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  css: {
    loaderOptions: {
      sass: {
        implementation: require('sass'), // This line must in sass option
      },
    },
  }
})
