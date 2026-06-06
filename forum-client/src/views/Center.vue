<template>
  <div class="center-page">
    <div class="center-container">
      <!-- 顶部信息卡 -->
      <div class="profile-card">
        <div class="profile-avatar-col">
          <div class="profile-avatar-ring">
            <img :src="attachImageUrl(user.avatar)" class="profile-avatar" @error="e => e.target.src = avatarFallback(user.username, 100)" />
          </div>
        </div>
        <div class="profile-info-col">
          <div class="profile-name-row">
            <h2 class="profile-name">{{ user.username }}</h2>
            <span :class="['profile-badge', user.role === 'ADMIN' ? 'admin' : 'member']">
              {{ user.role === 'ADMIN' ? '管理员' : '会员' }}
            </span>
          </div>
          <p class="profile-bio" v-if="user.introduction">{{ user.introduction }}</p>
          <p class="profile-bio placeholder" v-else>这个人很懒，什么都没写...</p>
          <div class="profile-stats">
            <div class="stat-item">
              <span class="stat-value">{{ user.score || 0 }}</span>
              <span class="stat-label">积分</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <span class="stat-value">{{ articles.length }}</span>
              <span class="stat-label">收藏文章</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <span class="stat-value">{{ sources.length }}</span>
              <span class="stat-label">收藏资源</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <span class="stat-value">{{ browsingHistory.length }}</span>
              <span class="stat-label">浏览记录</span>
            </div>
          </div>
        </div>
        <router-link to="/setting" class="profile-edit-btn">
          <i class="el-icon-edit"></i> 编辑资料
        </router-link>
      </div>

      <!-- 详细信息 -->
      <div class="detail-card">
        <h3 class="card-title"><i class="el-icon-user"></i> 基本信息</h3>
        <div class="detail-grid">
          <div class="detail-item">
            <i class="el-icon-user detail-icon"></i>
            <span class="detail-label">用户名</span>
            <span class="detail-value">{{ user.username }}</span>
          </div>
          <div class="detail-item">
            <i class="el-icon-mobile-phone detail-icon"></i>
            <span class="detail-label">手机</span>
            <span class="detail-value">{{ user.phoneNum || '未绑定' }}</span>
          </div>
          <div class="detail-item">
            <i class="el-icon-location-outline detail-icon"></i>
            <span class="detail-label">地区</span>
            <span class="detail-value">{{ user.location || '未知' }}</span>
          </div>
          <div class="detail-item">
            <i :class="user.sex === 1 ? 'el-icon-male' : 'el-icon-female'"
               :style="{ color: user.sex === 1 ? '#409eff' : '#f56c6c' }"
               class="detail-icon"></i>
            <span class="detail-label">性别</span>
            <span class="detail-value">{{ user.sex === 1 ? '男' : (user.sex === 0 ? '女' : '未知') }}</span>
          </div>
          <div class="detail-item">
            <i class="el-icon-message detail-icon"></i>
            <span class="detail-label">邮箱</span>
            <span class="detail-value">{{ user.email || '未绑定' }}</span>
          </div>
          <div class="detail-item">
            <i class="el-icon-date detail-icon"></i>
            <span class="detail-label">生日</span>
            <span class="detail-value">{{ formatDate(user.birth) }}</span>
          </div>
          <div class="detail-item">
            <i class="el-icon-time detail-icon"></i>
            <span class="detail-label">注册时间</span>
            <span class="detail-value">{{ formatDate(user.createTime) }}</span>
          </div>
          <div class="detail-item">
            <i class="el-icon-key detail-icon"></i>
            <span class="detail-label">账号状态</span>
            <span :class="['detail-value', user.lockState === '0' ? 'text-danger' : 'text-success']">
              {{ user.lockState === '0' ? '已锁定' : '正常' }}
            </span>
          </div>
        </div>
      </div>

      <!-- 收藏 & 浏览 -->
      <div class="list-section">
        <div class="list-card">
          <h3 class="card-title"><i class="el-icon-star-off"></i> 文章收藏</h3>
          <el-empty v-if="articles.length === 0" :image-size="80" description="暂无收藏" />
          <div v-else class="link-list">
            <router-link v-for="a in articles" :key="a.id" :to="`/view/${a.id}`" class="link-item">
              <i class="el-icon-document"></i> {{ a.title }}
            </router-link>
          </div>
        </div>
        <div class="list-card">
          <h3 class="card-title"><i class="el-icon-folder-opened"></i> 资源收藏</h3>
          <el-empty v-if="sources.length === 0" :image-size="80" description="暂无收藏" />
          <div v-else class="link-list">
            <router-link v-for="s in sources" :key="s.id" :to="`/details/${s.id}`" class="link-item">
              <i class="el-icon-link"></i> {{ s.title }}
            </router-link>
          </div>
        </div>
        <div class="list-card">
          <h3 class="card-title"><i class="el-icon-view"></i> 浏览记录</h3>
          <el-empty v-if="browsingHistory.length === 0" :image-size="80" description="暂无记录" />
          <div v-else class="link-list">
            <router-link v-for="h in browsingHistory" :key="h.id" :to="`/view/${h.id}`" class="link-item">
              <i class="el-icon-time"></i> {{ h.title }}
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mixin } from "@/mixins";
import { getArticleHasFavour, getAuthor, getBrowsingHistory, getSourceHasFavour } from "@/api";

