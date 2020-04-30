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
    </div>
</template>
<script>
    // @ is an alias to /src

    export default {
        name: "about",
        data() {
            return {
                value1: null,
                value2: 0
            }
        },
        methods: {
            getWebData(data) {
                this.$axios({
                    method: 'get',
                    url: 'web-server/test/hello2',
                    data: data
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
            }
        }
    };
</script>
