<template>
  <div class="admin-dash">
    <!-- 统计卡片 -->
    <div class="stat-grid">
      <div class="stat-card green">
        <div class="stat-icon"><i class="el-icon-user-solid"></i></div>
        <div class="stat-body">
          <span class="stat-num">{{ userCount }}</span>
          <span class="stat-label">用户总数</span>
        </div>
      </div>
      <div class="stat-card blue">
        <div class="stat-icon"><i class="el-icon-circle-plus-outline"></i></div>
        <div class="stat-body">
          <span class="stat-num">{{ newCustomer }}</span>
          <span class="stat-label">昨日新增</span>
        </div>
      </div>
      <div class="stat-card red">
        <div class="stat-icon"><i class="el-icon-document"></i></div>
        <div class="stat-body">
          <span class="stat-num">{{ articleCount }}</span>
          <span class="stat-label">帖子数量</span>
        </div>
      </div>
      <div class="stat-card orange">
        <div class="stat-icon"><i class="el-icon-folder-opened"></i></div>
        <div class="stat-body">
          <span class="stat-num">{{ sourceCount }}</span>
          <span class="stat-label">资源数量</span>
        </div>
      </div>
      <div class="stat-card purple">
        <div class="stat-icon"><i class="el-icon-star-on"></i></div>
        <div class="stat-body">
          <span class="stat-num">{{ avgScore }}</span>
          <span class="stat-label">人均积分</span>
        </div>
      </div>
      <div class="stat-card cyan">
        <div class="stat-icon"><i class="el-icon-medal-1"></i></div>
        <div class="stat-body">
          <span class="stat-num">{{ topScore }}</span>
          <span class="stat-label">最高积分</span>
        </div>
      </div>
    </div>

    <!-- 图表区 -->
    <div class="chart-row">
      <div class="chart-card">
        <h4><i class="el-icon-data-analysis"></i> 用户性别比例</h4>
        <ve-ring :data="userSex" height="340px" :key="'sex-'+userCount"></ve-ring>
      </div>
      <div class="chart-card">
        <h4><i class="el-icon-data-analysis"></i> 用户地区分布</h4>
        <ve-histogram :data="userArea" :extend="barExtend" height="340px" :key="'area-'+userCount"></ve-histogram>
      </div>
    </div>

    <div class="chart-row">
      <div class="chart-card chart-wide">
        <h4><i class="el-icon-data-analysis"></i> 用户积分区间</h4>
        <ve-histogram :data="scoreRange" :extend="scoreBarExtend" height="300px" :key="'score-'+userCount"></ve-histogram>
      </div>
    </div>

    <div class="chart-row">
      <div class="chart-card">
        <h4><i class="el-icon-data-analysis"></i> 用户角色分布</h4>
        <ve-ring :data="roleData" height="340px" :key="'role-'+userCount"></ve-ring>
      </div>
      <div class="chart-card">
        <h4><i class="el-icon-data-analysis"></i> 近30天注册趋势</h4>
        <ve-line v-if="userCount" :data="registerTrend" :extend="lineExtend" height="340px" :key="'trend-'+userCount"></ve-line>
      </div>
    </div>
  </div>
</template>

<script>
import { mixin } from "@/mixins";
import { getAllUser, getSourceAll, getAllNoPage } from "@/api";

