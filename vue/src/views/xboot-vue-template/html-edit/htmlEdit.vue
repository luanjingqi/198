<template>
  <div class="editor">
    <Card>
      <p slot="title">
        富文本编辑 -
        <a href="http://www.wangeditor.com/" target="_blank">wangEditor</a>&nbsp;&nbsp;&nbsp;
        其他编辑器推荐：
        <a
          href="https://github.com/notadd/neditor"
          target="_blank"
        >neditor</a>
      </p>
      <Form ref="form" :label-width="70">
        <Form-item label="内容详情">
          <editor id="editor" v-model="data" ref="editor"></editor>
        </Form-item>
        <Form-item label="说明：">
          使用开源wangEditor编辑器，已配置好图片上传、表情包、编辑HTML代码、全屏预览、XSS攻击过滤。
          <br />当然你可以根据官方文档自行配置使用，由于wangEditor轻量简洁美观等特点这里没有选用neditor。
        </Form-item>
        <Alert type="warning" show-icon style="width:60%">现已组件化封装 使用说明见下方</Alert>
        <h3 class="article">基础用法</h3>由于组件特殊性，使用
        <code>v-model</code> 仅能实现数据的
        <b>单向</b> 绑定，数据的回显或设值需手动调用
        <code>setData()</code>方法，同时使用两个及以上该组件时，需设定不同的id值加以区分。
        <h3 class="article">props</h3>
        <Table :columns="columns1" :data="data1" border size="small" width="800"></Table>
        <h3 class="article">events</h3>
        <Table :columns="columns2" :data="data2" border size="small" width="800"></Table>
        <h3 class="article">methods</h3>
        <Table :columns="columns3" :data="data3" border size="small" width="800"></Table>
      </Form>
    </Card>
  </div>
</template>

<script>
import editor from "@/views/my-components/xboot/editor";
export default {
  name: "html-edit",
  components: {
    editor
  },
  data() {
    return {
      data:
        '<img src="http://img.t.sinajs.cn/t4/appstyle/expression/ext/normal/a1/2018new_doge02_org.png" alt="[doge]">',
      columns1: [
        {
          title: "属性",
          key: "name",
          width: 130
        },
        {
          title: "说明",
          key: "desc"
        },
        {
          title: "类型",
          key: "type",
          width: 130
        },
        {
          title: "默认值",
          key: "value"
        }
      ],
      data1: [
        {
          name: "value",
          desc: "绑定的值，可使用 v-model 双向绑定",
          type: "String",
          value: "空"
        },
        {
          name: "id",
          desc:
            "富文本的id值，用于绑定富文本编辑器，当同时使用两个及以上该组件时，需设定不同的id值加以区分",
          type: "String",
          value: "editor"
        },
        {
          name: "openXss",
          desc:
            "是否打开XSS过滤，注意！开启后将默认过滤掉样式，请自行添加白名单",
          type: "Boolean",
          value: "false"
        }
      ],
      columns2: [
        {
          title: "事件名",
          key: "name",
          width: 150
        },
        {
          title: "类型",
          key: "type"
        },
        {
          title: "返回值",
          key: "value"
        }
      ],
      data2: [
        {
          name: "on-change",
          type: "返回富文本编辑器内容",
          value: "value（富文本编辑器内容）"
        }
      ],
      columns3: [
        {
          title: "方法名",
          key: "name",
          width: 150
        },
        {
          title: "说明",
          key: "type",
          width: 300
        },
        {
          title: "参数",
          key: "value"
        }
      ],
      data3: [
        {
          name: "setData",
          type: "设置富文本编辑器内容（回显使用）",
          value: "你要传入的内容，示例 setData(data)"
        }
      ]
    };
  },
  methods: {
    init() {
      this.$refs.editor.setData(this.data);
    }
  },
  mounted() {
    this.init();
  }
};
</script>
<style lang="less">
.article {
  font-size: 16px;
  font-weight: 400;
  margin: 12px 0;
}
code {
  display: inline-block;
  background: #f7f7f7;
  font-family: Consolas, Monaco, Andale Mono, Ubuntu Mono, monospace;
  margin: 0 3px;
  padding: 1px 5px;
  border-radius: 3px;
  color: #666;
  border: 1px solid #eee;
}
</style>