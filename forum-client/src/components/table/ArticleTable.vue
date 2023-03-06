<template>
  <div>
      <el-empty v-if="tableData.length===0" :image-size="100" description="暂无搜索内容"></el-empty>
      <el-col v-else :span="24" v-for="(item,index) in tableData" :key="index" style="margin-bottom: 20px;">
        <el-card  shadow="hover" style="height: 120px;">
          <div @click="view(item.id)">
            <a>{{item.title}}</a>
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
    }
  }
}


</script>