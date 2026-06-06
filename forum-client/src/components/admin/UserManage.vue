<template>
  <div class="user-manage">
    <div class="um-toolbar">
      <el-input v-model="keyword" size="small" placeholder="搜索用户名/邮箱..." prefix-icon="el-icon-search" clearable class="um-search" />
      <span class="um-total">共 {{ filteredData.length }} 个用户</span>
    </div>

    <el-table :data="pagedData" style="width: 100%" size="medium" :row-class-name="rowClass">
      <el-table-column label="用户" min-width="160">
        <template slot-scope="{ row }">
          <div class="um-user-cell">
            <img :src="attachImageUrl(row.avatar)" class="um-avatar" @error="e => e.target.src = avatarFallback(row.username)" />
            <span>{{ row.username }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="性别" width="70" align="center">
        <template slot-scope="{ row }">
          <i v-if="row.sex == 0" class="el-icon-female" style="color:#f56c6c;font-size:16px" />
          <i v-else class="el-icon-male" style="color:#409eff;font-size:16px" />
        </template>
      </el-table-column>
      <el-table-column prop="email" label="邮箱" min-width="170" show-overflow-tooltip />
      <el-table-column prop="phoneNum" label="手机" width="130" show-overflow-tooltip />
      <el-table-column prop="score" label="积分" width="70" align="center" sortable />
      <el-table-column label="角色" width="80" align="center">
        <template slot-scope="{ row }">
          <el-tag :type="row.role === 'ADMIN' ? 'warning' : 'info'" size="mini" effect="plain">
            {{ row.role === 'ADMIN' ? '管理员' : '用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80" align="center">
        <template slot-scope="{ row }">
          <el-tag :type="row.lockState === '0' ? 'danger' : 'success'" size="mini" effect="plain">
            {{ row.lockState === '0' ? '已禁用' : '正常' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="160" />
      <el-table-column label="操作" width="100" align="center">
        <template slot-scope="{ row }">
          <el-button
            v-if="row.lockState !== '0'"
            size="mini" type="danger" plain
            @click="handleToggle(row)"
          >禁用</el-button>
          <el-button
            v-else
            size="mini" type="success" plain
            @click="handleToggle(row)"
          >解禁</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="total > pageSize"
      class="um-pager"
      background
      layout="prev, pager, next"
      :total="total"
      :page-size="pageSize"
      :current-page.sync="currentPage"
    />
  </div>
</template>

<script>
import { getAllUser, lockOrUnlock } from "@/api";
import { mixin } from "@/mixins";

export default {
  mixins: [mixin],
  data() {
    return {
      allData: [],
      keyword: "",
      currentPage: 1,
      pageSize: 15,
    };
  },
  computed: {
    filteredData() {
      if (!this.keyword) return this.allData;
      const kw = this.keyword.toLowerCase();
      return this.allData.filter(
        (r) =>
          (r.username || "").toLowerCase().includes(kw) ||
          (r.email || "").toLowerCase().includes(kw) ||
          (r.phoneNum || "").includes(kw)
      );
    },
    pagedData() {
      const s = (this.currentPage - 1) * this.pageSize;
      return this.filteredData.slice(s, s + this.pageSize);
    },
    total() { return this.filteredData.length; },
  },
  watch: { keyword() { this.currentPage = 1; } },
  mounted() { this.loadData(); },
  methods: {
    async loadData() {
      try {
        const res = await getAllUser();
        if (res.code === 0) this.allData = res.data || [];
        else this.$message.error(res.msg);
      } catch (e) { this.$message.error("加载失败"); }
    },
    rowClass({ rowIndex }) { return rowIndex % 2 ? "um-even" : ""; },
    handleToggle(row) {
      const action = row.lockState === "0" ? "解禁" : "禁用";
      this.$confirm(`确认${action}用户「${row.username}」？`, "提示", { type: "warning" }).then(() => {
        lockOrUnlock(row.id).then((res) => {
          if (res.code === 0) {
            row.lockState = res.data === 0 ? "0" : "1";
            this.$message.success(`${action}成功`);
          } else {
            this.$message.error(res.msg);
          }
        }).catch(() => this.$message.error("操作失败"));
      }).catch(() => {});
    },
  },
};
</script>

<style scoped>
.user-manage { padding: 0; }
.um-toolbar {
  display: flex; align-items: center; gap: 16px;
  margin-bottom: 16px;
}
.um-search { width: 260px; }
.um-total { font-size: 13px; color: #909399; }
.um-user-cell { display: flex; align-items: center; gap: 10px; }
.um-avatar {
  width: 32px; height: 32px;
  border-radius: 50%; object-fit: cover;
  border: 1px solid #eee;
}
.um-pager { margin-top: 20px; text-align: center; }
</style>

<style>
.um-even { background: #fafbfc; }
</style>
