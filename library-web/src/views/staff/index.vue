<template>
  <div class="staff-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="姓名/编号/用户名/手机号"
            clearable
            style="width: 220px"
          />
        </el-form-item>
        <el-form-item label="所属部门">
          <el-select v-model="searchForm.departmentId" placeholder="请选择部门" clearable style="width: 180px">
            <el-option
              v-for="item in departmentList"
              :key="item.id"
              :label="item.deptName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">
            搜索
          </el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 人员列表 -->
    <el-card class="table-card">
      <div slot="header" class="card-header">
        <span>内部人员列表</span>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">
          新增人员
        </el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        style="width: 100%"
      >
        <el-table-column type="index" width="50" align="center" />
        <el-table-column prop="staffName" label="姓名" width="100" />
        <el-table-column prop="staffCode" label="人员编号" width="100" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="departmentName" label="所属部门" width="120" />
        <el-table-column prop="position" label="职位" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.gender === 1" size="small" type="primary">男</el-tag>
            <el-tag v-else-if="scope.row.gender === 2" size="small" type="danger">女</el-tag>
            <el-tag v-else size="small">未知</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="entryDate" label="入职日期" width="110" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="220" fixed="right">
          <template slot-scope="scope">
            <div class="operation-buttons">
              <el-button type="primary" size="mini" @click="handleEdit(scope.row)">
                编辑
              </el-button>
              <el-button type="info" size="mini" @click="handleViewPermission(scope.row)">
                查看权限
              </el-button>
              <el-button type="danger" size="mini" @click="handleDelete(scope.row)">
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        :current-page="page"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="550px"
    >
      <el-form
        ref="form"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="staffName">
              <el-input v-model="form.staffName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="人员编号" prop="staffCode">
              <el-input v-model="form.staffCode" placeholder="如：S-001" :disabled="!!form.id" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="用于登录" :disabled="!!form.id" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="密码" prop="password" v-if="!form.id">
              <el-input v-model="form.password" type="password" placeholder="默认123456" show-password />
            </el-form-item>
            <el-form-item label="新密码" v-if="form.id">
              <el-input v-model="form.password" type="password" placeholder="不修改请留空" show-password />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属部门" prop="departmentId">
              <el-select v-model="form.departmentId" placeholder="请选择部门" style="width: 100%">
                <el-option
                  v-for="item in departmentList"
                  :key="item.id"
                  :label="item.deptName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位" prop="position">
              <el-input v-model="form.position" placeholder="请输入职位" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
                <el-radio :label="0">未知</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入职日期" prop="entryDate">
              <el-date-picker
                v-model="form.entryDate"
                type="date"
                placeholder="选择日期"
                value-format="yyyy-MM-dd"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </div>
    </el-dialog>

    <!-- 权限查看对话框 -->
    <el-dialog
      title="查看权限"
      :visible.sync="viewPermissionDialogVisible"
      width="600px"
    >
      <div v-if="staffPermissions.length > 0" class="permission-container">
        <div class="permission-section">
          <h4>菜单权限</h4>
          <div class="permission-tags">
            <el-tag
              v-for="perm in menuPermissions"
              :key="perm"
              type="primary"
              size="medium"
              style="margin: 5px"
            >
              {{ getPermissionName(perm) }}
            </el-tag>
            <span v-if="menuPermissions.length === 0" class="no-permission">无</span>
          </div>
        </div>
        <div class="permission-section">
          <h4>按钮/操作权限</h4>
          <div class="permission-tags">
            <el-tag
              v-for="perm in buttonPermissions"
              :key="perm"
              type="success"
              size="medium"
              style="margin: 5px"
            >
              {{ getPermissionName(perm) }}
            </el-tag>
            <span v-if="buttonPermissions.length === 0" class="no-permission">无</span>
          </div>
        </div>
      </div>
      <div v-else class="empty-text">
        暂无权限
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getStaffPage,
  addStaff,
  updateStaff,
  deleteStaff,
  updateStaffStatus,
  getStaffPermissions
} from '@/api/staff'
import { getDepartmentList } from '@/api/department'

