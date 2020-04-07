// const HOST = 'https://www.fastmock.site/mock/b643231b81d6851b3bf8fe14314271d4/femts';
const HOST = '/api/1.0';
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
        axios.get(HOST + "/user/")
            .then(res => {
                if (res.data.code === 200) {
                    this.user = res.data.data;
                } else {
                    this.$message.error(res.data.data);
                }
            }).catch(err => { this.$message.error(err); });

        return {
            user: {},
            file: {},
            file_type: 0,
            show_upload: false,
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
            doc: {},
            doc_backup: {}
        }
    },
    methods: {
        close_all: function () {
            this.$data.show_alert = false;
            this.$data.show_setting = false;
        },
        checkIfAdmin() {
            return false;
        },
        loadNodes(node, resolve) {
            console.log(node);
            if (node.level === 0) {
                this.node = node;
                this.resolve = resolve;
                let treeData;
                axios.get(HOST + "/doc/directory/list/0")
                    .then(res => {
                        if (res.data.code === 200) {
                            return resolve(res.data.data);
                        } else {
                            this.$message.error(res.data.data);
                            return resolve([]);
                        }
                    }).catch(err => {
                    this.$message.error(err);
                    return resolve([]);
                });
            } else {
                // let children = [];
                axios.get(HOST + "/doc/directory/list/" + node.data.id)
                    .then(res => {
                        if (res.data.code === 200) {
                            return resolve(res.data.data);
                        } else {
                            this.$message.error(res.data.data);
                            return resolve([]);
                        }
                    }).catch(err => {
                        this.$message.error(err);
                        return resolve([]);
                    });
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
            console.log(data);
            let crtData = data;
            this.$prompt('请输入文档标题', '创建文档', {
                confirmButtonText: '创建',
                cancelButtonText: '取消',
                inputPattern: /^[\u0391-\uFFE5A-Za-z_]{3,20}$/,
                inputErrorMessage: '文档标题应为3-20位的中文或英文'
            }).then(({ value }) => {
                axios.get(HOST + "/doc/detect?title=" + value)
                    .then(res => {
                        if (res.data.code === 200) {
                            let docData = {
                                title: value,
                                content: '',
                                directoryId: data.id,
                                level: 'PRIVATE'
                            };
                            console.log(docData);
                            axios.post(HOST + "/doc/post", docData)
                                .then(result => {
                                    if (result.data.code === 200) {
                                        this.doc = result.data.data;
                                        const newChild = {
                                            'id': result.data.data.id,
                                            'name': value,
                                            'leaf': true,
                                            'children': []
                                        };
                                        crtData.children.push(newChild);
                                        // this.refreshDirectoryTree();
                                        this.show_upload = true;
                                        this.$message({
                                            message: '创建成功',
                                            type: 'success'
                                        });
                                    } else {
                                        this.$message.error(result.data.data);
                                    }
                                });
                        } else {
                            this.$message.error(res.data.data);
                        }
                    }).catch(err => { this.$message.error(err); });
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '取消输入'
                });
            });
        },
        refreshDirectoryTree() {
            this.node.childNodes = [];
            this.loadNodes(this.node, this.resolve);
        },
        edit() {
            axios.get(HOST + "/doc/check/edit/" + this.doc.id)
                .then(res => {
                    if (res.data.code === 200) {
                        // 备份原文档数据
                        this.doc_backup = this.doc;

                        this.show_upload = false;
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
                    } else {
                        this.$message.error(res.data.data);
                    }
                }).catch(err => { this.$message.error(err); });
        },
        download() {
            // 关闭弹窗
            this.show_download_dialog = false;
            window.location.href = HOST + "/file/download/" + this.doc.id;
        },
        cancelSave() {
            this.doc = this.doc_backup;
            this.show_upload = false;
            this.show_editor = false;
        },
        saveDoc() {
            let doc = this.$data.doc;
            let loader = this.$data.loader;
            loader.save_btn = true;

            let html = tinyMCE.activeEditor.getContent();
            doc.content = html;
            let document = {
                id: doc.id,
                title: doc.title,
                content: html,
                directoryId: doc.directoryId,
                level: SECURITY_LEVEL[this.$data.setting_select]
            };
            axios.post(HOST + "/doc/post", document)
                .then(res => {
                    // console.log(res.data);
                    loader.save_btn = false;
                    if (res.data.code === 200) {
                        this.refreshDirectoryTree();
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
            });
        },
        selectSecurityLevel(command) {
            console.log(command);
            this.$data.setting_select = command;
        },
        handleUploadChange(file) {
            this.$data.file = file.raw;
        },
        uploadFile(param) {
            let form = new FormData();
            form.append('mulFile', this.$data.file);
            form.append('title', this.doc.title);
            form.append('docId', this.doc.id);
            axios.post(HOST + '/file/upload/' + this.doc.directoryId
                + "?level=" + SECURITY_LEVEL[this.doc.level], form)
                .then(res => {
                    if (res.data.code === 200) {
                        this.$data.doc = res.data.data;
                        this.show_upload = false;
                        this.show_editor = false;
                    } else {
                        this.$message.error(res.data.data);
                    }
                }).catch(err => {
                this.$message.error(err);
            });
        },
        showDoc() {
            let data = this.$refs.directoryTree.getCurrentNode();
            if (!data.leaf) {
                return;
            }
            axios.get(HOST + "/doc/" + data.id)
                .then(res => {
                    if (res.data.code === 200) {
                        this.doc = res.data.data;
                        this.show_upload = false;
                        this.show_editor = false;
                    } else {
                        this.$message.error(res.data.data);
                    }
            }).catch(err => { this.$message.error(err)} );
        }
    }
});