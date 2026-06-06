<template>
  <div class="article-manage">
    <div class="am-toolbar">
      <el-input v-model="keyword" size="small" placeholder="搜索标题..." prefix-icon="el-icon-search" clearable class="am-search" />
      <span class="am-total">共 {{ tableData.length }} 篇</span>
    </div>

    <el-table :data="pagedData" style="width: 100%" size="medium" :row-class-name="rowClass">
      <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
      <el-table-column prop="userId" label="作者ID" width="120" show-overflow-tooltip />
      <el-table-column prop="viewCount" label="阅读" width="70" align="center" />
      <el-table-column prop="commentCount" label="评论" width="70" align="center" />
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="120" align="center">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" icon="el-icon-view" @click="preview(row)">预览</el-button>
          <el-button type="text" size="mini" icon="el-icon-delete" style="color:#f56c6c" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="total > pageSize"
      class="am-pager"
      background
      layout="prev, pager, next"
      :total="total"
      :page-size="pageSize"
      :current-page.sync="currentPage"
    />

    <!-- 预览弹窗 -->
    <el-dialog :title="previewTitle" :visible.sync="dialogVisible" width="70%" top="5vh" destroy-on-close>
      <div class="am-preview-body" v-html="previewHtml"></div>
    </el-dialog>
  </div>
</template>

<script>
import { deleteArticle, getArticles } from "@/api";
import { marked } from "marked";
import hljs from "highlight.js";

export default {
  data() {
    return {
      tableData: [],
      allData: [],
      keyword: "",
      currentPage: 1,
      pageSize: 15,
      dialogVisible: false,
      previewTitle: "",
      previewHtml: "",
    };
  },
  computed: {
    filteredData() {
      if (!this.keyword) return this.allData;
      const kw = this.keyword.toLowerCase();
      return this.allData.filter(
        (row) =>
          (row.title || "").toLowerCase().includes(kw) ||
          (row.userId || "").toLowerCase().includes(kw)
      );
    },
    pagedData() {
      const start = (this.currentPage - 1) * this.pageSize;
      return this.filteredData.slice(start, start + this.pageSize);
    },
    total() {
      return this.filteredData.length;
    },
  },
  watch: {
    keyword() { this.currentPage = 1; },
  },
  mounted() { this.loadData(); },
  methods: {
    async loadData() {
      try {
        const res = await getArticles(false, -1, { pageSize: 0, pageNumber: 0, sort: "desc" });
        this.allData = res.data?.data || [];
        this.tableData = this.allData;
      } catch (e) { this.$message.error("加载失败"); }
    },
    rowClass({ rowIndex }) {
      return rowIndex % 2 ? "am-even" : "";
    },
    preview(row) {
      this.previewTitle = row.title || "预览";
      this.previewHtml = this.md2html(row.content || "");
      this.dialogVisible = true;
    },
    md2html(text) {
      try {
        marked.setOptions({ highlight: (c) => hljs.highlightAuto(c).value, breaks: true });
        return marked(text);
      } catch (e) { return text; }
    },
    handleDelete(row) {
      this.$confirm("确认删除该文章？", "提示", { type: "warning" }).then(() => {
        deleteArticle(row.id).then((res) => {
          if (res.code === 0) {
            this.$message.success("已删除");
            this.allData = this.allData.filter((r) => r.id !== row.id);
          } else {
            this.$message.error("删除失败");
          }
        }).catch(() => this.$message.error("删除失败"));
      }).catch(() => {});
    },
  },
};
</script>

<style scoped>
.article-manage { padding: 0; }
.am-toolbar {
  display: flex; align-items: center; gap: 16px;
  margin-bottom: 16px;
}
.am-search { width: 280px; }
.am-total { font-size: 13px; color: #909399; }

.am-pager { margin-top: 20px; text-align: center; }

.am-preview-body {
  max-height: 70vh;
  overflow-y: auto;
  padding: 16px 20px;
  background: #fafbfc;
  border-radius: 8px;
  font-size: 15px;
  line-height: 1.8;
  color: #303133;
}
.am-preview-body >>> pre {
  background: #1e1e1e; color: #d4d4d4;
  padding: 14px 16px; border-radius: 8px;
  overflow-x: auto; font-size: 13px;
}
.am-preview-body >>> code {
  font-family: "Fira Code", monospace; font-size: 13px;
}
</style>

<style>
.am-even { background: #fafbfc; }
</style>
