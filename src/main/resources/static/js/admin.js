const HOST = "/api/1.0";
// const HOST = "https://www.fastmock.site/mock/b643231b81d6851b3bf8fe14314271d4/femts";
const OPERATION_TYPE = ['创建','更新','删除','下载','上传'];
const DOC_LEVEL = ['私人', '不可编辑', '仅上级可见', '公开'];

const Home = {
    template: `
<div>
    <div class="row">
        <div class="col-xl-3 col-md-6 mb-4">
          <div class="card border-left-primary shadow h-100 py-2">
            <div class="card-body">
              <div class="row no-gutters align-items-center">
                <div class="col mr-2">
                  <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">注册人数</div>
                  <div class="h5 mb-0 font-weight-bold text-gray-800">{{counter.registered}}</div>
                </div>
                <div class="col-auto">
                  <i class="fas fa-calendar fa-2x text-gray-300"></i>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="col-xl-3 col-md-6 mb-4">
          <div class="card border-left-warning shadow h-100 py-2">
            <div class="card-body">
              <div class="row no-gutters align-items-center">
                <div class="col mr-2">
                  <div class="text-xs font-weight-bold text-warning text-uppercase mb-1">激活人数</div>
                  <div class="h5 mb-0 font-weight-bold text-gray-800">{{counter.actived}}</div>
                </div>
                <div class="col-auto">
                  <i class="fas fa-comments fa-2x text-gray-300"></i>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="col-xl-3 col-md-6 mb-4">
          <div class="card border-left-success shadow h-100 py-2">
            <div class="card-body">
              <div class="row no-gutters align-items-center">
                <div class="col mr-2">
                  <div class="text-xs font-weight-bold text-success text-uppercase mb-1">在线人数</div>
                  <div class="h5 mb-0 font-weight-bold text-gray-800">{{counter.online}}</div>
                </div>
                <div class="col-auto">
                  <i class="fas fa-dollar-sign fa-2x text-gray-300"></i>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="col-xl-3 col-md-6 mb-4">
          <div class="card border-left-info shadow h-100 py-2">
            <div class="card-body">
              <div class="row no-gutters align-items-center">
                <div class="col mr-2">
                  <div class="text-xs font-weight-bold text-info text-uppercase mb-1">文档个数</div>
                  <div class="row no-gutters align-items-center">
                    <div class="col-auto">
                      <div class="h5 mb-0 mr-3 font-weight-bold text-gray-800">{{counter.document}}</div>
                    </div>
                  </div>
                </div>
                <div class="col-auto">
                  <i class="fas fa-clipboard-list fa-2x text-gray-300"></i>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
</div>
`,
    data () {
        axios.get(HOST + "/user/count/system")
            .then(res => {
                if (res.data.code === 200) {
                    this.counter = res.data.data;
                } else {
                    this.$message.error(res.data.data);
                }
            }).catch(err => {
                this.$message.error(err);
        });
        return {
            counter: {
                registered: 0,
                actived: 0,
                online: 0,
                document: 0
            }
        }
    }
};

