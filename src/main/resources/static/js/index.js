const HOST = 'https://www.fastmock.site/mock/b643231b81d6851b3bf8fe14314271d4/femts';
// const HOST = 'https://www.fastmock.site/mock/b643231b81d6851b3bf8fe14314271d4/femts/api/1.0';
let wait_time = 10;
// 编辑器配置
const options = {
    selector: '#editor_id',
    branding: false,
    statusbar: false,
    height: 380,
    menu: {
        file: {title: '文件', items: 'newdocument'},
        edit: {title: '编辑', items: 'undo redo | cut copy paste pastetext | selectall'},
        insert: {title: '插入', items: 'link media | template hr'},
        view: {title: '查看', items: 'visualaid'},
        format: {title: '格式', items: 'bold italic underline strikethrough superscript subscript | formats | removeformat'},
        table: {title: '表格', items: 'inserttable tableprops deletetable | cell row column'},
        tools: {title: '工具', items: 'spellchecker code'}
    }
};
const SECURITY_LEVEL = ["PRIVATE", "UNEDITABLE", "CONFIDENTIAL", "PUBLIC"];
const FILE_TYPE = ["WORD2003", "WORD2007", "TXT", "PDF", "OFD", "RTF", "CUSTOM"];
tinymce.init(options);

let $app = new Vue({
    el: '#app',
    data () {
        return {
            file: {},
            file_type: 1,
            show_download_dialog: false,
            show_alert: false,
            show_setting: false,
            show_editor: false,
            setting_select: 1,
            loader: {
                save_btn: false
            },
            props: {
                label: 'name',
                children: 'children',
                isLeaf: 'leaf'
            },
            node: [],
            resolve: [],
            doc: {
                id: 1,
                directoryId: 2,
                title: "测试文档标题English&*( $_)",
                creator: "刘昊楠05",
                created: '2019/07/12 12:56',
                editor: "张三72",
                edited: '2020/01/02 05:23',
                content: `<div style="width:595.0pt;margin-bottom:56.0pt;margin-top:85.0pt;margin-left:85.0pt;margin-right:56.0pt;"><p style="text-align:center;"><span style="font-family:'楷体';font-size:16.0pt;font-weight:bold;">&#26159;&#30701;&#21457;&#25151;&#39030;&#19978;&#21453;&#20498;&#26159;&#26041;&#24335;</span></p><p>&#20799;&#21834;<span style="font-family:'宋体';font-size:16.0pt;">&#25746;&#26086;</span><span style="font-family:'宋体';font-size:16.0pt;font-style:italic;">&#38463;</span><span style="font-style:italic;">&#26031;&#39039;a</span>F&#21380;s<span style="text-decoration:underline;">d&#38750;</span>dss</p><p>&#25746;<span style="font-weight:bold;">&#26086;&#38463;&#26031;</span>&#39039;adsaa<span id="_GoBack"/></p><p>   &#21714;&#21714;&#21714;</p><p>&#21457;&#29983;&#22823;&#24133;</p></div>`
            }
        }
    },
    methods: {
        close_all: function () {
            this.$data.show_alert = false;
            this.$data.show_setting = false;
        },
        loadNodes(node, resolve) {
            // console.log(node);
            if (node.level === 0) {
                this.$data.node = node;
                this.resolve = resolve;
                let treeData;
                axios.get(HOST + "/directory/list/0")
                    .then(res => {
                        if (res.data.code === 200) {
                            return resolve(res.data.data);
                        } else {
                            this.$message.error(res.data.data);
                        }
                    }).catch(err => {
                    this.$message.error(err);
                });
            } else {
                // let children = [];
                axios.get(HOST + "/directory/list/" + node.data.id)
                    .then(res => {
                        if (res.data.code === 200) {
                            return resolve(res.data.data);
                        } else {
                            this.$message.error(res.data.data);
                        }
                    }).catch(err => {this.$message.error(err)});
                // return resolve([
                //     {
                //         id: 12,
                //         name: 'hhh',
                //         children: []
                //     }
                // ]);
            }
        },
        // refreshTree(node) {
        //     this.node.childrenNodes = [];
        //     this.loadNodes(this.$data.node, this.$data.resolve);
        // },
        modifyDirectory(node, data) {
            this.$prompt('请输入文件夹名称', '编辑文件夹', {
                confirmButtonText: '更新',
                cancelButtonText: '取消',
                inputPattern: /^[\u0391-\uFFE5A-Za-z]{2,15}$/,
                inputErrorMessage: '名称中不能包含非中文或英文的字符'
            }).then(({ value }) => {
                console.log('data: ' + data);
                console.log('node: ' + node);
                axios.post(HOST + "/doc/directory/edit/" + data.id + "?name=" + value)
                    .then(res => {
                        if (res.data.code === 200) {
                            // 如果返回200，则返回的数据为新目录的id
                            // console.log('-------------------');
                            // console.log(this.node);
                            this.$set(data, 'name', value);
                            // console.log(this.node);
                            // console.log('-------------------');
                            this.$message({
                                type: 'success',
                                message: '更新成功'
                            });
                        } else {
                            this.$message.error(res.data.data);
                        }
                    }).catch(err => {
                    this.$message.error(err);
                })
            }).catch(() => {});
        },
        createDirectory(node, data) {
            this.$prompt('请输入文件夹名称', '创建文件夹', {
                confirmButtonText: '创建',
                cancelButtonText: '取消',
                inputPattern: /^[\u0391-\uFFE5A-Za-z]+$/,
                inputErrorMessage: '名称中不能包含非中文或英文的字符'
            }).then(({ value }) => {
                // 这里是向后台提交的数据，非vue绑定的数据
                const postData = {
                    id: node.id,
                    name: value,
                    children: [],
                    visible: false
                };
                axios.post(HOST + "/doc/directory/append/" + data.id, postData)
                    .then(res => {
                        if (res.data.code === 200) {
                            // 如果返回200，则返回的数据为新目录的id
                            let id = res.data.data;
                            const newChild = {
                                'id': id,
                                'name': value,
                                'leaf': false,
                                'children': []
                            };
                            data.children.push(newChild);
                            this.$message({
                                type: 'success',
                                message: '创建成功'
                            });
                        } else {
                            this.$message.error(res.data.data);
                        }
                    }).catch(err => {
                    // 生产环境中需要改为‘异常错误信息’
                    this.$message.error(err);
                })
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '取消输入'
                });
            });
        },
        createManuscript(node, data) {

        },
        edit() {
            this.$data.show_editor = true;

            setTimeout(function() {
                if ($('#editor').is(":visible")) {
                    tinymce.init(options);
                } else {
                    this.$message.error("编辑器加载失败，请重新点击'编辑'按钮");
                    // 每次等待时间延长50毫秒，保证用户不会一直加载失败
                    wait_time += 50;
                }
            }, wait_time);
        },
        download() {
            this.$data.show_download_dialog = false;
        },
        saveDoc() {
            let doc = this.$data.doc;
            let loader = this.$data.loader;
            loader.save_btn = true;

            let html = tinyMCE.activeEditor.getContent();
            doc.content = html;
            let document = {
                title: doc.title,
                content: html,
                directoryId: doc.directoryId,
                level: SECURITY_LEVEL[this.$data.setting_select]
            };

            axios.post(HOST + "/doc/:id", document)
                .then(res => {
                    console.log(res.data);
                    loader.save_btn = false;
                    if (res.data.code === 200) {
                        this.$message({
                            message: '保存成功',
                            type: 'success'
                        });
                        this.$data.show_editor = false;
                    } else {
                        this.$message.error(res.data.data);
                    }
                }).catch(err => {
                loader.save_btn = false;
                this.$message.error(err);
            })
        },
        selectSecurityLevel(command) {
            console.log(command);
            this.$data.setting_select = command;
        },
        handleUploadChange(file) {
            this.$data.file = file.raw;
        },
        uploadFile(param) {
            // let form = new FormData();
            // form.append('mulFile', this.$data.file);
            // axios.post('/test/upload', form)
            //     .then(res => {
            //         console.log(res);
            //     }).catch(err => {
            //     this.$message.error(err);
            // });
        }
    }
});