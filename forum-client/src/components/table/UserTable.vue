<template>
  <div>
    <el-empty v-if="tableData.length===0" :image-size="100" description="暂无搜索内容"></el-empty>
    <el-row v-else :gutter="20">
      <el-col :span="8" v-for="(item,index) in tableData" :key="index" style="margin-bottom: 20px;">
        <el-card shadow="hover" style="height: 200px;">
          <div>
            <el-avatar :src="attachImageUrl(item.avatar)"></el-avatar>
            <b style="vertical-align: top;margin-left: 15px">{{item.username}}</b>
          </div>
          <div style="margin-top: 50px">
            <span>
            简介： {{item.introduction}}
            </span>
          </div>
        </el-card>
      </el-col>
    </el-row>

  </div>
</template>

<script>
import {mixin} from "@/mixins";

export default {
  mixins: [mixin],
  props:{
    tableData : {
      type: Array,
      default: []
    },
  }
}


</script>