const UserInfo = {
    template: `
<div class="card-info" style="text-align: center">
    <div class="d-none d-sm-inline-block form-inline mr-auto ml-md-3 my-2 my-md-0 mw-100 navbar-search" style="margin: 25px auto !important; box-shadow: 0 0.25rem 0.35rem 0 rgba(58,59,69,.15)!important; width: 25rem;">
        <div class="input-group">
            <input type="text" v-model="username" placeholder="Search for user..." aria-label="Search" aria-describedby="basic-addon2" class="form-control bg-light border-0 small" style="font-weight: 100;"> 
            <div class="input-group-append">
               <button type="button" @click="searchUser" class="btn btn-primary"><i class="fas fa-search fa-sm"></i></button>
            </div>
        </div>
    </div>
    <el-card class="box-card user-card">
        <div slot="header" class="clearfix" style="text-align: initial; font-size: 19px;">
            <span>{{user.username}}</span>
            <div class="d-inline" v-if="not_editable">
                <el-button style="float: right; padding: 3px 0" type="text" @click="not_editable=false"">信息修改</el-button>
            </div>
            <div class="d-inline" v-else>
                <el-button style="float: right; padding: 3px 0" type="text" @click="saveAndPost" :loading="loading">保存</el-button>
            </div>
        </div>
        <el-form label-width="80px" :model="user" :disabled="not_editable">
            <el-form-item label="用户名">
                <el-input v-model="user.username"></el-input>
            </el-form-item>
            <el-form-item label="工号">
                <el-input v-model="user.jobId"></el-input>
            </el-form-item>
            <el-form-item label="邮箱">
                <el-input v-model="user.email"></el-input>
            </el-form-item>
            <el-form-item label="手机">
                <el-input v-model="user.phone"></el-input>
            </el-form-item>
            <div class="d-inline-block">
                <div class="d-inline-block" style="margin-right: 15px;font-size: 14px;color: #606266;">权限级别</div>
                <el-input-number v-model="user.level" :min="0" :max="10" label="权限级别"></el-input-number>
            </div>
            <div class="d-inline-block" style="margin-left: 8.6rem">
                <div class="d-inline-block " style="margin-right: 15px;font-size: 14px;color: #606266;">用户状态</div>
                <el-select v-model="user.state" placeholder="请选择">
                    <el-option
                        v-for="item in user_states"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                        :disabled="item.disabled">
                    </el-option>
                </el-select> 
            </div>
        </el-form>
    </el-card>
    

</div>
`,
    data: function () {
        // 初始用户数据为当前登陆用户
        axios.get(HOST + "/user")
            .then(res => {
                if (res.data.code === 200) {
                    this.user = res.data.data;
                    this.username = res.data.data.username;
                } else {
                    this.$message.error(res.data.data);
                }
            }).catch(err => {
            this.$message.error(err);
        });
        return {
            username: '',
            user: {
                id: null,
                username: '',
                jobId: '',
                email: '',
                phone: '',
                level: 0,
                state: '',
                createAt: '',
                modifiedAt: ''
            },
            not_editable: true,
            loading: false,
            user_states: [
                {
                    value: 'LOCKED',
                    label: '被锁定'
                }, {
                    value: 'INACTIVATED',
                    label: '未激活'
                }, {
                    value: 'GENERAL',
                    label: '普通用户',
                }, {
                    value: 'ADMIN',
                    label: '管理员',
                    disabled: true
                }, {
                    value: 'SUPER_ADMIN',
                    label: '超级管理员',
                    disabled: true
                }
            ]
        };
    },
    methods: {
        searchUser() {
            if (this.$data.username.length < 4) {
                this.$message.error("查询用户名不合法");
                return;
            }
            axios.get(HOST + "/user/" + this.$data.username + "/info")
                .then(res => {
                    if (res.data.code === 200) {
                        this.$data.user = res.data.data;
                    } else {
                        this.$message.error(res.data.data);
                    }
                }).catch(err => {
                    this.$message.error(err);
            });
        },
        saveAndPost() {
            this.$data.loading = true;
            axios.post(HOST + "/user/" + this.$data.user.id, this.$data.user)
                .then(res => {
                    if (res.data.code === 200) {
                        this.$message({
                            message: '保存成功',
                            type: 'success'
                        });
                        this.$data.not_editable = true;
                    } else {
                        this.$message.error(res.data.data);
                    }
                    this.$data.loading = false;
                }).catch(err => {
                    this.$message.error(err);
            });
        }
    }
};

