<template>
  <div class="employee-container">
    <el-card class="table-card">
      <div slot="header" class="card-header">
        <span>人员列表</span>
        <el-button v-if="isAdmin" type="primary" icon="el-icon-plus" @click="handleAdd">
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
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="employeeNo" label="员工编号" width="120" />
        <el-table-column prop="departmentName" label="所属部门" width="120" />
        <el-table-column prop="position" label="职位" width="100" />
        <el-table-column label="性别" width="80" align="center">
          <template slot-scope="scope">
            {{ scope.row.gender === 1 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="150" show-overflow-tooltip />
        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 1 ? '在职' : '离职' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template slot-scope="scope">
            <el-button v-if="isAdmin" type="primary" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button v-if="isAdmin" type="warning" size="small" @click="handlePermission(scope.row)">
              设置权限
            </el-button>
            <el-button
              v-if="isAdmin"
              type="danger"
              size="small"
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        :total="total"
        :page-size="pageSize"
        :current-page="pageNum"
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[5, 10, 20, 50]"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        style="margin-top: 20px; text-align: right"
      />
    </el-card>

    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="600px"
    >
      <el-form
        ref="form"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="员工编号" prop="employeeNo">
          <el-input v-model="form.employeeNo" placeholder="请输入员工编号" />
        </el-form-item>
        <el-form-item label="所属部门" prop="departmentId">
          <el-select v-model="form.departmentId" placeholder="请选择所属部门" style="width: 100%">
            <el-option
              v-for="dept in departmentOptions"
              :key="dept.id"
              :label="dept.name"
              :value="dept.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="职位" prop="position">
          <el-input v-model="form.position" placeholder="请输入职位" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="出生日期" prop="birthday">
          <el-date-picker
            v-model="form.birthday"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            value-format="yyyy-MM-dd"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">在职</el-radio>
            <el-radio :label="0">离职</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="权限设置"
      :visible.sync="permissionDialogVisible"
      width="500px"
    >
      <el-tree
        :data="menuTree"
        :props="{ label: 'name', children: 'children' }"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedMenuIds"
        ref="permissionTree"
        check-strictly
      />
      <div slot="footer">
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="permissionLoading" @click="handleSavePermission">
          保存
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getEmployeeList, getEmployeeDetail, addEmployee, updateEmployee, deleteEmployee, setEmployeePermissions, getMenuTree } from '@/api/employee'
import { getDepartmentList } from '@/api/department'
import { mapGetters } from 'vuex'

export default {
  name: 'EmployeeManagement',
  computed: {
    ...mapGetters(['isAdmin'])
  },
  data() {
    return {
      loading: false,
      tableData: [],
      departmentOptions: [],
      menuTree: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      dialogVisible: false,
      dialogTitle: '新增人员',
      submitLoading: false,
      permissionDialogVisible: false,
      permissionLoading: false,
      currentEmployeeId: null,
      checkedMenuIds: [],
      form: {
        id: null,
        name: '',
        employeeNo: '',
        departmentId: null,
        position: '',
        gender: 1,
        phone: '',
        email: '',
        birthday: '',
        status: 1
      },
      rules: {
        name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        employeeNo: [{ required: true, message: '请输入员工编号', trigger: 'blur' }],
        departmentId: [{ required: true, message: '请选择所属部门', trigger: 'change' }]
      }
    }
  },
  created() {
    this.loadData()
    this.loadDepartments()
    this.loadMenuTree()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getEmployeeList(this.pageNum, this.pageSize)
        if (res.code === 200) {
          this.tableData = res.data.records
          this.total = res.data.total
        }
      } catch (error) {
        console.error('加载人员列表失败:', error)
      } finally {
        this.loading = false
      }
    },

    async loadDepartments() {
      try {
        const res = await getDepartmentList()
        if (res.code === 200) {
          this.departmentOptions = res.data
        }
      } catch (error) {
        console.error('加载部门列表失败:', error)
      }
    },

    async loadMenuTree() {
      try {
        const res = await getMenuTree()
        if (res.code === 200) {
          this.menuTree = res.data
        }
      } catch (error) {
        console.error('加载菜单树失败:', error)
      }
    },

    handleSizeChange(val) {
      this.pageSize = val
      this.loadData()
    },

    handlePageChange(val) {
      this.pageNum = val
      this.loadData()
    },

    handleAdd() {
      if (!this.isAdmin) {
        this.$message.warning('无权限添加人员')
        return
      }
      this.dialogTitle = '新增人员'
      this.form = {
        id: null,
        name: '',
        employeeNo: '',
        departmentId: null,
        position: '',
        gender: 1,
        phone: '',
        email: '',
        birthday: '',
        status: 1
      }
      this.dialogVisible = true
    },

    handleEdit(row) {
      if (!this.isAdmin) {
        this.$message.warning('无权限编辑人员')
        return
      }
      this.dialogTitle = '编辑人员'
      this.form = { ...row, birthday: row.birthday || '' }
      this.dialogVisible = true
    },

    async handlePermission(row) {
      if (!this.isAdmin) {
        this.$message.warning('无权限设置权限')
        return
      }
      this.currentEmployeeId = row.id
      try {
        const res = await getEmployeeDetail(row.id)
        if (res.code === 200) {
          this.checkedMenuIds = res.data.permissionIds || []
        }
      } catch (error) {
        console.error('获取人员权限失败:', error)
      }
      this.permissionDialogVisible = true
    },

    async handleSavePermission() {
      this.permissionLoading = true
      try {
        const checkedKeys = this.$refs.permissionTree.getCheckedKeys()
        const halfCheckedKeys = this.$refs.permissionTree.getHalfCheckedKeys()
        const allCheckedKeys = [...checkedKeys, ...halfCheckedKeys]

        const res = await setEmployeePermissions(this.currentEmployeeId, allCheckedKeys)
        if (res.code === 200) {
          this.$message.success('权限设置成功')
          this.permissionDialogVisible = false
        }
      } catch (error) {
        console.error('保存权限失败:', error)
      } finally {
        this.permissionLoading = false
      }
    },

    async handleDelete(row) {
      if (!this.isAdmin) {
        this.$message.warning('无权限删除人员')
        return
      }
      try {
        await this.$confirm(`确定要删除人员「${row.name}」吗？`, '提示', {
          type: 'warning'
        })

        const res = await deleteEmployee(row.id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadData()
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除失败:', error)
        }
      }
    },

    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          this.submitLoading = true
          try {
            const api = this.form.id ? updateEmployee(this.form.id, this.form) : addEmployee(this.form)
            const res = await api

            if (res.code === 200) {
              this.$message.success(this.form.id ? '更新成功' : '添加成功')
              this.dialogVisible = false
              this.loadData()
            }
          } catch (error) {
            console.error('提交失败:', error)
          } finally {
            this.submitLoading = false
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.employee-container {
  padding-bottom: 24px;
}

.table-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-size: 16px;
  font-weight: 500;
}
</style>
