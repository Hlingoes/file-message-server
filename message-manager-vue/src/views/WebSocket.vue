<template>
  <div class="webSocket">

  </div>
</template>

<script>
  export default {
    name : 'webSocket',
    data() {
      return {
        websock: null,
      }
    },
    created() {
      this.initWebSocket();
    },
    destroyed() {
        //离开路由之后断开websocket连接
        this.websock.close()
    },
    methods: {
      initWebSocket() { //初始化weosocket
        const wsuri = "ws://localhost:9012/websocket";
        this.websock = new WebSocket(wsuri);
        this.websock.onmessage = this.websocketonmessage;
        this.websock.onopen = this.websocketonopen;
        this.websock.onerror = this.websocketonerror;
        this.websock.onclose = this.websocketclose;
      },
      websocketonopen() { 
        //连接建立之后执行send方法发送数据
        let actions = {"test":"12345"};
        this.websocketsend(JSON.stringify(actions));
      },
      websocketonerror() {
        //连接建立失败重连
        this.initWebSocket();
      },
      websocketonmessage(e) { 
        //数据接收
        const redata = JSON.parse(e.data);
      },
      websocketsend(Data) {
        //数据发送
        this.websock.send(Data);
      },
      websocketclose(e) {  
        //关闭
        console.log('断开连接',e);
      },
    },
  }
</script>
<style lang='less'>
 
</style>