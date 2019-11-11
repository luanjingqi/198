<template>
  <div class="hello">
    <h1>欢迎来到XBoot前后端分离开发平台单点登录测试站点</h1>
    <h1>sso.exrick.cn</h1>

    <div v-if="login">
      <h3>当前登录用户信息</h3>
      <br />
      <Avatar :src="userInfo.avatar" size="large" />
      <br />
      <br />
      <p>用户名：{{userInfo.username}}</p>
      <Button @click="logout" type="primary" style="margin-top:10px;margin-right:10px;">退出登录测试</Button>
      <a href="http://xboot.exrick.cn/login" target="_blank">
        <Button style="margin-top:10px;">返回主站点</Button>
      </a>
    </div>

    <div v-else>
      <h3>
        您还未登录，请先去主站点
        <a href="http://xboot.exrick.cn/login" target="_blank">xboot.exrick.cn</a> 登录
        <br />登录成功后刷新该页面重试
      </h3>

      <a href="http://xboot.exrick.cn/login" target="_blank">
        <Button type="primary" style="margin-top:10px;">去主站点登录</Button>
      </a>
      <Button style="margin-top:10px;margin-left:10px;" @click="init">刷新</Button>
    </div>

    <h3>XBoot完整版获取</h3>
    <ul>本站源码可通过XBoot完整版获取</ul>
    <a href="http://xpay.exrick.cn/pay?xboot" target="_blank">
      <Button type="error" style="margin-top:10px;">立即获取完整版</Button>
    </a>
    <br />
    <br />
    <br />
    <br />
    <br />
    <h3>
      By:
      <a href="http://exrick.cn" target="_blank">Exrick</a>
    </h3>

    <Modal
      v-model="modal"
      title="Common Modal dialog box title"
      @on-ok="ok"
      footer-hide
      width="620"
      :styles="{top: '230px'}"
      @on-cancel="cancel"
    >
      <p slot="header" style="border: none !important"></p>
      <div class="wrap">
        <div class="title" style>快速安全登录</div>
        <div class="tips">
          检测到您的账号已在其他站点登录，
          <br />点击头像直接授权登录。
        </div>
        <div class="avatar" @click="confirmLogin" v-if="!loading">
          <img :src="data.avatar" class="img" />
          <span class="img_out_focus"></span>
        </div>
        <div style="position:absolute;top:30px;">
          <RectLoading v-if="loading" />
        </div>

        <span class="nick" @click="confirmLogin" v-if="!loading">{{data.username}}</span>
        <div class="bottom">
          <a href="http://xboot.exrick.cn/regist" class="link" target="_blank">注册新帐号</a>
        </div>
      </div>
    </Modal>
  </div>
</template>

<script>
import Cookies from "js-cookie";
import RectLoading from "./rect-loading";
export default {
  name: "HelloWorld",
  components: {
    RectLoading
  },
  data() {
    return {
      loading: false,
      login: false,
      modal: false,
      data: {},
      userInfo: {}
    };
  },
  methods: {
    init() {
      let accessToken = Cookies.get("accessToken");
      if (accessToken) {
        this.setStore("accessToken", accessToken);
        this.loading = true;
        this.getRequest("/user/info").then(res => {
          let that = this;
          this.loading = false;
          if (res.success) {
            this.data = res.result;
          }
        });
        this.modal = true;
      } else {
        this.$Message.warning("未检测到主站点登录用户");
      }
    },
    confirmLogin() {
      this.login = true;
      this.userInfo = this.data;
      this.modal = false;
    },
    logout() {
      this.$Modal.info({
        title: "退出登录测试方法",
        content: "您可以返回主站点退出登陆后，刷新本页面观察登录状态"
      });
    }
  },
  mounted() {
    this.init();
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="less">
.wrap {
  min-height: 287px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.title {
  font-size: 20px;
  color: #333;
  margin-bottom: 16px;
}

.tips {
  color: #666;
  font-size: 16px;
  text-align: center;
  margin-bottom: 36px;
}

.avatar {
  position: relative;
  cursor: pointer;
}

.img {
  width: 80px;
  height: 80px;
  margin-left: 4px;
  margin-top: 4px;
  margin-bottom: 10px;
}

.nick {
  margin-bottom: 40px;
  cursor: pointer;
}

.img_out_focus {
  width: 88px;
  height: 88px;
  position: absolute;
  top: 0;
  left: 0;
  background: url(data:image/png;base64,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) -91px -183px
    no-repeat;
}

.bottom {
  .link {
    right: 20px;
    bottom: 13px;
    position: absolute;
    color: #666;
  }
}

h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
// a {
//   color: #42b983;
// }
</style>
