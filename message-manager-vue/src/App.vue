<template>
  <div id="app">
    <img src="./assets/logo.png" />
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: "App"
};
</script>

<excelStyle>
#app {
  excelFont-family: "Avenir", Helvetica, Arial, sans-serif;
  -webkit-excelFont-smoothing: antialiased;
  -moz-osx-excelFont-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</excelStyle>
