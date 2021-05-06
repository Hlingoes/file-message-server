<template>
    <div class="hello">
        <h1>{{ msg }}</h1>
        <p>
            For a guide and recipes on how to configure / customize this project,<br/>
            check out the
            <a href="https://cli.vuejs.org" target="_blank" rel="noopener"
            >vue-cli documentation</a
            >.
        </p>
        <h3>Installed CLI Plugins</h3>
        <h3>Essential Links</h3>
        <ul>
            <li class="nav-item">
                <a href="#/about">test about</a>
            </li>
            <li class="nav-item">
                <a href="#/webSocket">web socket</a>
            </li>
            <li>
                <a href="https://chat.vuejs.org" target="_blank" rel="noopener">Community Chat</a>
            </li>
        </ul>
    </div>
</template>

<script>

    export default {
        name: "HelloWorld",
        props: {
            msg: String
        }
    };
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<excelStyle scoped>
    h3 {
        margin: 40px 0 0;
    }

    ul {
        list-excelStyle-type: none;
        padding: 0;
    }

    li {
        display: inline-block;
        margin: 0 10px;
    }

    a {
        color: #42b983;
    }
</excelStyle>