const UserRecord = {
    template: `
<div style="text-align: center">
    <div class="d-none d-sm-inline-block form-inline mr-auto ml-md-3 my-2 my-md-0 mw-100 navbar-search" style="margin: 25px auto !important; box-shadow: 0 0.25rem 0.35rem 0 rgba(58,59,69,.15)!important; width: 25rem;">
        <div class="input-group">
            <input type="text" v-model="username" placeholder="Search for user..." aria-label="Search" aria-describedby="basic-addon2" class="form-control bg-light border-0 small" style="font-weight: 100;"> 
            <div class="input-group-append">
               <button type="button" @click="queryUserHistory()" class="btn btn-primary"><i class="fas fa-search fa-sm"></i></button>
            </div>
        </div>
    </div>
    <el-table
        :data="user_records"
        border
        style="width: 100%">
        <el-table-column
          prop="type"
          label="类型"
          width="100">
            <template slot-scope="scope">
                <el-tag
                  :type="scope.row.type === 0 ? 'primary' : 'success'"
                  disable-transitions>{{scope.row.type === 0 ? '用户' : '文档'}}</el-tag>
            </template>
        </el-table-column>
        <el-table-column
          prop="name"
          label="用户名/文档标题"
          width="400">
        </el-table-column>
        <el-table-column
          prop="operation"
          label="操作类型"
          width="100">
            <template slot-scope="scope">
                <el-tag
                  :type="getOperationTagStyle(scope.row.operation)"
                  disable-transitions>{{OPERATION_TYPE[scope.row.operation]}}</el-tag>
            </template>
        </el-table-column>
        <el-table-column
          prop="time"
          label="日期"
          width="200">
        </el-table-column>
        <el-table-column
          prop="remark"
          label="备注">
        </el-table-column>
    </el-table>
    <div style="margin: 20px auto;"></div>
    <el-pagination
        background
        layout="prev, pager, next"
        :current-page.sync="page.pageNum"
        @current-change="jumpPage"
        :page-count="page.pageCount">
    </el-pagination>
</div>
`,
    data() {
        axios.get(HOST + "/history/user").then(res => {
                if (res.data.code === 200) {
                    this.$data.user_records = res.data.data;
                    this.$data.page.pageCount = res.data.page.pageCount;
                } else {
                    this.$message.error(res.data.data);
                }
            }).catch(err => {
            this.$message.error(err);
        });
        return {
            page: {
                pageNum: 1,
                pageCount: 10
            },
            username_stable: '',
            username: '',
            user_records: ''
        }
    },
    methods: {
        queryUserHistory() {
            if (this.$data.username.length < 4) {
                this.$message.error("查询用户名不合法");
                return;
            }
            // 这样用户在搜索过一次后即使修改了搜索栏的内容也可以保留上一次搜索记录
            this.$data.username_stable = this.$data.username;
            axios.get(HOST + "/history/user/" + this.$data.username)
                .then(res => {
                    if (res.data.code === 200) {
                        this.$data.user_records = res.data.data;
                        this.$data.page.pageCount = res.data.page.pageCount;
                    } else {
                        this.$message.error(res.data.data);
                    }
                }).catch(err => {
                this.$message.error(err);
            });
        },
        getOperationTagStyle(type) {
            switch (type) {
                case 0: return 'success';
                case 1: return 'primary';
                case 2: return 'danger';
                case 3: return 'warning';
                case 4:
                default:
                    return 'info';
            }
        },
        jumpPage() {
            axios.get(HOST + '/history/user/' + this.$data.username_stable + '?pageNum=' + this.$data.page.pageNum)
                .then(res => {
                    if (res.data.code === 200) {
                        this.$data.user_records = res.data.data;
                        this.$data.page.pageCount = res.data.page.pageCount;
                    }
                }).catch(err => {
                this.$message.error(err);
            });
        }
    }
};

