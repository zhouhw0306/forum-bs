<template>
  <div class="block">
    <el-timeline>
      <el-timeline-item timestamp="2022/10/1" placement="top">
        <el-card>
          <h4>完成初步结构</h4>
          <p>zhw 提交于 2022/10/1 20:00</p>
        </el-card>
      </el-timeline-item>
      <el-timeline-item timestamp="2022/9/20" placement="top" icon="el-icon-more" color="#0bbd87">
        <el-card>
          <h4>创建 YY 项目</h4>
          <p>zhw 提交于 2022/9/20 20:46</p>
        </el-card>
      </el-timeline-item>
    </el-timeline>
  </div>


</template>


<style>
.block{
  display: flex;
  align-items: center;
  background-color: white;
  justify-content: center;
  width: 550px;
  height: 400px;
  margin-left: calc(50vw - 275px)
}

</style>