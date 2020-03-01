<template>
  <div class="webSocket">
    <el-form
      :model="webSocketForm"
      status-icon
      :rules="webSocketRule"
      ref="webSocketForm"
      label-width="150px"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="服务地址" prop="ip" required>
            <el-input
              type="text"
              v-model="webSocketForm.ip"
              auto-complete="off"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="用户名" prop="userName" required>
            <el-input
              type="text"
              v-model="webSocketForm.userName"
              auto-complete="off"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="密码" prop="pass" required>
            <el-input
              type="password"
              v-model="webSocketForm.pass"
              auto-complete="off"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="确认密码" prop="checkPass" required>
            <el-input
              type="password"
              v-model="webSocketForm.checkPass"
              auto-complete="off"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24"
          ><el-form-item label="命令行" prop="commandLine">
            <el-input
              v-model.number="webSocketForm.commandLine"
            ></el-input> </el-form-item
        ></el-col>
      </el-row>

      <el-form-item>
        <el-button type="primary" @click="submitForm('webSocketForm')"
          >提交</el-button
        >
        <el-button @click="resetForm('webSocketForm')">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  name: "webSocket",
  data() {
    let checkIp = (rule, value, callback) => {
      if (!value) {
        return callback(new Error("IP不能为空"));
      }
      setTimeout(() => {
        let reg = /^([1-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3}$/gi;
        if (!reg.test(value)) {
          callback(new Error("请输入正确的IP地址"));
        } else {
          callback();
        }
      }, 1000);
    };
    let validatePass = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请输入密码"));
      } else {
        if (this.webSocketForm.checkPass !== "") {
          this.$refs.webSocketForm.validateField("checkPass");
        }
        callback();
      }
    };
    let validatePass2 = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请再次输入密码"));
      } else if (value !== this.webSocketForm.pass) {
        callback(new Error("两次输入密码不一致!"));
      } else {
        callback();
      }
    };
    return {
      websock: null,
      webSocketForm: {
        ip: "",
        userName: "",
        pass: "",
        checkPass: "",
        commandLine: ""
      },
      webSocketRule: {
        pass: [{ validator: validatePass, trigger: "blur" }],
        checkPass: [{ validator: validatePass2, trigger: "blur" }],
        ip: [{ validator: checkIp, trigger: "blur" }]
      }
    };
  },
  created() {
    this.initWebSocket();
  },
  destroyed() {
    // 离开路由之后断开websocket连接
    this.websock.close();
  },
  methods: {
    initWebSocket() {
      // 初始化weosocket
      const wsuri = "ws://localhost:9012/websocket";
      this.websock = new WebSocket(wsuri);
      this.websock.onmessage = this.websocketonmessage;
      this.websock.onopen = this.websocketonopen;
      this.websock.onerror = this.websocketonerror;
      this.websock.onclose = this.websocketclose;
    },
    websocketonopen() {
      // 连接建立之后执行send方法发送数据
      let actions = { test: "12345" };
      this.websocketsend(JSON.stringify(actions));
    },
    websocketonerror() {
      // 连接建立失败重连
      this.initWebSocket();
    },
    websocketonmessage(e) {
      // 数据接收
      this.$message(e.data);
    },
    websocketsend(Data) {
      // 数据发送
      this.websock.send(Data);
    },
    websocketclose(e) {
      // 关闭
      this.$message("断开连接" + e);
    },
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.$message("submit: " + JSON.stringify(this.webSocketForm));
          this.websocketsend(JSON.stringify(this.webSocketForm));
        } else {
          this.$message("error submit!!");
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    }
  }
};
</script>
<style lang="less"></style>