const DocInfo = {
    template: `
<div class="card-info" style="text-align: center;">
    <div class="d-none d-sm-inline-block form-inline mr-auto ml-md-3 my-2 my-md-0 mw-100 navbar-search" style="margin: 25px auto !important; box-shadow: 0 0.25rem 0.35rem 0 rgba(58,59,69,.15)!important; width: 39rem;">
        <div class="input-group">
            <div class="input-stable-append">
               {{param.creator}}
            </div>
            <el-autocomplete
                type="text" 
                v-model="param.title" 
                placeholder="Search for documents..." 
                aria-label="Search" aria-describedby="basic-addon2" 
                class="form-control bg-light border-0"
                :fetch-suggestions="searchSameTitleAsync"
                @select="handleTitleSelect"
                >
                <template slot-scope="{ item }">
                    <div class="name d-inline-block">{{ item.title }}</div>
                    <div class="d-inline-block float-right">
                        <el-tag size="small">{{item.creator}}</el-tag>
                    </div>
                </template>
            </el-autocomplete>
            <div class="input-group-append">
               <button type="button" @click="searchDoc" class="btn btn-primary"><i class="fas fa-search fa-sm"></i></button>
            </div>
        </div>
    </div>
    <el-card class="box-card" v-if="visible.card">
        <div slot="header" class="clearfix" style="text-align: initial; font-size: 19px;">
            <span>{{document.title}}</span>
            <div class="d-inline-block" style="margin-left: 10px;">
                <el-tag size="medium">{{document.format}}</el-tag> 
                <el-tag size="medium" :type="filterLevelTag(document.level)">{{DOC_LEVEL[document.level]}}</el-tag>           
            </div>
            <div style="float: right">
                <div class="d-inline">
                    <el-button style="margin-right: 12px; padding: 7px 8px" @click="visible.content=true">显示内容</el-button>
                    <el-dialog title="文档内容" :visible.sync="visible.content">
                        <div v-html="document.content"></div>
                    </el-dialog>
                </div>
                <div class="d-inline">
                    <el-button slot="reference" style="padding: 7px 8px" type="danger" @click="deleteDoc">删除</el-button>
                </div>
            </div>
        </div>
        <el-form label-width="130px" :model="document" disabled="true">
            <el-form-item label="文档ID">
                <el-input v-model="document.id"></el-input>
            </el-form-item>
            <el-form-item label="文件索引">
                <el-input v-model="document.fileId"></el-input>
            </el-form-item>
            <el-form-item label="标题">
                <el-input v-model="document.title"></el-input>
            </el-form-item>
            <el-form-item label="创建人">
                <el-input v-model="document.creator"></el-input>
            </el-form-item>
            <el-form-item label="创建时间">
                <el-input v-model="document.createdAt"></el-input>
            </el-form-item>
            <el-form-item label="最近一次修改人">
                <el-input v-model="document.editor"></el-input>
            </el-form-item>
            <el-form-item label="最近一次修改时间">
                <el-input v-model="document.editedAt"></el-input>
            </el-form-item>
            
        </el-form> 
    </el-card>
    
</div>
`,
    data() {
        return {
            visible: {
                card: false,
                delBtn: false,
                content: false
            },
            param: {
                id: '',
                title: '',
                creator: ''
            },
            document: {}
        }
    },
    methods: {
        searchSameTitleAsync(queryString, cb) {
            if (queryString.length > 1) {
                axios.get(HOST + "/doc/list/title?title=" + queryString)
                    .then(res => {
                        if (res.data.code === 200) {
                            cb(res.data.data);
                        } else {
                            this.$message.error(res.data.data);
                        }
                    }).catch(err => {
                    this.$message.error(err);
                });
            } else {
                cb([]);
            }
        },
        handleTitleSelect(item) {
            this.$data.param.creator = item.creator;
            this.$data.param.title = item.title;
        },
        searchDoc() {
            this.$data.visible.card = true;
            axios.get(HOST + "/doc/info/name?id=" + this.$data.param.id)
                .then(res => {
                    if (res.data.code === 200) {
                        this.$data.document = res.data.data;
                    } else {
                        this.$message.error(res.data.data);
                    }
                }).catch(err => {
                    this.$message.error(err);
            });
        },
        filterLevelTag(level) {
            switch (level) {
                case 0: return 'warning';
                case 1: return 'danger';
                case 2: return 'info';
                case 3: return 'success';
                default: return '';
            }
        },
        showContent() {

        },
        deleteDoc() {
            this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'danger'
            }).then(() => {
                this.$data.visible.delBtn = false;
                axios.delete(HOST + '/doc/' + this.$data.param.id)
                    .then(res => {
                        if (res.data.code === 200) {
                            this.$message({
                                type: 'success',
                                message: '删除成功'
                            });
                            this.$data.visible.card = false;
                        } else {
                            this.$message.error(res.data.data);
                        }
                    }).catch(err => {
                    this.$message.error(err);
                });
            }).catch(() => {});
        }
    }
};

