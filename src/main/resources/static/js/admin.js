// const HOST = "https://www.fastmock.site/mock/b643231b81d6851b3bf8fe14314271d4/femts/api/1.0";
const HOST = "https://www.fastmock.site/mock/b643231b81d6851b3bf8fe14314271d4/femts";

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
<div class="user-info" style="text-align: center">
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
    template: `<div>fff</div>`
};

const DocInfo = {
    template: `<div>bbb</div>`
};
const DocRecord = {
    template: `<div>ccc</div>`
};
const DocTrace = {
    template: `<div>ddd</div>`
};


