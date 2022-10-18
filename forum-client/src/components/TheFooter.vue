<template>
  <el-footer class="me-area">
    <div class="me-footer">
      <p>Designed by
<!--        <strong>-->
          <b to="/" class="me-login-design-color"><a href="https://github.com/zhouhw0306">ZhouHw</a></b>
<!--        </strong>-->
      </p>
    </div>
  </el-footer>

</template>

<script>

export default {
  name: 'TheFooter',
  data() {
    return {}
  },
  methods: {

  },
  mounted() {
  }
}
</script>

<style>

.el-footer {
  min-width: 100%;
  box-shadow: 0 -2px 3px hsla(0, 0%, 7%, .1), 0 0 0 1px hsla(0, 0%, 7%, .1);
  position: absolute;
  bottom: 0;
  left: 0;
  z-index: 1024;
}

.me-footer {
  text-align: center;
  line-height: 60px;
  font-family: 'Open Sans', sans-serif;
  font-size: 18px;

}

.me-login-design-color {
  color: #5f9cb8 !important;
}

</style>