export default {
  name: 'StaffManagement',
  data() {
    return {
      loading: false,
      submitLoading: false,
      page: 1,
      size: 10,
      total: 0,
      searchForm: {
        keyword: '',
        departmentId: null
      },
      tableData: [],
      departmentList: [],
      dialogVisible: false,
      dialogTitle: '新增人员',
      form: {
        id: null,
        staffName: '',
        staffCode: '',
        username: '',
        password: '',
        departmentId: null,
        position: '',
        phone: '',
        email: '',
        gender: 0,
        entryDate: '',
        status: 1
      },
      rules: {
        staffName: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        staffCode: [
          { required: true, message: '请输入人员编号', trigger: 'blur' }
        ],
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        departmentId: [
          { required: true, message: '请选择所属部门', trigger: 'change' }
        ]
      },
      viewPermissionDialogVisible: false,
      staffPermissions: [],
      permissionNameMap: {
        'dashboard': '数据概览',
        'book': '图书管理',
        'book:add': '图书新增',
        'book:edit': '图书编辑',
        'book:delete': '图书删除',
        'book:export': '图书导出',
        'category': '分类管理',
        'category:add': '分类新增',
        'category:edit': '分类编辑',
        'category:delete': '分类删除',
        'borrow': '借阅管理',
        'borrow:add': '借阅新增',
        'borrow:edit': '借阅编辑',
        'borrow:delete': '借阅删除',
        'borrow:return': '归还图书',
        'user': '用户管理',
        'user:add': '用户新增',
        'user:edit': '用户编辑',
        'user:delete': '用户删除',
        'user:status': '用户状态',
        'department': '部门管理',
        'department:add': '部门新增',
        'department:edit': '部门编辑',
        'department:delete': '部门删除',
        'department:status': '部门状态',
        'staff': '内部人员',
        'staff:add': '人员新增',
        'staff:edit': '人员编辑',
        'staff:delete': '人员删除',
        'staff:status': '人员状态',
        'staff:permission': '权限设置'
      }
    }
  },
  computed: {
    menuPermissions() {
      return this.staffPermissions.filter(perm => !perm.includes(':'))
    },
    buttonPermissions() {
      return this.staffPermissions.filter(perm => perm.includes(':'))
    }
  },
  created() {
    this.fetchData()
    this.fetchDepartmentList()
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const res = await getStaffPage({
          page: this.page,
          size: this.size,
          keyword: this.searchForm.keyword,
          departmentId: this.searchForm.departmentId
        })
        if (res.code === 200) {
          this.tableData = res.data.records
          this.total = res.data.total
        }
      } catch (error) {
        console.error('获取人员列表失败:', error)
      } finally {
        this.loading = false
      }
    },
    async fetchDepartmentList() {
      try {
        const res = await getDepartmentList()
        if (res.code === 200) {
          this.departmentList = res.data
        }
      } catch (error) {
        console.error('获取部门列表失败:', error)
      }
    },
    handleSearch() {
      this.page = 1
      this.fetchData()
    },
    handleReset() {
      this.searchForm.keyword = ''
      this.searchForm.departmentId = null
      this.page = 1
      this.fetchData()
    },
    handleSizeChange(val) {
      this.size = val
      this.fetchData()
    },
    handleCurrentChange(val) {
      this.page = val
      this.fetchData()
    },
    handleAdd() {
      this.dialogTitle = '新增人员'
      this.form = {
        id: null,
        staffName: '',
        staffCode: '',
        username: '',
        password: '',
        departmentId: null,
        position: '',
        phone: '',
        email: '',
        gender: 0,
        entryDate: '',
        status: 1
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑人员'
      this.form = {
        id: row.id,
        staffName: row.staffName,
        staffCode: row.staffCode,
        username: row.username,
        password: '',
        departmentId: row.departmentId,
        position: row.position,
        phone: row.phone,
        email: row.email,
        gender: row.gender,
        entryDate: row.entryDate,
        status: row.status
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (!valid) return

        this.submitLoading = true
        try {
          let res
          if (this.form.id) {
            res = await updateStaff(this.form.id, this.form)
          } else {
            res = await addStaff(this.form)
          }

          if (res.code === 200) {
            this.$message.success(this.form.id ? '更新成功' : '添加成功')
            this.dialogVisible = false
            this.fetchData()
          } else {
            this.$message.error(res.message || '操作失败')
          }
        } catch (error) {
          console.error('提交失败:', error)
          this.$message.error('操作失败')
        } finally {
          this.submitLoading = false
        }
      })
    },
    handleDelete(row) {
      this.$confirm(`确定要删除人员"${row.staffName}"吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await deleteStaff(row.id)
          if (res.code === 200) {
            this.$message.success('删除成功')
            this.fetchData()
          } else {
            this.$message.error(res.message || '删除失败')
          }
        } catch (error) {
          console.error('删除失败:', error)
          this.$message.error('删除失败')
        }
      }).catch(() => {})
    },
    async handleStatusChange(row) {
      try {
        const res = await updateStaffStatus(row.id, row.status)
        if (res.code === 200) {
          this.$message.success('状态更新成功')
        } else {
          this.$message.error(res.message || '状态更新失败')
          row.status = row.status === 1 ? 0 : 1
        }
      } catch (error) {
        console.error('状态更新失败:', error)
        this.$message.error('状态更新失败')
        row.status = row.status === 1 ? 0 : 1
      }
    },
    async handleViewPermission(row) {
      this.viewPermissionDialogVisible = true
      try {
        const res = await getStaffPermissions(row.id)
        if (res.code === 200) {
          this.staffPermissions = res.data
        }
      } catch (error) {
        console.error('获取权限失败:', error)
        this.$message.error('获取权限失败')
      }
    },
    getPermissionName(permCode) {
      return this.permissionNameMap[permCode] || permCode
    }
  }
}
</script>

<style scoped>
.staff-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-card {
  margin-bottom: 20px;
}

.operation-buttons {
  display: flex;
  flex-wrap: nowrap;
  gap: 5px;
}

.operation-buttons .el-button {
  padding: 7px 10px;
}

.el-pagination {
  margin-top: 20px;
  text-align: right;
}

.empty-text {
  text-align: center;
  color: #999;
  padding: 20px;
}

.permission-container {
  padding: 10px;
}

.permission-section {
  margin-bottom: 20px;
}

.permission-section h4 {
  margin: 0 0 10px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
  color: #303133;
  font-size: 14px;
}

.permission-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.no-permission {
  color: #909399;
  font-size: 13px;
  padding: 5px;
}
</style>