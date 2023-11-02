<template>
  <div>
    <div v-hljs style="overflow:auto; border-top: 1px solid #ccc;margin-bottom: 60px;margin-right: 10px" v-html="content"></div>

    <div class="ai-input-div">
      <el-input class="ai-input"
        :autosize="{ minRows: 2, maxRows: 4}"
        placeholder="请输入内容"
        @keyup.enter.native="send"
        v-model="issue">
        <el-button @click="send" slot="append" type="primary">发送</el-button>
      </el-input>
    </div>


  </div>
</template>

<script>
import {mapGetters} from "vuex";
import {chatAi} from "@/api";
import {marked} from 'marked';
import hljs from "highlight.js";

export default {
  name: 'AiModel',
  data() {
    return {
      content:'',
      issue: '',
    }
  },
  computed: {
    ...mapGetters([
      'avatar',
      'username'
    ])
  },
  mounted() {
    this.init()
  },
  methods:{
    init(){
      this.pushAiMsg('你好，你可以向我提问')
    },
    send(){
      let rendererMD = new marked.Renderer();
      marked.setOptions({
        renderer: rendererMD,
        highlight: function(code) {
          return hljs.highlightAuto(code).value;
        },
        pedantic: false,
        gfm: true,
        tables: true,
        breaks: false,
        sanitize: false,
        smartLists: true,
        smartypants: false,
        xhtml: false
      })
      this.pushUserMsg(this.issue)
      let param = new URLSearchParams()
      param.append('issue',this.issue)
      chatAi(param).then(res => {
        this.pushAiMsg(marked(res.data))
      })
      this.issue = ''
    },
    pushAiMsg(text){
      this.content += `<div class="el-row" style="padding: 5px 0">
                        <div class="el-col el-col-2" style="text-align: right">
                          <span class="el-avatar el-avatar--circle" style="height: 40px; width: 40px; line-height: 40px">
                            <img src="https://static.vecteezy.com/system/resources/previews/022/227/364/non_2x/openai-chatgpt-logo-icon-free-png.png"/>
                          </span>
                        </div>
                        <div class="el-col el-col-22" style="text-align: left; padding-left: 10px">
                          <div class="tip right">${text}</div>
                        </div>
                      </div>`;
    },
    pushUserMsg(text){
      this.content +=  `<div class="el-row" style="padding: 5px 0">
                          <div class="el-col el-col-22" style="text-align: right; padding-right: 10px">
                            <div class="tip left">${text}</div>
                          </div>
                          <div class="el-col el-col-2">
                            <span class="el-avatar el-avatar--circle" style="height: 40px; width: 40px; line-height: 40px">
                              <img src="${this.avatar}"/>
                            </span>
                          </div>
                        </div>`;
    }
  }

}
</script>
<style>
.ai-input-div{
  position: absolute;
  background-color: #ffffff;
  bottom: 0;
  width: 100%;
  height: 60px;
}
.ai-input{
  position: absolute;
  width: 95%;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  margin: auto;
}
.tip {
  color: black;
  border-radius: 10px;
  /*font-family: sans-serif;*/
  font-family: 'Courier New', Courier, monospace;
  padding: 10px;
  width:auto;
  display:inline-block;
}
.right {
  background-color: whitesmoke;
}
.left {
  background-color: whitesmoke;
}
</style>

