<template>
  <div style="margin: 80px auto 30px;">
  <el-card class="box-card" :body-style="{ padding: '0px' }">
    <el-page-header @back="goBack" content="详情页面" style="padding: 15px"></el-page-header>
    <div class="main">
      <div class="head">
        <el-col :span="2">
          <el-avatar style="vertical-align: top" :size="50" :src="attachImageUrl(source.user.avatar)"></el-avatar>
        </el-col>
        <el-col :span="19">
          <span style="color: #000000d9;font-size: 16px;">{{source.user.username}}</span><br>
          <span style="color: #8c939d;font-size: small;">{{source.createTime}}</span>
        </el-col>
      </div>
      <div class="content">
        <h1 style="color: #000000d9;font-weight: 600;font-size: 30px;margin-bottom: 15px">{{source.title}}</h1>
        <div style="color: #00000073;margin-bottom: 15px">{{source.description}}</div>
        <div v-html="urlToLink(source.content)"></div>
      </div>
    </div>
    <div v-if="source.fileName">
      <el-alert
          :closable="false"
          type="warning">
        <el-link style="font-size: 10px" icon="el-icon-download" :href="source.fileUrl" type="warning" :underline="false">{{source.fileName}}</el-link>
      </el-alert>
    </div>
    <div class="foot">
      <el-button class="san" @click="thumb(source.id)">
        <i v-if="!source.hasThumb" class="fa fa-thumbs-o-up"></i>
        <i v-else class="fa fa-thumbs-up"></i> {{source.thumbNum}}
      </el-button>
      <el-button class="san san1" @click="favour(source.id)">
        <i v-if="!source.hasFavour" class="fa fa-star-o"></i>
        <i v-else class="fa fa-star"></i> {{source.favourNum}}
      </el-button>
      <el-button class="san" @click="share()">
        <i class="el-icon-share"></i>
      </el-button>
    </div>
  </el-card>

   <el-card style="margin: 20px 0px;" class="box-card">
     <div class="me-view-comment-title">
       <span>{{commentCount}} 条评论</span>
     </div>

     <div class="me-view-comment">
       <div class="me-view-comment-write">
         <el-row :gutter="20">
           <el-col :span="2">
             <a class="">
               <el-avatar :src="attachImageUrl(avatar)"></el-avatar>
             </a>
           </el-col>
           <el-col :span="22">
<!--             <el-input-->
<!--                 type="textarea"-->
<!--                 :autosize="{ minRows: 2}"-->
<!--                 placeholder="你的评论..."-->
<!--                 class="me-view-comment-text"-->
<!--                 v-model="comment.content"-->
<!--                 resize="none">-->
<!--             </el-input>-->
             <VueEmoji ref="emoji" width="100%" height="100" :value="comment.content" @input="onInput" />
           </el-col>
         </el-row>

         <el-row :gutter="20">
           <el-col :span="2" :offset="22">
             <el-button type="text" @click="pushComment()">评论</el-button>
           </el-col>
         </el-row>
       </div>

       <commment-item
           v-for="(c,index) in comments"
           :comment="c"
           :articleId="source.id"
           :index="index"
           :rootCommentCounts="comments.length"
           :key="c.id">
       </commment-item>

       <el-empty :image-size="50" v-if="comments.length === 0"></el-empty>
     </div>
   </el-card>

  </div>
</template>

<script>
import VueEmoji from 'emoji-vue2'
import {mixin} from "@/mixins";
import {favour, getCommentsByArticle, getSourceById, pushComment, thumb} from "@/api";
import CommmentItem from "@/components/CommentItem";

