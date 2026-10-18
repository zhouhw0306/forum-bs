<template>
  <transition>
    <button @click="toTop" v-show="topShow" class="me-to-top" aria-label="回到顶部"><i class="el-icon-top"></i></button>
  </transition>
</template>

<script>
  export default {
    name: 'GoTop',
    data() {
      return {
        topShow: false
      }
    },
    methods: {
      toTop() {
        window.scrollTo({top:0,left:0,behavior:'smooth'})
        this.topShow = false;
      },
      needToTop() {
        let curHeight = document.documentElement.scrollTop || document.body.scrollTop;
        this.topShow = curHeight > 400;
      }
    },
    beforeDestroy() { window.removeEventListener('scroll', this.needToTop); },
    mounted() {
      /**
       * 等到整个视图都渲染完毕
       */
      this.$nextTick(function () {
        window.addEventListener('scroll', this.needToTop);
      });
    }
  }
</script>

<style scoped>
.me-to-top { position: fixed; right: 24px; bottom: 24px; display: grid; place-items: center; width: 36px; height: 36px; background: var(--surface); border: 1px solid var(--border); border-radius: 6px; cursor: pointer; color: var(--text-muted); z-index: 5; font-size: 18px; box-shadow: 0 1px 2px #1f23280a; }
.me-to-top:hover { color: var(--text); background: var(--hover); }
@media (max-width: 600px) { .me-to-top { right: 16px; bottom: 16px; } }
</style>