export default {
  mixins: [mixin],
  data() {
    return {
      user: {},
      sources: [],
      articles: [],
      browsingHistory: [],
    };
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      getAuthor().then(res => this.user = res.data).catch(() => {});
      getSourceHasFavour().then(res => this.sources = res.data || []).catch(() => {});
      getArticleHasFavour().then(res => this.articles = res.data || []).catch(() => {});
      getBrowsingHistory().then(res => this.browsingHistory = res.data || []).catch(() => {});
    },
    formatDate(t) {
      if (!t) return "未知";
      const d = new Date(t);
      return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,"0")}-${String(d.getDate()).padStart(2,"0")}`;
    },
  },
};
</script>

<style scoped>
.center-page {
  min-height: calc(100vh - 60px);
  background: transparent;
}
.center-container {
  max-width: 880px;
  width: 92%;
  margin: 0 auto;
  padding: 28px 0 60px;
}

/* ====== 顶部信息卡 ====== */
.profile-card {
  position: relative;
  display: flex;
  align-items: center;
  gap: 28px;
  background: #fff;
  border-radius: 16px;
  padding: 32px 36px;
  box-shadow: 0 2px 12px rgba(0,0,0,.05);
  margin-bottom: 18px;
}
.profile-avatar-ring {
  width: 100px; height: 100px;
  border-radius: 50%;
  padding: 3px;
  background: linear-gradient(135deg, #409eff, #67c23a);
}
.profile-avatar {
  width: 100%; height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #fff;
}
.profile-info-col { flex: 1; min-width: 0; }
.profile-name-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}
.profile-name { margin: 0; font-size: 24px; font-weight: 700; color: #1a1a2e; }
.profile-badge {
  font-size: 11px; padding: 2px 10px; border-radius: 10px; font-weight: 500;
}
.profile-badge.member  { color: #409eff; background: #ecf5ff; }
.profile-badge.admin   { color: #e6a23c; background: #fdf6ec; }
.profile-bio { margin: 0 0 16px; font-size: 14px; color: #606266; line-height: 1.5; }
.profile-bio.placeholder { color: #c0c4cc; font-style: italic; }

.profile-stats { display: flex; align-items: center; gap: 0; }
.stat-item { text-align: center; padding: 0 20px; }
.stat-item:first-child { padding-left: 0; }
.stat-value { display: block; font-size: 20px; font-weight: 700; color: #1a1a2e; }
.stat-label { display: block; font-size: 12px; color: #909399; margin-top: 2px; }
.stat-divider { width: 1px; height: 32px; background: #ebeef5; }

.profile-edit-btn {
  position: absolute; top: 20px; right: 24px;
  font-size: 13px; color: #409eff; text-decoration: none;
  padding: 6px 14px; border-radius: 8px; transition: background .2s;
}
.profile-edit-btn:hover { background: #ecf5ff; }

/* ====== 详细信息 ====== */
.detail-card {
  background: #fff; border-radius: 16px;
  padding: 24px 32px; margin-bottom: 18px;
  box-shadow: 0 2px 12px rgba(0,0,0,.05);
}
.card-title {
  margin: 0 0 20px; font-size: 16px; font-weight: 600; color: #1a1a2e;
}
.card-title i { margin-right: 6px; color: #409eff; }

.detail-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 18px 12px;
}
.detail-item {
  display: flex; flex-direction: column; gap: 2px;
}
.detail-icon { font-size: 18px; color: #909399; margin-bottom: 2px; }
.detail-label { font-size: 12px; color: #909399; }
.detail-value { font-size: 14px; color: #303133; font-weight: 500; word-break: break-all; }
.text-danger { color: #f56c6c !important; }
.text-success { color: #67c23a !important; }

/* ====== 收藏 & 浏览 ====== */
.list-section {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}
.list-card {
  background: #fff; border-radius: 16px;
  padding: 24px; box-shadow: 0 2px 12px rgba(0,0,0,.05);
}
.link-list { max-height: 360px; overflow-y: auto; }
.link-item {
  display: block; padding: 10px 12px; border-radius: 8px;
  font-size: 14px; color: #303133; text-decoration: none;
  transition: background .15s; overflow: hidden;
  text-overflow: ellipsis; white-space: nowrap;
}
.link-item:hover { background: #f5f7fa; color: #409eff; }
.link-item i { margin-right: 6px; color: #909399; font-size: 13px; }

@media (max-width: 900px) {
  .profile-card { flex-direction: column; text-align: center; }
  .profile-stats { justify-content: center; }
  .detail-grid { grid-template-columns: repeat(2, 1fr); }
  .list-section { grid-template-columns: 1fr; }
  .profile-edit-btn { position: static; margin-top: 12px; }
}
</style>