export default {
  mixins: [mixin],
  data() {
    return {
      userCount: 0,
      newCustomer: 0,
      articleCount: 0,
      sourceCount: 0,
      avgScore: 0,
      topScore: 0,
      user: [],
      userSex: { columns: ["name", "value"], rows: [] },
      userArea: { columns: ["name", "value"], rows: [] },
      scoreRange: { columns: ["name", "value"], rows: [] },
      roleData: { columns: ["name", "value"], rows: [] },
      registerTrend: { columns: ["name", "value"], rows: [] },
      pieSettings: { radius: ["40%", "65%"] },
      pieExtend: {
        legend: { bottom: 0 },
        color: ["#5da8f5", "#ef8888", "#67c23a", "#e6a23c", "#909399", "#f56c6c", "#409eff", "#eeb45d"],
      },
      barExtend: {
        color: ["#409eff"],
        grid: { left: "3%", right: "4%", bottom: "3%", containLabel: true },
        xAxis: { axisLabel: { rotate: 30 } },
      },
      scoreBarExtend: {
        color: ["#67c23a"],
        grid: { left: "3%", right: "4%", bottom: "3%", containLabel: true },
      },
      lineExtend: {
        color: ["#409eff"],
        grid: { left: "3%", right: "4%", bottom: "3%", containLabel: true },
        xAxis: { axisLabel: { rotate: 30 } },
      },
    };
  },
  mounted() {
    this.loadAll();
  },
  methods: {
    async loadAll() {
      await this.loadUsers();
      await this.loadStats();
    },
    async loadUsers() {
      try {
        const res = await getAllUser();
        const users = res.data || [];
        this.user = users;
        this.userCount = users.length;

        // 性别
        const maleCount = users.filter(u => u.sex == 1).length;
        const femaleCount = users.filter(u => u.sex == 0).length;
        this.userSex = {
          columns: ["name", "value"],
          rows: [{ name: "男", value: maleCount }, { name: "女", value: femaleCount }]
        };

        // 角色分布
        const roleMap = {};
        users.forEach(u => { const r = u.role || "USER"; roleMap[r] = (roleMap[r] || 0) + 1; });
        this.roleData = {
          columns: ["name", "value"],
          rows: Object.entries(roleMap).map(([k, v]) => ({ name: k === "ADMIN" ? "管理员" : "普通用户", value: v }))
        };

        // 昨日新增 + 注册趋势
        const yesterday = new Date(); yesterday.setDate(yesterday.getDate() - 1);
        const ymd = `${yesterday.getFullYear()}-${String(yesterday.getMonth()+1).padStart(2,"0")}-${String(yesterday.getDate()).padStart(2,"0")}`;
        this.newCustomer = 0;
        this.registerTrend = this.buildRegisterTrend(users);

        // 地区分布
        const areaMap = {};
        users.forEach(u => {
          const loc = (u.location || "未知").replace(/省|市|自治区|壮族|维吾尔|回族|特别行政区/g, "").trim() || "未知";
          areaMap[loc] = (areaMap[loc] || 0) + 1;
          const ct = u.createTime ? new Date(u.createTime).toISOString().slice(0, 10) : "";
          if (ct === ymd) this.newCustomer++;
        });
        this.userArea = {
          columns: ["name", "value"],
          rows: Object.entries(areaMap).sort((a, b) => b[1] - a[1]).slice(0, 12).map(([k, v]) => ({ name: k, value: v }))
        };

        // 积分区间
        const ranges = [
          { label: "0-50", min: 0, max: 50 }, { label: "50-100", min: 50, max: 100 },
          { label: "100-200", min: 100, max: 200 }, { label: "200-500", min: 200, max: 500 },
          { label: "500+", min: 500, max: Infinity },
        ];
        this.scoreRange = {
          columns: ["name", "value"],
          rows: ranges.map(r => ({ name: r.label, value: users.filter(u => (u.score || 0) >= r.min && (u.score || 0) < r.max).length }))
        };

        // 积分统计
        const scores = users.map(u => u.score || 0);
        this.avgScore = users.length ? Math.round(scores.reduce((a, b) => a + b, 0) / users.length) : 0;
        this.topScore = scores.length ? Math.max(...scores) : 0;
      } catch (e) { console.error("加载用户数据失败", e); }
    },
    async loadStats() {
      try {
        const res = await getAllNoPage();
        this.articleCount = res.data || 0;
      } catch (e) {}
      try {
        const res = await getSourceAll("1");
        this.sourceCount = (res.data || []).length;
      } catch (e) {}
    },
    buildRegisterTrend(users) {
      const now = new Date();
      const map = {};
      for (let i = 29; i >= 0; i--) {
        const d = new Date(now); d.setDate(d.getDate() - i);
        map[d.toISOString().slice(0, 10)] = 0;
      }
      users.forEach(u => {
        const ct = u.createTime ? new Date(u.createTime).toISOString().slice(0, 10) : "";
        if (map[ct] !== undefined) map[ct]++;
      });
      return {
        columns: ["name", "value"],
        rows: Object.entries(map).map(([k, v]) => ({ name: k.slice(5), value: v })),
      };
    },
  },
};
</script>

<style scoped>
.admin-dash { padding-bottom: 40px; }

/* 统计卡片 */
.stat-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}
.stat-card {
  display: flex; align-items: center; gap: 14px;
  border-radius: 14px; padding: 18px 20px; color: #fff; box-shadow: 0 3px 12px rgba(0,0,0,.08);
}
.stat-card.green  { background: linear-gradient(135deg, #67c23a, #85ce61); }
.stat-card.blue   { background: linear-gradient(135deg, #409eff, #66b1ff); }
.stat-card.red    { background: linear-gradient(135deg, #f56c6c, #f78989); }
.stat-card.orange { background: linear-gradient(135deg, #e6a23c, #ebb563); }
.stat-card.purple { background: linear-gradient(135deg, #9b59b6, #b07cc6); }
.stat-card.cyan   { background: linear-gradient(135deg, #00bcd4, #4dd0e1); }
.stat-icon {
  width: 48px; height: 48px; border-radius: 12px;
  background: rgba(255,255,255,.2); display: flex;
  align-items: center; justify-content: center; font-size: 22px;
}
.stat-body { display: flex; flex-direction: column; }
.stat-num  { font-size: 26px; font-weight: 700; line-height: 1.1; }
.stat-label { font-size: 13px; opacity: .85; margin-top: 2px; }

/* 图表 */
.chart-row { display: flex; gap: 16px; margin-bottom: 16px; }
.chart-card {
  flex: 1; background: #fff; border-radius: 14px;
  padding: 14px 24px 20px; box-shadow: 0 2px 10px rgba(0,0,0,.04);
}
.chart-wide { flex: 2; }
.chart-card h4 { margin: 0 0 4px; font-size: 15px; color: #1a1a2e; }
.chart-card h4 i { color: #409eff; margin-right: 6px; }

@media (max-width: 1400px) {
  .stat-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 900px) {
  .stat-grid { grid-template-columns: repeat(2, 1fr); }
  .chart-row { flex-direction: column; }
  .chart-card, .chart-wide { flex: none; }
  .chart-card { padding: 14px 16px 20px; }
}
@media (max-width: 560px) {
  .stat-grid { grid-template-columns: 1fr 1fr; gap: 10px; }
  .stat-card { padding: 14px; gap: 10px; }
  .stat-icon { width: 36px; height: 36px; font-size: 18px; }
  .stat-num { font-size: 20px; }
}
</style>
