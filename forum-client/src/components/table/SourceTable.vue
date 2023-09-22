<template>
  <div>
    <el-empty v-if="tableData.length===0" :image-size="100" description="暂无搜索内容"></el-empty>
    <el-row v-else :gutter="20">
      <el-col :span="8" v-for="(item,index) in tableData" :key="index" style="margin-bottom: 20px;">
        <el-card shadow="hover" style="height: 200px;">

            <div style="max-width: calc(100% - 100px);height:100px;display: inline-block">
              <div>
                <a class="hover-title" @click="view(item.id)"><b v-html="item.title"/></a>
              </div>
            </div>
            <div style="float: right;display: inline-block">
              <el-image
                  style="width: 100px; height: 100px"
                  :src="item.cover"
                  :preview-src-list="arr(item.cover)"
                  fit="cover">
              </el-image>
            </div>


          <div style="margin-top: 10px;overflow: hidden;text-indent: 25px;">
            <span>{{item.description}}</span>
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
  },
  methods: {
    arr(src) {
      let arr = []
      arr.push(src)
      return arr
    },
    view(id){
      this.$router.push({path: `/details/${id}`})
    }
  }
}


</script>