const DocRecord = {
    template: `
<div style="text-align: center">
    <div class="d-none d-sm-inline-block form-inline mr-auto ml-md-3 my-2 my-md-0 mw-100 navbar-search" style="margin: 25px auto !important; box-shadow: 0 0.25rem 0.35rem 0 rgba(58,59,69,.15)!important; width: 39rem;">
        <div class="input-group">
            <div class="input-stable-append">
               {{param.creator}}
            </div>
            <el-autocomplete
                type="text" 
                v-model="param.title" 
                placeholder="Search for documents..." 
                aria-label="Search" aria-describedby="basic-addon2" 
                class="form-control bg-light border-0"
                :fetch-suggestions="searchSameTitleAsync"
                @select="handleTitleSelect"
                >
                <template slot-scope="{ item }">
                    <div class="name d-inline-block">{{item.title}}</div>
                    <div class="d-inline-block float-right">
                        <el-tag size="small">{{item.creator}}</el-tag>
                    </div>
                </template>
            </el-autocomplete>
            <div class="input-group-append">
               <button type="button" @click="searchDocHistory" class="btn btn-primary"><i class="fas fa-search fa-sm"></i></button>
            </div>
        </div>
    </div>
    
    <el-table
        :data="doc_records"
        border
        style="width: 100%">
        <el-table-column
          prop="operator"
          label="操作用户"
          width="180">
        </el-table-column>
        <el-table-column
          prop="operation"
          label="操作类型"
          width="100">
            <template slot-scope="scope">
                <el-tag
                  :type="getOperationTagStyle(scope.row.operation)"
                  disable-transitions>{{OPERATION_TYPE[scope.row.operation]}}</el-tag>
            </template>
        </el-table-column>
        <el-table-column
          prop="time"
          label="操作时间"
          width="180">
        </el-table-column>
        <el-table-column
          prop="remark"
          label="备注">
        </el-table-column>
    </el-table>
    <div style="margin: 20px auto;"></div>
    <el-pagination
        background
        layout="prev, pager, next"
        :current-page.sync="page.pageNum"
        @current-change="jumpPage"
        :page-count="page.pageCount">
    </el-pagination>
</div>
`,
    data() {
        return {
            param: {
                id: '',
                creator: '',
                title: ''
            },
            page: {
                pageNum: '',
                pageCount: ''
            },
            doc_records: []
        }
    },
    methods: {
        searchSameTitleAsync(queryString, cb) {
            if (queryString.length > 1) {
                axios.get(HOST + "/doc/list/title?title=" + queryString)
                    .then(res => {
                        if (res.data.code === 200) {
                            cb(res.data.data);
                        } else {
                            this.$message.error(res.data.data);
                        }
                    }).catch(err => {
                    this.$message.error(err);
                });
            } else {
                cb([]);
            }
        },
        handleTitleSelect(item) {
            this.$data.param.id = item.id;
            this.$data.param.creator = item.creator;
            this.$data.param.title = item.title;
        },
        searchDocHistory() {
            if (this.$data.param.id === null) {
                return;
            }
            axios.get(HOST + "/history/doc/" + this.$data.param.id)
                .then(res => {
                    if (res.data.code === 200) {
                        this.$data.doc_records = res.data.data;
                        this.$data.page.pageCount = res.data.page.pageCount;
                    }
                }).catch(err => {
                    this.$message.error(err);
            });
        },
        getOperationTagStyle(type) {
            switch (type) {
                case 0: return 'success';
                case 1: return 'primary';
                case 2: return 'danger';
                case 3: return 'warning';
                case 4:
                default:
                    return 'info';
            }
        },
        jumpPage() {
            axios.get(HOST + '/history/doc/' + this.$data.param.id + '?pageNum=' + this.$data.page.pageNum)
                .then(res => {
                    if (res.data.code === 200) {
                        this.$data.user_records = res.data.data;
                        this.$data.page.pageCount = res.data.page.pageCount;
                    }
                }).catch(err => {
                this.$message.error(err);
            });
        }
    }
};

