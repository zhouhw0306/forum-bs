<template>
  <div style="padding: 20px">
    <el-card style="margin: 0px 0px 20px 0px;" shadow="never">
      <i class="el-icon-s-operation" style="margin-right: 10px"></i>分类
      <el-radio-group v-model="type" size="medium" style="margin: 0 20px">
        <el-radio-button label="工具"></el-radio-button>
        <el-radio-button label="网站"></el-radio-button>
        <el-radio-button label="项目"></el-radio-button>
        <el-radio-button label="教程"></el-radio-button>
      </el-radio-group>
      <el-button v-if="this.$store.getters.loginIn" type="success" icon="el-icon-upload" size="small" @click="dialogFormVisible = true">分享</el-button>
      <div style="margin-top: 20px">
        <i class="el-icon-finished" style="margin-right: 10px"></i>排序
        <el-radio-group v-model="sort" size="medium" style="margin-left: 20px">
          <el-radio-button label="create_time">最新</el-radio-button>
          <el-radio-button label="thumb_num">点赞</el-radio-button>
        </el-radio-group>
      </div>
    </el-card>
    <el-row :gutter="20">
      <el-col :xs="13" :sm="13" :md="12" :lg="8" :xl="8" v-for="(item,index) in tableData" :key="index" style="margin-bottom: 20px;">
        <el-card shadow="hover" style="height: 270px;">
          <div>
            <el-avatar style="vertical-align: top" :size="50" :src="attachImageUrl(item.user.avatar)"></el-avatar>
            <span class="hover-title" style="margin-left: 20px;font-size: 20px;">{{item.user.username}}</span>
          </div>

          <div style="margin-top: 20px;height: 140px;overflow: hidden">
            <div style="max-width: calc(100% - 100px);display: inline-block">
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
            <el-col :span="8">
              <el-button class="icon-foot" @click="thumb(item)">
                <i v-if="!item.hasThumb" class="fa fa-thumbs-o-up"></i>
                <i v-else class="fa fa-thumbs-up"></i> {{item.thumbNum}}
              </el-button>
            </el-col>
            <el-col :span="8">
              <el-button class="icon-foot" @click="favour(item)">
                <i v-if="!item.hasFavour" class="fa fa-star-o"></i>
                <i v-else class="fa fa-star"></i> {{item.favourNum}}
              </el-button>
            </el-col>
            <el-col :span="8">
              <el-button class="icon-foot">
                <i class="fa fa-comment-o"></i>
              </el-button>
            </el-col>
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

    <el-dialog title="分享" :visible.sync="dialogFormVisible" width="40%">
      <el-form :model="form"  style="padding-right: 70px">
        <el-form-item label="标题" label-width="100px">
          <el-input v-model="form.title" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="信息" label-width="100px">
          <el-input v-model="form.description" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="内容" label-width="100px">
          <el-input v-model="form.content" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="封面" label-width="100px">
          <el-input v-model="form.cover" ></el-input>
        </el-form-item>
        <el-form-item label="类型" label-width="100px">
          <el-select v-model="form.category" placeholder="请选择类型">
            <el-option label="工具" value="1"></el-option>
            <el-option label="网站" value="2"></el-option>
            <el-option label="项目" value="3"></el-option>
            <el-option label="教程" value="4"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="share()">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>


<script>
import {mixin} from "@/mixins";
import {sourceShare, favour, getTableData, thumb} from "@/api";

export default {
  data() {
    return {
      tableData: [],
      pageNo: 1,
      pageSize: 9,
      total: 0,
      type: '工具',
      sort: 'create_time',
      dialogFormVisible: false,
      form: {
        title:'',
        description:'',
        content:'',
        cover:'',
        category:''
      }
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
    },
    share(){
      if (this.form.title ==='' || this.form.category ===''){
        return this.$message.error('表单不能为空')
      }
      sourceShare(this.form).then(res => {
         if (res.code === 0){
           this.$message.success('分享成功正在审核')
           this.dialogFormVisible = false
           this.form = {
             title:'',
             description:'',
             content:'',
             cover:'',
             category:''
           }
         } else {
           this.$message.error(res.msg)
         }
      }).catch(err => this.$message.error(err))
    }
  }
}
</script>

<style scoped>
.icon-foot{
  width: 100%;
  cursor: pointer;
  background-color: white;
  border:none
}
.icon-foot:hover{
  background-color: whitesmoke;
}
.hover-title:hover{
  color: #1890ff;
  cursor: pointer;
}
</style>