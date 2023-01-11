<template>
  <div style="padding: 0px 20px">
    <el-card style="margin: 30px 20px 20px 0px;" shadow="never">
      <i class="el-icon-s-operation" style="margin-right: 10px"></i>分类
      <el-radio-group v-model="type" size="medium" style="margin-left: 20px">
        <el-radio-button label="工具"></el-radio-button>
        <el-radio-button label="网站"></el-radio-button>
        <el-radio-button label="项目"></el-radio-button>
        <el-radio-button label="教程"></el-radio-button>
      </el-radio-group>
      <div style="margin-top: 20px">
        <i class="el-icon-finished" style="margin-right: 10px"></i>排序
        <el-radio-group v-model="sort" size="medium" style="margin-left: 20px">
          <el-radio-button label="create_time">最新</el-radio-button>
          <el-radio-button label="thumb_num">点赞</el-radio-button>
        </el-radio-group>
      </div>
    </el-card>
    <el-row>
      <el-col :span="8" v-for="(item,index) in tableData" :key="index">
        <el-card shadow="hover" style="width:450px;height: 270px;">
          <div>
            <el-avatar style="vertical-align: top" :size="50" :src="attachImageUrl(item.user.avatar)"></el-avatar>
            <span class="hover-title" style="margin-left: 20px;font-size: 20px;">{{item.user.username}}</span>
          </div>

          <div style="margin-top: 20px;height: 140px">
            <div style="width: 250px;display: inline-block">
              <h1 class="hover-title" @click="view(item.id)">{{item.title}}</h1>
              <span>{{item.description}}</span>
            </div>
            <div style="float: right;">
              <el-image
                  style="width: 100px; height: 100px"
                  :src="item.cover"
                  :preview-src-list="arr(item.cover)"
                  fit="cover">
              </el-image>
            </div>
          </div>

          <div style="height: 30px;border-top: antiquewhite solid 1px">
            <el-button class="icon-foot" @click="thumb(item)">
              <i v-if="!item.hasThumb" class="fa fa-thumbs-o-up"></i>
              <i v-else class="fa fa-thumbs-up"></i> {{item.thumbNum}}
            </el-button>
            <el-button class="icon-foot" @click="favour(item)">
              <i v-if="!item.hasFavour" class="fa fa-star-o"></i>
              <i v-else class="fa fa-star"></i> {{item.favourNum}}
            </el-button>
            <el-button class="icon-foot">
              <i class="fa fa-comment-o"></i>
            </el-button>
          </div>

        </el-card>
      </el-col>
    </el-row>
    <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNo"
        :page-size="pageSize"
        :total="total"
        style="margin-left: 800px;margin-bottom: 20px"
        background
        layout="prev, pager, next">
    </el-pagination>
  </div>
</template>


<script>
import {mixin} from "@/mixins";
import {favour, getTableData, thumb} from "@/api";

export default {
  data() {
    return {
      tableData: [],
      pageNo: 1,
      pageSize: 9,
      total: 0,
      type: '工具',
      sort: 'create_time'
    };
  },
  mixins: [mixin],
  created() {
    this.loadTable();
  },
  // 条件改变刷新页面
  watch: {
    sort : {
      handler(newName, oldName) {
        this.pageNo=1
        this.loadTable()
      }
    },
    type : {
      handler(newName, oldName) {
        this.pageNo=1
        this.loadTable()
      }
    }
  },
  methods:{
    arr(src){
      let arr = []
      arr.push(src)
      return arr
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.loadTable();
    },
    handleCurrentChange(val) {
      this.pageNo = val;
      this.loadTable();
    },
    loadTable(){
      let params = new URLSearchParams();
      params.append("type", this.type);
      params.append("sort", this.sort);
      params.append("pageNo", this.pageNo);
      params.append("pageSize", this.pageSize);
      getTableData(params)
          .then((res) => {
            this.tableData = res.records;
            this.total = res.total;
          })
          .catch((err) => {
            console.log(err);
          });
    },
    //点赞
    thumb(item){
      let params = new URLSearchParams();
      params.append("targetId", item.id);
      thumb(params)
          .then(res => {
            if (res.data === 1){
              this.$message.success('点赞成功')
              item.hasThumb = true
              item.thumbNum += 1
            }else if(res.data === -1){
              this.$message.success('取消点赞成功')
              item.hasThumb = false
              item.thumbNum -= 1
            }else{
              this.$message.error(res.msg)
            }
          }).catch(err => this.$message.error(err.data.msg))
    },
    //收藏
    favour(item){
      let params = new URLSearchParams();
      params.append("targetId", item.id);
      favour(params)
          .then(res => {
            if (res.data === 1){
              this.$message.success('收藏成功')
              item.hasFavour = true
              item.favourNum += 1
            }else if(res.data === -1){
              this.$message.success('取消收藏成功')
              item.hasFavour = true
              item.favourNum -= 1
            }else{
              this.$message.error(res.msg)
            }
            this.loadTable();
          }).catch(err => this.$message.error(err.data.msg))
    },
    //详情
    view(id){
      this.$router.push({path: `/details/${id}`})
    }
  }
}
</script>

<style scoped>
.el-col {
  margin-bottom: 20px;
}
.el-row{
  margin-right: 0px!important;
}
.icon-foot{
  width: 60px;
  display: inline-block;
  margin-left: 30px;
  margin-right: 30px;
  text-align:center;
  padding: 5px 0px;
  cursor: pointer;
  background-color: white;
  border:none
}
.icon-foot:hover{
  background-color: whitesmoke;
}
.hover-title:hover{
  color: #1890ff;
}
</style>