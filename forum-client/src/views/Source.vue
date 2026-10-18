<template>
  <div class="page-container resources-page">
    <div class="page-heading"><div><div class="page-eyebrow"><i class="el-icon-collection"></i> CodeBase / 资源</div><h1>资源广场</h1><p>发现实用工具、开源项目和优质教程，让开发更高效。</p></div><button class="button-primary" @click="$store.getters.loginIn ? dialogFormVisible = true : $router.push({ path: '/login', query: { redirect: $route.fullPath } })"><i class="el-icon-plus"></i> 分享资源</button></div>
    <section class="resource-filters" aria-label="筛选资源">
      <div class="filter-group"><span class="filter-label"><i class="el-icon-folder-opened"></i> 分类</span><el-radio-group v-model="activeNav.type" size="small" @change="applyFilters"><el-radio-button label="">全部</el-radio-button><el-radio-button label="工具" /><el-radio-button label="网站" /><el-radio-button label="项目" /><el-radio-button label="教程" /></el-radio-group></div>
      <div class="filter-group"><span class="filter-label"><i class="el-icon-sort"></i> 排序</span><el-radio-group v-model="activeNav.sort" size="small" @change="applyFilters"><el-radio-button label="create_time">最新发布</el-radio-button><el-radio-button label="thumb_num">最多点赞</el-radio-button></el-radio-group></div>
    </section>
    <div class="resource-results-meta"><span>{{ activeNav.type || '全部' }}资源</span><span>{{ total }} 个资源</span></div>
    <div v-if="loadError" class="resource-empty state-panel" role="status"><i class="el-icon-warning-outline"></i><h3>资源暂时无法加载</h3><p>请稍后重试。</p><el-button @click="loadTable" size="small">重新加载</el-button></div>
    <el-row v-else-if="loading" :gutter="20"><el-col v-for="n in 6" :key="n" :xs="24" :sm="12" :lg="8"><el-card class="resource-skeleton" shadow="never"><el-skeleton :rows="4" animated /></el-card></el-col></el-row>
    <el-row v-else-if="tableData.length" :gutter="20" class="resource-grid">
      <el-col v-for="item in tableData" :key="item.id" :xs="24" :sm="12" :lg="8">
        <article class="resource-card">
          <div class="resource-card-top"><el-image v-if="item.cover" class="resource-cover" :src="item.cover" fit="cover"><span slot="error" class="resource-cover-fallback"><i class="el-icon-folder-opened"></i></span></el-image><span v-else class="resource-cover-fallback"><i class="el-icon-folder-opened"></i></span><span class="resource-type">{{ item.categoryName || activeNav.type || '社区资源' }}</span></div>
          <router-link :to="'/details/' + item.id" class="resource-title">{{ item.title }}</router-link><p class="resource-description">{{ item.description || '打开资源，了解更多内容。' }}</p>
          <div class="resource-author"><img :src="attachImageUrl(item.user?.avatar)" :alt="item.user?.username || '作者头像'" @error="e => e.target.src = avatarFallback(item.user?.username)" /><span>{{ item.user?.username || '社区成员' }}</span><span class="resource-date">{{ (item.createTime || '').slice(0, 10) }}</span></div>
          <div class="resource-footer"><button :class="{ selected: item.hasThumb }" @click="thumb(item)" :aria-label="'点赞 ' + item.title"><i :class="item.hasThumb ? 'fa fa-thumbs-up' : 'fa fa-thumbs-o-up'"></i> {{ item.thumbNum || 0 }}</button><button :class="{ selected: item.hasFavour }" @click="favour(item)" :aria-label="'收藏 ' + item.title"><i :class="item.hasFavour ? 'el-icon-star-on' : 'el-icon-star-off'"></i> {{ item.favourNum || 0 }}</button><router-link :to="'/details/' + item.id">查看资源 <i class="el-icon-arrow-right"></i></router-link></div>
        </article>
      </el-col>
    </el-row>
    <div v-else class="resource-empty state-panel"><i class="el-icon-folder-opened"></i><h3>还没有相关资源</h3><p>试试其他分类，或分享你发现的好工具。</p></div>
    <div v-if="total > pageSize && !loadError" class="resource-pagination"><el-pagination @current-change="handleCurrentChange" :current-page="pageNo" :page-size="pageSize" :total="total" :pager-count="5" background layout="prev, pager, next" /></div>
    <el-dialog :visible.sync="dialogFormVisible" width="70%">
      <template v-slot:title>
        <i class="el-icon-share"/><span class="el-dialog__title"> 分享</span>
      </template>
      <el-row :gutter="24">
        <el-form ref="sourceForm" :status-icon="true" :model="form" :rules="sourceForm" size="medium" label-width="60px" class="resource-share-form">
          <el-col :xs="24" :sm="12">
            <el-form-item label="封面" prop="cover">
              <el-input v-model="form.cover" placeholder="请输入封面" clearable :style="{width: '100%'}"></el-input>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="类型" prop="category">
              <el-select v-model="form.category" placeholder="请选择类型">
                <el-option label="工具" value="1"></el-option>
                <el-option label="网站" value="2"></el-option>
                <el-option label="项目" value="3"></el-option>
                <el-option label="教程" value="4"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入标题" clearable :style="{width: '100%'}"></el-input>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="外链" prop="content">
              <el-input v-model="form.content" placeholder="请输入外链" clearable :style="{width: '100%'}"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="信息" prop="description">
              <el-input v-model="form.description" placeholder="请输入描述信息" clearable :style="{width: '100%'}"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="附件" prop="fileSrc">
              <el-upload :limit="1"
                         :headers="{'Token' : token}"
                         :file-list="fileList"
                         :action="fileAction"
                         :on-remove="handleRemove"
                         :before-remove="beforeRemove"
                         :on-success="handleSuccess"
                         :on-exceed="handleExceed">
                <el-button size="mini" type="primary" icon="el-icon-upload">点击上传</el-button>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-form>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="share()">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import {mixin} from "@/mixins";
