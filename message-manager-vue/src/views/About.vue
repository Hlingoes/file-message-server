<template>
    <div class="about">
        <h1>This is an about page</h1>
        <el-rate v-model="value1"
                 show-text
                 @change="getWebData">
        </el-rate>
        <div class="block">
            <el-slider v-model="value2"
                       show-input
                       @change="getConsumerData">
            </el-slider>
        </div>
        <div class="block">
            <el-steps :active="active" align-center finish-status="success">
                <el-step title="开始"></el-step>
                <el-step title="上传"></el-step>
                <el-step title="下载"></el-step>
            </el-steps>
            <el-button style="margin-top: 12px;" @click="nextStep">下一步</el-button>
        </div>
        <el-form
                :model="webSocketForm"
                status-icon
                :rules="webSocketRule"
                ref="webSocketForm"
                label-width="150px">
            <el-row :gutter="20">
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
            </el-row>
            <el-row :gutter="20">
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
                                :data="uploadData"
                                :on-error="uploadFalse"
                                :on-success="uploadSuccess"
                                :auto-upload="false"
                                :before-upload="beforeAvatarUpload">
                            <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
                            <el-button style="float: left;margin-left: 10px; margin-top: 5px; " size="small"
                                       type="success" @click="submitUpload">
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
            </el-row>

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
    // @ is an alias to /src

    export default {
        name: "about",
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
                value1: null,
                value2: 0,
                active: 0,
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
            uploadData: function () {
                return this.webSocketForm
            }
        },
        methods: {
            getWebData(data) {
                this.$axios({
                    method: 'get',
                    url: 'web-server/test/hello2',
                    params: data
                }).then((response) => {
                    //请求成功返回的数据
                    console.log(response)
                    this.$message({
                        message: '恭喜你，这是一条成功消息: ' + response.data,
                        type: 'success'
                    });
                }).catch((error) => {
                    //请求失败返回的数据
                    console.log(error)
                })
            },
            getConsumerData(data) {
                this.$axios({
                    method: 'get',
                    url: 'msg-consumer/testAddJob',
                    params: {jobName: data}
                }).then((response) => {
                    //请求成功返回的数据
                    console.log(response)
                    this.$message({
                        message: '恭喜你，这是一条成功消息: ' + response.data,
                        type: 'success'
                    });
                }).catch((error) => {
                    //请求失败返回的数据
                    console.log(error)
                })
            },
            submitForm(formName) {
                this.$refs[formName].validate(valid => {
                    if (valid) {
                        this.$message("submit: " + JSON.stringify(this.webSocketForm));
                        let data = this.webSocketForm;
                        this.$axios({
                            method: 'post',
                            url: 'web-server/test/submit',
                            data: data
                        }).then((response) => {
                            //请求成功返回的数据
                            console.log(response)
                            this.$message({
                                message: '恭喜你，这是一条成功消息: ' + JSON.stringify(response.data),
                                type: 'success'
                            });
                        }).catch((error) => {
                            //请求失败返回的数据
                            console.log(error)
                        })
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
                    this.webSocketForm.filePath = response.data.filePath;
                    this.$message({
                        message: '导入成功' + JSON.stringify(response.data),
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
            nextStep() {
                let fileName = "test_excel_" + this.active;
                if (this.active++ == 2) {
                    this.$axios({
                        method: 'get',
                        url: 'web-server/test/downloadExcel',
                        params: {fileName: fileName},
                        //一定要写
                        responseType: 'blob'
                    }).then((response) => {
                        let blob = new Blob([response.data], {
                            type: 'application/vnd.ms-excel;charset=utf-8'
                            // word文档为application/msword;charset=utf-8
                            // pdf文档为application/pdf,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8
                        });
                        let objectUrl = URL.createObjectURL(blob);
                        let link = document.createElement("a");
                        link.href = objectUrl;
                        // 下载文件的名字
                        link.setAttribute("download", fileName + '.xlsx');
                        document.body.appendChild(link);
                        // 点击下载
                        link.click();
                        // 下载完成移除元素
                        // document.body.removeChild(link);
                        // 释放掉blob对象
                        URL.revokeObjectURL(objectUrl);
                        console.log(response);
                        this.active = 0;
                    }).catch((error) => {
                        //请求失败返回的数据
                        console.log(error)
                    })
                }
            }
        }
    };
</script>
<style>
    .block {
        margin: 1em;
    }

    .el-upload, .el-upload__tip {
        float: left;
    }

    .el-upload__tip {
        margin-top: 0;
    }

    .el-upload-list {
        margin: 0;
        padding: 0;
        list-style: none;
        position: absolute;
        top: 30px;
    }

    .el-upload-list__item-name {
        text-align: left;
    }
</style>
