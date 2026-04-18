<template>
  <div class="department-container">
    <el-card class="table-card">
      <div slot="header" class="card-header">
        <span>部门列表</span>
        <el-button v-if="isAdmin" type="primary" icon="el-icon-plus" @click="handleAdd">
          新增部门
        </el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        row-key="id"
        :default-expand-all="true"
        :tree-props="{children: 'children'}"
        style="width: 100%"
      >
        <el-table-column type="index" width="50" align="center" />
        <el-table-column prop="name" label="部门名称" min-width="150" />
        <el-table-column prop="code" label="部门编号" width="120" />
        <el-table-column prop="leader" label="负责人" width="100" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="150" show-overflow-tooltip />
        <el-table-column prop="address" label="地址" min-width="150" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
            <el-button v-if="isAdmin" type="primary" size="small" @click="handleEdit(scope.row)">
              编辑
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
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="部门编号" prop="code">
          <el-input v-model="form.code" placeholder="格式：B-数字" />
        </el-form-item>
        <el-form-item label="上级部门" prop="parentId">
          <el-select v-model="form.parentId" clearable placeholder="请选择上级部门" style="width: 100%">
            <el-option label="顶级部门" :value="0" />
            <el-option
              v-for="dept in departmentOptions"
              :key="dept.id"
              :label="dept.name"
              :value="dept.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人" prop="leader">
          <el-input v-model="form.leader" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入部门地址" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
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
  </div>
</template>

<script>
import { getDepartmentList, getDepartmentTree, addDepartment, updateDepartment, deleteDepartment } from '@/api/department'
import { mapGetters } from 'vuex'

export default {
  name: 'DepartmentManagement',
  computed: {
    ...mapGetters(['isAdmin'])
  },
  data() {
    return {
      loading: false,
      tableData: [],
      departmentOptions: [],
      dialogVisible: false,
      dialogTitle: '新增部门',
      submitLoading: false,
      form: {
        id: null,
        name: '',
        code: '',
        parentId: 0,
        leader: '',
        phone: '',
        email: '',
        address: '',
        sortOrder: 0,
        status: 1
      },
      rules: {
        name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
        code: [
          { required: true, message: '请输入部门编号', trigger: 'blur' },
          { pattern: /^B-\d+$/, message: '部门编号格式错误，格式应为：B-数字', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const [treeRes, listRes] = await Promise.all([
          getDepartmentTree(),
          getDepartmentList()
        ])
        if (treeRes.code === 200) {
          this.tableData = treeRes.data
        }
        if (listRes.code === 200) {
          this.departmentOptions = listRes.data
        }
      } catch (error) {
        console.error('加载部门失败:', error)
      } finally {
        this.loading = false
      }
    },

    handleAdd() {
      if (!this.isAdmin) {
        this.$message.warning('无权限添加部门')
        return
      }
      this.dialogTitle = '新增部门'
      this.form = {
        id: null,
        name: '',
        code: '',
        parentId: 0,
        leader: '',
        phone: '',
        email: '',
        address: '',
        sortOrder: 0,
        status: 1
      }
      this.dialogVisible = true
    },

    handleEdit(row) {
      if (!this.isAdmin) {
        this.$message.warning('无权限编辑部门')
        return
      }
      this.dialogTitle = '编辑部门'
      this.form = { ...row }
      this.dialogVisible = true
    },

    async handleDelete(row) {
      if (!this.isAdmin) {
        this.$message.warning('无权限删除部门')
        return
      }
      try {
        await this.$confirm(`确定要删除部门「${row.name}」吗？`, '提示', {
          type: 'warning'
        })

        const res = await deleteDepartment(row.id)
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
            const api = this.form.id ? updateDepartment(this.form.id, this.form) : addDepartment(this.form)
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
.department-container {
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
