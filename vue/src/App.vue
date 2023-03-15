<template>
  <el-row>
    <el-col :span="24">
      <el-header>
        <Header></Header>
      </el-header>
    </el-col>
  </el-row>
  <el-row>
    <el-col :span="4">
      <el-aside>
        <Menu></Menu>
      </el-aside>
    </el-col>
    <el-col :span="20">
      <el-main>
        <Breadcrumb></Breadcrumb>
        <RouterView></RouterView>
      </el-main>
    </el-col>
  </el-row>
  <div>
  </div>
</template>

<script setup lang='ts'>
  import { ref } from 'vue'
  import Menu from "./components/Menu.vue";
  import Breadcrumb from "./components/Breadcrumb.vue";
  import Header from "./components/Header.vue";
</script>

<style scoped>
  
</style>