<template>
  <div>
      <el-empty v-if="tableData.length===0" :image-size="100" description="暂无搜索内容"></el-empty>
      <el-col v-else :span="24" v-for="(item,index) in tableData" :key="index" style="margin-bottom: 10px;">
        <el-card  shadow="hover" style="height: 120px;">
          <div @click="view(item.id)">
            <a class="me-article-title">{{item.title}}</a>
            <span class="me-pull-right me-article-count">
              <i class="el-icon-chat-dot-round"></i>&nbsp;{{item.commentCount}}
            </span>
            <span class="me-pull-right me-article-count">
              <i class="el-icon-view"></i>&nbsp; {{item.viewCount}}
            </span>
            <span class="me-pull-right me-article-count">
              <i class="el-icon-time"></i>&nbsp;{{item.createTime}}
            </span>
          </div>
          <div class="me-artile-description">
            {{filterHtml(item.contentHtml)}}
          </div>
        </el-card>
      </el-col>
  </div>
</template>

<script>
export default {
  props:{
    tableData : {
      type: Array,
      default: []
    },
  },
  methods:{
    view(id){
      this.$router.push({path:`/view/${id}`})
    },
    filterHtml(strHTML){
      let re = new RegExp('<[^<>]+>','g');
      strHTML = strHTML.replace(re ,"");
      strHTML = strHTML.replace(/<[^<>]+>/g,"");
      return strHTML
    }
  }
}


</script>
<style>
.me-artile-description {
  font-size: 13px;
  line-height: 24px;
  margin-top: 10px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.me-article-title {
  color: #7ab3ea;
  font-weight: 600;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
}
</style>