<template>
  <div ref="scroll" class="scroll-page">
    <slot />
    <div v-if="loading" class="scroll-loading" role="status"><i class="el-icon-loading"></i> 正在加载讨论…</div>
    <div v-else-if="!noData" class="scroll-more"><el-button size="small" @click="$emit('load')">加载更多讨论 <i class="el-icon-arrow-down"></i></el-button></div>
  </div>
</template>
<script>
export default {
  name: 'ScrollPage',
  props: { loading: Boolean, noData: Boolean, offset: { type: Number, default: 100 } },
  data: () => ({ previousY: 0 }),
  mounted() { this.previousY = window.scrollY; window.addEventListener('scroll', this.handleScroll, { passive: true }) },
  beforeDestroy() { window.removeEventListener('scroll', this.handleScroll) },
  methods: {
    handleScroll() {
      const currentY = window.scrollY
      const movingDown = currentY > this.previousY
      this.previousY = currentY
      if (movingDown && !this.loading && !this.noData && this.$refs.scroll.getBoundingClientRect().bottom <= window.innerHeight + this.offset) this.$emit('load')
    }
  }
}
</script>
<style scoped>
.scroll-loading, .scroll-more { display: flex; align-items: center; justify-content: center; padding: 20px; gap: 8px; font-size: 12px; color: var(--text-muted); border-top: 1px solid var(--border-subtle); }
</style>
