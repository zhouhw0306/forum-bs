<template>
  <el-card>
    <div style="text-align: center;">
      <img class="me-picture" :src="attachImageUrl(avatar)"/>
    </div>

    <h1 class="me-author-name">{{username}}</h1>
    <el-button @click="toWrite" type="primary" style="width: 100%;padding: 5px;margin-top: 5px"><i class="el-icon-edit"></i>我要发帖</el-button>
    <div class="me-author-description">
        <div style="display: inline-block">关注 <br>{{ personal.idol }}</div>
        <el-divider direction="vertical"></el-divider>
        <div style="display: inline-block">粉丝 <br>{{ personal.followers }}</div>
        <el-divider direction="vertical"></el-divider>
        <div style="display: inline-block">帖子 <br>{{ personal.writeNum }}</div>
        <el-divider direction="vertical"></el-divider>
        <div style="display: inline-block">积分 <br>{{ personal.score }}</div>
    </div>
  </el-card>

</template>

<script>
  import {mapGetters} from "vuex";
  import {mixin} from "@/mixins";
  import {getPersonal} from "@/api";

  export default {
    name: 'CardMe',
    mixins: [mixin],
    data() {
      return {
        personal:{}
      }
    },
    computed: {
      ...mapGetters([
        'userId',
        'avatar',
        'username',
        'loginIn'
      ]),
    },
    mounted() {
      this.init()
    },
    methods: {
      init(){
        getPersonal().then(res => {this.personal = res.data})
      },
      toWrite(){
        this.$router.push({path: '/write'})
      }
    }
  }
</script>

<style scoped>
  .me-author-name {
    text-align: center;
    font-size: 20px;
    border-bottom: 1px solid #5FB878;
  }

  .me-author-description {
    padding: 8px 0;
    font-size: 16px;
    text-align: center;
  }

  .me-picture {
    width: 36px;
    height: 36px;
    border: 1px solid #e266b0;
    border-radius: 50%;
    vertical-align: middle;
    background-color: #409EFF;
    margin: 0px auto
  }
</style>
