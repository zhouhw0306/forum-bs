<template>
  <div class="main" :style="this.bg">
    <div id="he-plugin-simple"></div>

    <div class="container">
      <i class="fa fa-windows item" aria-hidden="true"></i>
      <div class="search-box">
        <input type="text" v-model="words" @keyup.enter="searchWords" class="search-btn" placeholder="搜索">
      </div>
      <a href=""><i class="fa fa-search item" aria-hidden="true" @click="searchWords"></i></a>

    </div>

    <div class="timeBox">
      {{ dateFormat(date) }}
    </div>

    <div>
      <div>

      </div>
      <span></span>
    </div>

  </div>

</template>

<script>

import {initBg} from "@/api";

export default {
  name: 'Nav',
  data(){
    return{
      words: '',
      date: new Date(),
      bg:'',
      fits: ['fill', 'contain', 'cover', 'none', 'scale-down'],
      url: 'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg'
    }
  },
  methods:{
    searchWords(){
      window.open(`https://cn.bing.com/search?q=${this.words}`)
    },
    dateFormat(time){
      let date = new Date(time);
      let hours = date.getHours()<10? "0"+date.getHours() : date.getHours()
      let minutes = date.getMinutes()<10? "0"+date.getMinutes() : date.getMinutes()
      let seconds = date.getSeconds()<10? "0"+date.getSeconds() : date.getSeconds()
      return hours+':'+minutes+':'+seconds
    }
  },
  mounted() {
    let _this = this
    this.timer = setInterval(() => {
      _this.date = new Date();
    },1000)
    window.WIDGET = {
      "CONFIG": {
        "modules": "01234",
        "background": "5",
        "tmpColor": "FFFFFF",
        "tmpSize": "16",
        "cityColor": "FFFFFF",
        "citySize": "16",
        "aqiColor": "FFFFFF",
        "aqiSize": "16",
        "weatherIconSize": "24",
        "alertIconSize": "18",
        "padding": "10px 10px 10px 10px",
        "shadow": "0",
        "language": "auto",
        "fixed": "true",
        "vertical": "top",
        "horizontal": "left",
        "left": "10",
        "top": "70",
        "key": "502230457c8c421f9ff128635d39cc46"
      }
    }
    let script = document.createElement("script")
    script.type = "text/javascript"
    script.src = "https://widget.qweather.net/simple/static/js/he-simple-common.js?v=2.0"
    document.getElementsByTagName("head")[0].appendChild(script)

  },
  created() {
    initBg().then(res => {
      if (res.code === 0){
        this.bg = `background: url(${res.data}) no-repeat center`
      }
    })
  },
  beforeDestroy() {
    if (this.timer){
      clearInterval(this.timer)
    }
  }

}
</script>

<style scoped>

.main{
  margin: 0;
  padding: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}
.main:before{
  content: "";
  /*background: url(../../public/wallhaven.png) no-repeat center;*/
  background-size: auto;
  background-position: center;
  object-fit: cover;
  width: 100%;
  height: 100%;
  position: absolute;
  /*left: -50px;*/
  /*top: -50px;*/
  transition: all 0.2s ease-in-out;
}

.container{
  height: 60px;
  background: rgba(255,255,255,0.7);
  display: flex;
  padding: auto 10px;
  justify-content: space-around;
  align-items: center;
  border-radius: 30px;
  backdrop-filter: blur(4px);
  box-shadow: 0 0 5px 1px gray;
  z-index: 1;
}

.timeBox{
  position: absolute;
  background-color: transparent;
  height: 40px;
  top: 35%;
  color: white;
  text-align: center;
  line-height: 40px;
  font-size: 40px;
}

.search-box{
  width: 200px;
  transition: all 0.3s ease-in-out;
}
.container:hover .search-box{
  width: 440px;
}
.item{
  margin: auto 20px;
  font-size: 20px;
  opacity: 0;
  transition-delay: 0.3s;
  transition: all 0.3s ease;
}
.container:hover .item{
  opacity: 1;
}
.search-btn{
  width: 100%;
  border: none;
  text-align: center;
  outline: none;
  background: inherit;
  font-size: 20px;
  transition: all 0.5s ease-in-out;
}
.search-btn::placeholder{
  color: rgba(255,255,255,0.7);
  text-shadow: 0 0 10px #f3f3f3;
}
.container:hover .search-btn::placeholder{
  color: rgba(119,119,119,0.9);
}

</style>