import { sourceForm } from '@/assets/data/form'
import {sourceShare, favour, getTableData, thumb} from "@/api";
import {mapGetters} from "vuex";

export default {
  data() {
    return {
      tableData: [],
      loading: false,
      loadError: false,
      requestVersion: 0,
      total: 0,
      pageNo: Number(this.$route.query.pageNo) || 1,
      pageSize: 9,
      dialogFormVisible: false,
      form: {
        title:'',
        description:'',
        content:'',
        cover:'',
        category:''
      },
      sourceForm: sourceForm,
      fileAction: '',
      fileList: [],
      activeNav:{
        type : this.$route.query.type || '',
        sort : this.$route.query.sort || 'create_time',
      }
    };
  },
  mixins: [mixin],
  mounted() {
    this.fileAction = `${this.HOST}/source/upload`
    this.loadTable();
  },
  computed: {
    ...mapGetters(['token','HOST']),
  },
  watch: {
    '$route.query'() {
      this.activeNav = { type: this.$route.query.type || '', sort: this.$route.query.sort || 'create_time' }
      this.pageNo = Number(this.$route.query.pageNo) || 1
      this.loadTable()
    }
  },
  beforeDestroy() { this.requestVersion += 1 },
  methods:{
    applyFilters() {
      this.pageNo = 1
      this.$router.push({ query: { type: this.activeNav.type, sort: this.activeNav.sort, pageNo: 1 } })
    },
    arr(src){
      let arr = []
      arr.push(src)
      return arr
    },
    handleSizeChange(val) {
      this.pageSize = val;
      this.loadTable();
    },
    // 翻页回调
    handleCurrentChange(val) {
      this.pageNo = val;
      let {type,sort} = this.activeNav
      this.$router.push({query: {type: type,sort: sort,pageNo:this.pageNo}})
    },
    //初始化
    async loadTable() {
      const version = ++this.requestVersion
      this.loading = true; this.loadError = false
      const params = new URLSearchParams()
      if (this.activeNav.type) params.append('type', this.activeNav.type)
      params.append('sort', this.activeNav.sort)
      params.append('pageNo', this.pageNo)
      params.append('pageSize', this.pageSize)
      try {
        const res = await getTableData(params)
        if (version !== this.requestVersion) return
        if (!Array.isArray(res.records)) throw new Error('Invalid resources response')
        this.tableData = res.records; this.total = res.total || 0
      } catch (error) { if (version === this.requestVersion) this.loadError = true }
      finally { if (version === this.requestVersion) this.loading = false }
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
    //分享
    share(){
      this.$refs.sourceForm.validate(validate=>{
        if (!validate) return;
        if(this.fileList.length > 0){
          this.form.fileName = this.fileList[0].fileName
          this.form.fileUrl = this.fileList[0].fileUrl
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
            this.fileList = []
          } else {
            this.$message.error(res.msg)
          }
        }).catch(err => this.$message.error(err))
      })
    },
    handleRemove(file, fileList) {
      this.fileList = []
    },
    handleExceed(files, fileList) {
      this.$message.warning(`超出数量限制`);
    },
    beforeRemove(file, fileList) {
      return this.$confirm(`确定移除 ${ file.name }？`)
    },
    handleSuccess(res, file, fileList){
      this.fileList[0]=res.data
    }
  }
}
</script>
<style scoped>
.resources-page { max-width: 1240px; }
.resource-filters { display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 16px; padding: 16px 20px; background: var(--surface); border: 1px solid var(--border); border-radius: 6px; }
.filter-group { display: flex; align-items: center; gap: 14px; flex-wrap: wrap; } .filter-label { color: var(--text-muted); font-size: 12px; white-space: nowrap; } .filter-label i { margin-right: 5px; font-size: 15px; }
.resource-results-meta { display: flex; justify-content: space-between; margin: 24px 0 16px; font-size: 12px; color: var(--text-muted); } .resource-results-meta span:first-child { color: var(--text); font-weight: 600; }
.resource-grid { display: flex; flex-wrap: wrap; } .resource-grid > .el-col { margin-bottom: 20px; display: flex; }
.resource-card { display: flex; flex-direction: column; width: 100%; min-width: 0; padding: 20px; border: 1px solid var(--border); border-radius: 6px; background: var(--surface); }
.resource-card:hover { border-color: #afb8c1; }
.resource-card-top { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.resource-cover, .resource-cover-fallback { display: flex; align-items: center; justify-content: center; width: 40px; height: 40px; border-radius: 8px; background: var(--surface-muted); color: var(--text-muted); font-size: 22px; }
.resource-type { color: var(--text-muted); border: 1px solid var(--border); border-radius: 12px; padding: 1px 8px; font-size: 11px; }
.resource-title { color: var(--accent); font-size: 17px; font-weight: 600; line-height: 1.5; overflow-wrap: anywhere; } .resource-title:hover { text-decoration: underline; }
.resource-description { margin: 8px 0 20px; color: var(--text-muted); font-size: 13px; line-height: 1.7; min-height: 44px; overflow: hidden; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow-wrap: anywhere; }
.resource-author { display: flex; align-items: center; gap: 7px; color: var(--text-muted); font-size: 11px; margin-top: auto; } .resource-author img { height: 20px; width: 20px; object-fit: cover; border-radius: 50%; border: 1px solid var(--border-subtle); }
.resource-date { margin-left: auto; }
.resource-footer { display: flex; align-items: center; gap: 16px; margin-top: 16px; padding-top: 14px; border-top: 1px solid var(--border-subtle); }
.resource-footer button { border: 0; padding: 0; background: none; color: var(--text-muted); cursor: pointer; font-size: 12px; display: flex; align-items: center; gap: 5px; } .resource-footer button i { font-size: 16px; } .resource-footer button:hover, .resource-footer button.selected { color: var(--accent); }
.resource-footer a { margin-left: auto; color: var(--text-muted); font-size: 12px; } .resource-footer a:hover { color: var(--accent); }
.resource-empty { border: 1px solid var(--border); border-radius: 6px; background: var(--surface); }
.resource-pagination { display: flex; justify-content: center; padding-top: 8px; }
.resource-skeleton { margin-bottom: 20px; min-height: 240px; }
@media (max-width: 600px) { .resource-filters { padding: 16px; } .filter-group { gap: 10px; width: 100%; } .filter-label { width: 38px; } .filter-group >>> .el-radio-button__inner { padding: 8px 10px; } .resource-card { padding: 18px; } }
</style>
