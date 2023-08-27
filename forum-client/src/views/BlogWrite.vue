<template>
  <div id="write" :data-title="title">
    <el-container>
      <the-header :simple=true>
        <el-col :span="4" :offset="2">
          <div class="me-write-info">发帖</div>
        </el-col>
        <el-col :span="4" :offset="6">
          <div class="me-write-btn">
            <el-button round @click="publishShow">发布</el-button>
            <el-button round @click="cancel">取消</el-button>
          </div>
        </el-col>
      </the-header>

      <el-container class="me-area me-write-box">
        <el-main class="me-write-main">
          <div class="me-write-title">
            <el-input resize="none"
                      type="textarea"
                      autosize
                      v-model="articleForm.title"
                      placeholder="请输入标题"
                      class="me-write-input">
            </el-input>

          </div>
          <markdown-editor :editor="articleForm.editor" class="me-write-editor"></markdown-editor>
        </el-main>
      </el-container>

      <el-dialog title=" 分类 / 标签"
                 :visible.sync="publishVisible"
                 :close-on-click-modal=false
                 custom-class="me-dialog">

        <el-form :model="articleForm" ref="articleForm" :rules="rules">
          <el-form-item label="文章分类" prop="category">
            <el-select v-model="articleForm.category" value-key="id" placeholder="请选择文章分类">
              <el-option v-for="c in categorys" :key="c.id" :label="c.categoryName" :value="c.id"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="文章标签" prop="tags">
            <el-checkbox-group v-model="articleForm.tags">
              <el-checkbox v-for="t in tags" :key="t.id" :label="t.id" name="tags">{{t.tagName}}</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="publishVisible = false">取 消</el-button>
          <el-button type="primary" @click="publish('articleForm')">发布</el-button>
        </div>
      </el-dialog>
    </el-container>
  </div>
</template>

<script>
import TheHeader from '@/components/TheHeader'
import MarkdownEditor from '@/components/article/MarkdownEditor'
import {publishArticle, getArticleById} from '@/api/index'
import {getAllCategorys} from '@/api'
import {getAllTags} from '@/api'

