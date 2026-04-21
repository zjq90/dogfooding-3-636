<template>
  <div class="department-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="部门名称/编号"
            clearable
            style="width: 220px"
          />
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

    <!-- 部门列表 -->
    <el-card class="table-card">
      <div slot="header" class="card-header">
        <span>部门列表</span>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">
          新增部门
        </el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        style="width: 100%"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      >
        <el-table-column prop="deptName" label="部门名称" min-width="150" />
        <el-table-column prop="deptCode" label="部门编号" width="120" />
        <el-table-column prop="parentName" label="上级部门" width="150">
          <template slot-scope="scope">
            {{ scope.row.parentName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
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
        <el-table-column label="操作" width="250" fixed="right">
          <template slot-scope="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="warning" size="small" @click="handlePermission(scope.row)">
              权限
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">
              删除
            </el-button>
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
      width="500px"
    >
      <el-form
        ref="form"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="form.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="部门编号" prop="deptCode">
          <el-input v-model="form.deptCode" placeholder="格式：B-数字，如B-001" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="上级部门" prop="parentId">
          <el-select v-model="form.parentId" placeholder="请选择上级部门" clearable style="width: 100%">
            <el-option :key="0" label="无（顶级部门）" :value="0" />
            <el-option
              v-for="item in departmentList"
              :key="item.id"
              :label="item.deptName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="部门描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入部门描述" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" style="width: 100%" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </div>
    </el-dialog>

    <!-- 权限设置对话框 -->
    <el-dialog
      title="权限设置"
      :visible.sync="permissionDialogVisible"
      width="600px"
    >
      <el-tree
        ref="permissionTree"
        :data="permissionTreeData"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedPermissionIds"
        :props="{ label: 'permissionName', children: 'children' }"
        default-expand-all
      />
      <div slot="footer">
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="permissionSubmitLoading" @click="handlePermissionSubmit">
          确定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getDepartmentPage,
  getDepartmentList,
  addDepartment,
  updateDepartment,
  deleteDepartment,
  updateDepartmentStatus,
  setDepartmentPermissions,
  getDepartmentPermissions
} from '@/api/department'
import { getPermissionTree } from '@/api/permission'

export default {
  name: 'DepartmentManagement',
  data() {
    return {
      loading: false,
      submitLoading: false,
      permissionSubmitLoading: false,
      page: 1,
      size: 10,
      total: 0,
      searchForm: {
        keyword: ''
      },
      tableData: [],
      departmentList: [],
      dialogVisible: false,
      dialogTitle: '新增部门',
      form: {
        id: null,
        deptName: '',
        deptCode: '',
        parentId: 0,
        description: '',
        sortOrder: 0,
        status: 1
      },
      rules: {
        deptName: [
          { required: true, message: '请输入部门名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        deptCode: [
          { required: true, message: '请输入部门编号', trigger: 'blur' },
          { pattern: /^B-\d+$/, message: '格式：B-数字，如B-001', trigger: 'blur' }
        ]
      },
      permissionDialogVisible: false,
      currentDeptId: null,
      permissionTreeData: [],
      checkedPermissionIds: []
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
        const res = await getDepartmentPage({
          page: this.page,
          size: this.size,
          keyword: this.searchForm.keyword
        })
        if (res.code === 200) {
          this.tableData = res.data.records
          this.total = res.data.total
        }
      } catch (error) {
        console.error('获取部门列表失败:', error)
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
      this.dialogTitle = '新增部门'
      this.form = {
        id: null,
        deptName: '',
        deptCode: '',
        parentId: 0,
        description: '',
        sortOrder: 0,
        status: 1
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },
    handleEdit(row) {
      this.dialogTitle = '编辑部门'
      this.form = {
        id: row.id,
        deptName: row.deptName,
        deptCode: row.deptCode,
        parentId: row.parentId || 0,
        description: row.description,
        sortOrder: row.sortOrder,
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
          const data = { ...this.form }
          if (data.parentId === 0) {
            data.parentId = 0
          }

          let res
          if (data.id) {
            res = await updateDepartment(data.id, data)
          } else {
            res = await addDepartment(data)
          }

          if (res.code === 200) {
            this.$message.success(data.id ? '更新成功' : '添加成功')
            this.dialogVisible = false
            this.fetchData()
            this.fetchDepartmentList()
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
      this.$confirm(`确定要删除部门"${row.deptName}"吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const res = await deleteDepartment(row.id)
          if (res.code === 200) {
            this.$message.success('删除成功')
            this.fetchData()
            this.fetchDepartmentList()
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
        const res = await updateDepartmentStatus(row.id, row.status)
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
    async handlePermission(row) {
      this.currentDeptId = row.id
      this.permissionDialogVisible = true
      this.permissionSubmitLoading = true
      try {
        const [treeRes, permRes] = await Promise.all([
          getPermissionTree(),
          getDepartmentPermissions(row.id)
        ])
        if (treeRes.code === 200) {
          this.permissionTreeData = treeRes.data
        }
        if (permRes.code === 200) {
          this.checkedPermissionIds = permRes.data
        }
      } catch (error) {
        console.error('获取权限数据失败:', error)
        this.$message.error('获取权限数据失败')
      } finally {
        this.permissionSubmitLoading = false
      }
    },
    async handlePermissionSubmit() {
      const checkedKeys = this.$refs.permissionTree.getCheckedKeys()
      const halfCheckedKeys = this.$refs.permissionTree.getHalfCheckedKeys()
      const allCheckedKeys = [...checkedKeys, ...halfCheckedKeys]

      this.permissionSubmitLoading = true
      try {
        const res = await setDepartmentPermissions(this.currentDeptId, allCheckedKeys)
        if (res.code === 200) {
          this.$message.success('权限设置成功')
          this.permissionDialogVisible = false
        } else {
          this.$message.error(res.message || '权限设置失败')
        }
      } catch (error) {
        console.error('权限设置失败:', error)
        this.$message.error('权限设置失败')
      } finally {
        this.permissionSubmitLoading = false
      }
    }
  }
}
</script>

<style scoped>
.department-container {
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

.el-pagination {
  margin-top: 20px;
  text-align: right;
}
</style>