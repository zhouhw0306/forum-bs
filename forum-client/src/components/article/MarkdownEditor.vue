<template>
  <mavon-editor
    style="padding-left: 25px"
    class="me-editor"
    ref="md"
    :ishljs="true"
    toolbarsBackground="#ffffff"
    v-model="editor.value"
    @imgAdd="imgAdd"
    v-bind="editor">
  </mavon-editor>
</template>


<script>

import {mavonEditor} from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'

import {upload} from '@/api/index'

export default {
  name: 'MarkdownEditor',
  props: {
    editor: Object
  },
  data() {
    return {}
  },
  mounted() {
    this.$set(this.editor, 'ref', this.$refs.md)
  },
  methods: {
    imgAdd(pos, $file) {
      let that = this
      let formData = new FormData();
      formData.append('image', $file);

      upload(formData).then(res => {
        // 第二步.将返回的url替换到文本原位置![...](./0) -> ![...](url)
        if (res.data.code === 0) {

          that.$refs.md.$img2Url(pos, res.data.data.url);
        } else {
          that.$message({message: res.data.msg, type: 'error', showClose: true})
        }

      }).catch(err => {
        that.$message({message: err, type: 'error', showClose: true});
      })
    }
  },
  components: {
    mavonEditor
  }
}
</script>
<style scoped>
  .me-editor {
    z-index: 6 !important;
  }

  .v-note-wrapper.fullscreen {
    top: 60px !important
  }
</style>
