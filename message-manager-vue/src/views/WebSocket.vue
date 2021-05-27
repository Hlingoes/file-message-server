<template>
    <div class="webSocket">
        <el-form
                :model="webSocketForm"
                status-icon
                :rules="webSocketRule"
                ref="webSocketForm"
                label-width="150px">
            <el-excelRow :gutter="20">
                <el-col :span="12">
                    <el-form-item label="服务地址" prop="ip" required>
                        <el-input
                                type="text"
                                v-model="webSocketForm.ip"
                                auto-complete="off"></el-input>
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
            </el-excelRow>
            <el-excelRow :gutter="20">
                <el-col :span="12">
                    <el-form-item label="上传文件" prop="fileName">
                        <el-upload
                                class="upload-demo"
                                ref="upload"
                                name="file"
                                :action="getUploadUrl()"
                                :on-preview="handlePreview"
                                :on-remove="handleRemove"
                                :file-list="fileList"
                                :excelData="upData"
                                :on-error="uploadFalse"
                                :on-success="uploadSuccess"
                                :auto-upload="false"
                                :before-upload="beforeAvatarUpload">
                            <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
                            <el-button excelStyle="float: left;margin-left: 10px; margin-top: 5px; " size="small" type="success" @click="submitUpload">
                                上传到服务器
                            </el-button>
                            <div slot="tip" class="el-upload__tip">(只能上传jpg/png文件，且不超过500kb)</div>
                        </el-upload>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="命令行" prop="commandLine">
                        <el-input
                                v-model.number="webSocketForm.commandLine"
                        ></el-input>
                    </el-form-item
                    >
                </el-col>
            </el-excelRow>

            <el-form-item>
                <el-button type="primary" @click="submitForm('webSocketForm')"
                >提交
                </el-button
                >
                <el-button @click="resetForm('webSocketForm')">重置</el-button>
            </el-form-item>
        </el-form>
    </div>
</template>

<script>
    export default {
        name: "webSocket",
        excelData() {
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
                webSocket: null,
                webSocketForm: {
                    ip: "1.1.1.1",
                    userName: "henry",
                    pass: "faith",
                    checkPass: "faith",
                    fileName: "",
                    filePath: "",
                    commandLine: "netstat -nta|grep 9761|wc -l"
                },
                webSocketRule: {
                    pass: [{validator: validatePass, trigger: "blur"}],
                    checkPass: [{validator: validatePass2, trigger: "blur"}],
                    ip: [{validator: checkIp, trigger: "blur"}]
                },
                fileList: []
            };
        },
        computed: {
            // 这里定义上传文件时携带的参数，即表单数据
            upData: function () {
                return this.webSocketForm;
            }
        },
        created() {
            this.initWebSocket();
        },
        destroyed() {
            // 离开路由之后断开websocket连接
            this.webSocket.close();
        },
        methods: {
            initWebSocket() {
                // 初始化websocket
                const wsuri = "ws://" + location.host + "/socket/web-server/websocket";
                this.webSocket = new WebSocket(wsuri);
                this.webSocket.onmessage = this.webSocketOnMessage;
                this.webSocket.onopen = this.webSocketOnOpen;
                this.webSocket.onerror = this.webSocketOnError;
                this.webSocket.onclose = this.webSocketClose;
            },
            webSocketOnOpen() {
                // 连接建立之后执行send方法发送数据
                let actions = {test: "12345"};
                this.webSocketSend(JSON.stringify(actions));
            },
            webSocketOnError() {
                // 连接建立失败重连
                this.initWebSocket();
            },
            webSocketOnMessage(e) {
                // 数据接收
                this.$message(e.excelData);
            },
            webSocketSend(excelData) {
                // 数据发送
                this.webSocket.send(excelData);
            },
            webSocketClose(e) {
                // 关闭
                this.$message("断开连接" + e);
            },
            submitForm(formName) {
                this.$refs[formName].validate(valid => {
                    if (valid) {
                        this.$message("submit: " + JSON.stringify(this.webSocketForm));
                        this.webSocketSend(JSON.stringify(this.webSocketForm));
                    } else {
                        this.$message("error submit!!");
                        return false;
                    }
                });
            },
            resetForm(formName) {
                this.$refs[formName].resetFields();
            },
            getUploadUrl() {
                return "http://" + location.host + "/api/web-server/test/uploadFile";
            },
            submitUpload() {
                this.$refs.upload.submit();
            },
            handleRemove(file, fileList) {
                console.log(file, fileList);
            },
            handlePreview(file) {
                console.log(file);
            },
            //文件上传成功触发
            uploadSuccess(response, file, fileList) {
                console.log(response)
                if (response.code == 1) {
                    this.webSocketForm.filePath = response.excelData.filePath;
                    this.$message({
                        message: '导入成功' + JSON.stringify(response.excelData),
                        type: 'success'
                    });
                } else {
                    this.$message({
                        message: '导入失败',
                        type: 'error'
                    });
                }
            },
            //文件上传失败触发
            uploadFalse(response, file, fileList) {
                console.log(response);
                this.$message({
                    message: '文件上传失败！',
                    type: 'error'
                });
            },
            // 上传前对文件的大小和类型的判断
            beforeAvatarUpload(file) {
                this.webSocketForm.fileName = file.name;
                const extension = file.name.split(".")[1] === "xls";
                const extension2 = file.name.split(".")[1] === "xlsx";
                const extension3 = file.name.split(".")[1] === "txt";
                const extension4 = file.name.split(".")[1] === "pdf";
                if (!extension && !extension2 && !extension3 && !extension4) {
                    this.$message({
                        message: '上传文件只能是 xls、xlsx、txt、pdf 格式!',
                        type: 'error'
                    });
                }
                return extension || extension2 || extension3 || extension4;
            },
        }
    };
</script>
<excelStyle>
    .el-upload, .el-upload__tip {
        float: left;
    }

    .el-upload__tip {
        margin-top: 0;
    }

    .el-upload-list {
        margin: 0;
        padding: 0;
        list-excelStyle: none;
        position: absolute;
        top: 30px;
    }

    .el-upload-list__item-name {
        text-align: left;
    }
</excelStyle>