export default {
  name: 'BlogWrite',
  mounted() {

    if(this.$route.params.id){
      this.getArticleById(this.$route.params.id)
    }

    this.getCategorysAndTags()
    let but = document.getElementsByClassName("op-icon fa fa-mavon-eye-slash selected")
    but[0].click();
  },
  data() {
    return {
      publishVisible: false,
      categorys: [],
      tags: [],
      articleForm: {
        id: '',
        title: '',
        category: '',
        tags: [],
        editor: {
          value: '',
          ref: '',//保存mavonEditor实例  实际不该这样
          default_open: 'edit',
          toolbars: {
            bold: true, // 粗体
            italic: true, // 斜体
            header: true, // 标题
            underline: true, // 下划线
            strikethrough: true, // 中划线
            mark: true, // 标记
            superscript: true, // 上角标
            subscript: true, // 下角标
            quote: true, // 引用
            ol: true, // 有序列表
            ul: true, // 无序列表
            imagelink: true, // 图片链接
            code: true, // code
            fullscreen: true, // 全屏编辑
            readmodel: true, // 沉浸式阅读
            help: true, // 帮助
            undo: true, // 上一步
            redo: true, // 下一步
            trash: true, // 清空
            navigation: true, // 导航目录
            //subfield: true, // 单双栏模式
            preview: true, // 预览
          }
        }
      },
      rules: {
        category: [
          {required: true, message: '请选择文章分类', trigger: 'change'}
        ],
        tags: [
          {type: 'array', required: true, message: '请选择标签', trigger: 'change'}
        ]
      }
    }
  },
  computed: {
    title (){
      return '写帖子'
    }
  },
  methods: {
    getArticleById(id) {
      let that = this
      getArticleById(id).then(data => {
        that.articleForm.editor.value = data.data.data.content
        that.articleForm.id = data.data.data.id
        that.articleForm.title = data.data.data.title
      }).catch(error => {
        if (error !== 'error') {
          that.$message({type: 'error', message: '文章加载失败', showClose: true})
        }
      })
    },
    publishShow() {
      if (!this.articleForm.title) {
        this.$message({message: '标题不能为空哦', type: 'warning', showClose: true})
        return
      }

      if (this.articleForm.title.length > 30) {
        this.$message({message: '标题不能大于30个字符', type: 'warning', showClose: true})
        return
      }

      if (!this.articleForm.editor.ref.d_render) {
        this.$message({message: '内容不能为空哦', type: 'warning', showClose: true})
        return
      }

      this.publishVisible = true;
    },
    publish(articleForm) {
      if (!this.$store.getters.loginIn){
        this.$message({type: 'error', message: '请先登录', showClose: true})
        return;
      }
      let that = this

      this.$refs[articleForm].validate((valid) => {
        if (valid) {

          let tags = this.articleForm.tags.map(function (item) {
            return {id: item};
          });

          let article = {
            id: this.articleForm.id,
            title: this.articleForm.title,
            categoryId : this.articleForm.category, //分类
            tags: tags, //标签
            content: this.articleForm.editor.value,
            contentHtml: this.articleForm.editor.ref.d_render
          }

          this.publishVisible = false;

          let loading = this.$loading({
            lock: true,
            text: '发布中，请稍后...'
          })

          publishArticle(article).then((res) => {
            loading.close();
            if (res.data.code === 0){
              that.$message({message: '发布成功啦', type: 'success', showClose: true})
              that.$router.replace({path: `/view/${res.data.data}`})
            }else {
              that.$message({message: '发布失败', type: 'error', showClose: true})
            }
          }).catch((error) => {
            loading.close();
            if (error !== 'error') {
              that.$message({message: '发布失败', type: 'error', showClose: true});
            }
          })

        } else {
          return false;
        }
      });
    },
    cancel() {
      this.$confirm('文章将不会保存, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // this.$router.push('/')
        this.$router.go(-1)
      })
    },
    getCategorysAndTags() {
      let that = this
      getAllCategorys().then(res => {
        that.categorys = res.data
      }).catch(error => {
        if (error !== 'error') {
          that.$message({type: 'error', message: '文章分类加载失败', showClose: true})
        }
      })

      getAllTags().then(res => {
        that.tags = res.data
      }).catch(error => {
        if (error !== 'error') {
          that.$message({type: 'error', message: '标签加载失败', showClose: true})
        }
      })

    },
    editorToolBarToFixed() {

      let toolbar = document.querySelector('.v-note-op');
      let curHeight = document.documentElement.scrollTop || document.body.scrollTop;
      if (curHeight >= 160) {
        document.getElementById("placeholder").style.display = "block"; //bad  用计算属性较好
        toolbar.classList.add("me-write-toolbar-fixed");
      } else {
        document.getElementById("placeholder").style.display = "none";
        toolbar.classList.remove("me-write-toolbar-fixed");
      }
    }
  },
  components: {
    'the-header': TheHeader,
    'markdown-editor': MarkdownEditor
  },
  //组件内的守卫 调整body的背景色
  beforeRouteEnter(to, from, next) {
    window.document.body.style.backgroundColor = '#fff';
    next();
  },
  beforeRouteLeave(to, from, next) {
    window.document.body.style.backgroundColor = '#f5f5f5';
    next();
  }
}
</script>

<style>
  .el-header {
    position: fixed;
    z-index: 1024;
    min-width: 100%;
    box-shadow: 0 2px 3px hsla(0, 0%, 7%, .1), 0 0 0 1px hsla(0, 0%, 7%, .1);
  }

  .me-write-info {
    line-height: 60px;
    font-size: 18px;
    font-weight: 600;
  }

  .me-write-btn {
    margin-top: 10px;
  }

  .me-write-box {
    max-width: 80vw;
    margin: 80px auto 0;
    box-shadow: 0 0 3px 3px #eceaea;
  }

  .me-write-main {
    /*border: #f4f4f5 solid 2px;*/
    padding: 0;
  }

  .me-write-title {
  }

  .me-write-input textarea {
    font-size: 32px;
    font-weight: 600;
    height: 20px;
    border: none;
  }

  .me-write-editor {
    height: 550px !important;
  }


  .me-title img {
    max-height: 2.4rem;
    max-width: 100%;
  }

  .me-write-toolbar-fixed {
    position: fixed;
    width: 700px !important;
    top: 60px;
  }

  .v-note-wrapper{
    box-shadow: none !important;

  }
  .v-note-show{
    padding-left: 15px !important;
  }
  .v-note-op {
    border-bottom: 1px solid #dedfe0 !important;
    background-color: white !important;
  }

  .auto-textarea-input, .auto-textarea-block {
    font-size: 18px !important;
  }
</style>