const DocTrace = {
    template: `
<div>
    <div class="upload" style="text-align: center; padding: 60px 0;">
<!--        <el-upload-->
<!--        class="upload-demo" drag action="/api/1.0/file/trace" multiple>-->
        <el-upload
            class="upload-demo" drag action=""
            :on-change="handleFileChange"
            :http-request="uploadTraceFile"
            :on-preview="uploadPreview"
            :on-success="uploadSuccess">
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <div class="el-upload__tip" slot="tip">只能上传doc/docx/pdf/ofd/rtf/txt文件，且大小不超过2mb</div>
        </el-upload>
    </div>
    <el-dialog title="操作记录" :visible.sync="visible.record_dialog">
         <el-table
            :data="last_record"
            border
            style="width: 100%">
            <el-table-column
              prop="operator"
              label="操作用户"
              width="180">
            </el-table-column>
            <el-table-column
              prop="operation"
              label="操作类型"
              width="100">
                <template slot-scope="scope">
                    <el-tag
                      :type="getOperationTagStyle(scope.row.operation)"
                      disable-transitions>{{OPERATION_TYPE[scope.row.operation]}}</el-tag>
                </template>
            </el-table-column>
            <el-table-column
              prop="time"
              label="操作时间"
              width="180">
            </el-table-column>
            <el-table-column
              prop="remark"
              label="备注">
            </el-table-column>
        </el-table>
        <div style="margin: 20px auto;"></div>
        <div style="text-align: center">
            <el-pagination
                background
                layout="prev, pager, next"
                :current-page.sync="page.pageNum"
                @current-change="jumpPage"
                :page-count="page.pageCount">
            </el-pagination>
        </div>  
    </el-dialog>
</div>
`,
    data() {
        return {
            page: {
                pageCount: '',
                pageNum: ''
            },
            visible: {
                record_dialog: false
            },
            last_record: [],
            records_map: new Map(),
            file: {},
            id: null
        }
    },
    methods: {
        getOperationTagStyle(type) {
            switch (type) {
                case 0: return 'success';
                case 1: return 'primary';
                case 2: return 'danger';
                case 3: return 'warning';
                case 4:
                default:
                    return 'info';
            }
        },
        handleFileChange(file) {
            this.$data.file = file.raw;
        },
        uploadTraceFile(param) {
            let form = new FormData();
            form.append('mulFile', this.$data.file);
            axios.post(HOST + '/file/trace', form)
                .then(res => {
                    if (res.data.code === 200) {
                        if (res.data.data.length > 0) {
                            // 记录文档id
                            this.$data.id = res.data.data[0].operatedId;
                        }
                        this.$data.records_map.set(param.file.uid, res.data.data);
                        this.$data.last_record = res.data.data;
                        this.$data.visible.record_dialog = true;
                        this.$data.page = res.data.page;
                    } else {
                        this.$message.error(res.data.data);
                        this.$data.visible.record_dialog = false;
                    }
                }).catch(err => {
                this.$message.error(err);
            });
        },
        uploadPreview(file) {
            console.log(file.uid);
            this.$data.last_record = this.$data.records_map.get(file.uid);
            this.$data.visible.record_dialog = true;
        },
        uploadSuccess(response, file, fileList) {
            // this.$data.records_map.set(file.uid, record);
            // this.$data.last_record = record;
            // this.$data.visible.record_dialog = true;
        },
        jumpPage() {
            axios.get(HOST + '/history/doc/' + this.$data.id + '?pageNum=' + this.$data.page.pageNum)
                .then(res => {
                    if (res.data.code === 200) {
                        this.$data.last_record = res.data.data;
                        this.$data.page.pageCount = res.data.page.pageCount;
                    }
                }).catch(err => {
                this.$message.error(err);
            });
        }
    }
};