export default {

  data () {
    return {
      id : '',
      source : {
        id:'',
        createTime:'',
        title:'',
        description:'',
        content:'',
        hasThumb:'',
        thumbNum:'',
        hasFavour:'',
        favourNum:'',
        user:{
          avatar:'',
          username:''
        },
      },
      comments: [],
      comment: {
        content: ''
      }
    }
  },
  mixins: [mixin],
  components: {
    CommmentItem,
    VueEmoji
  },
  mounted () {
    window.scrollTo({top:0,left:0})
    this.id = this.$route.params.id
    this.init()
  },
  computed: {
    avatar() {
      let avatar = this.$store.getters.avatar
      if (avatar !== null) {
        return avatar
      }
      return '/avatarImages/default_user.jpg'
    },
    commentCount(){
      let sum = this.comments.length
      for(let i = 0; i < this.comments.length; i++){
        if (this.comments[i].childrens){
          sum += this.comments[i].childrens.length
        }
      }
      return sum
    }
  },
  methods : {
    onInput (event) {
      this.comment.content = event.data
    },
    init (){
      getSourceById(this.id)
          .then((res) => {
            this.source = res.data;
            this.getCommentsByArticle()
          })
          .catch((err) => {
            console.log(err);
          });
    },
    //点赞
    thumb(targetId){
      let params = new URLSearchParams();
      params.append("targetId", targetId);
      thumb(params)
          .then(res => {
            if (res.data == 1){
              this.$message.success('点赞成功')
            }else if(res.data == -1){
              this.$message.success('取消点赞成功')
            }else{
              this.$message.error(res.msg)
            }
            this.init();
          }).catch(err => this.$message.error(err.data.msg))
    },
    //收藏
    favour(targetId){
      let params = new URLSearchParams();
      params.append("targetId", targetId);
      favour(params)
          .then(res => {
            if (res.data == 1){
              this.$message.success('收藏成功')
            }else if(res.data == -1){
              this.$message.success('取消收藏成功')
            }else{
              this.$message.error(res.msg)
            }
            this.init();
          }).catch(err => this.$message.error(err.data.msg))
    },
    //发布评论
    pushComment() {
      if (!this.comment.content.trim()) {
        this.$message({type: 'error', message: '评论不能为空', showClose: true})
        return;
      }
      if (!this.$store.getters.loginIn){
        this.$message({type: 'error', message: '请先登录', showClose: true})
        return;
      }
      let params = new URLSearchParams();
      params.append("content",this.comment.content)
      params.append("articleId",this.source.id)
      params.append("level","0")
      pushComment(params).then(res => {
        if (res.code===0){
          this.$message({type: 'success', message: '评论成功', showClose: true})
          this.comments.unshift(res.data)
          this.$refs.emoji.clear()
          this.comment.content = ''
        }else{
          this.$message({type: 'error', message: `评论失败${res.msg}`, showClose: true})
        }
      }).catch(err => {
        if(err.status === 401){
          this.$message({type: 'error', message: `请重新登录`, showClose: true})
          this.$store.commit('setLoginIn',false)
        }else{
          this.$message({type: 'error', message: `评论失败`, showClose: true})
        }
      })
    },
    //得到资源评论
    getCommentsByArticle() {
      let params = new URLSearchParams();
      params.append('articleId',this.source.id)
      getCommentsByArticle(params).then(res => {
        this.comments = res.data
      }).catch(err => {
        if (err !== 'error') {
          this.$message({type: 'error', message: '评论加载失败', showClose: true})
        }
      })
    },
    share(){
      if (navigator.clipboard && window.isSecureContext){
        navigator.clipboard.writeText(`http://localhost:8080${this.$route.fullPath}`).then(()=>{
          this.$message({type: 'success', message: '复制成功', showClose: true})
        }).catch(()=>{
          this.$message({type: 'error', message: '复制失败', showClose: true})
        })
      }else {
        alert('http://localhost:8080'+this.$route.fullPath+'  ~快复制分享给朋友吧!!')
      }
    },
    goBack() {
      this.$router.go(-1)
    }
  }

}
</script>
<style scoped>
.box-card{
  width: 800px;
  min-height: 300px;
}
.main{
  min-height: 260px;
  padding: 20px;
}
.head{
  height: 80px;
}
.content{
  padding-top: 20px;
  border-top: rgba(0,0,0,.06) solid 1px;
}
.foot{
  height: 38px;
  width: 800px;
  border-top: rgba(0,0,0,.06) solid 1px;
}
.san{
  width: 33.33%;
  margin-left: 0px;
  border: none;
  background-color: white;
}
.san1{
  border-left: rgba(0,0,0,.06) solid 1px;
  border-right: rgba(0,0,0,.06) solid 1px;
}
.me-view-comment {
  margin-top: 20px;
}

</style>