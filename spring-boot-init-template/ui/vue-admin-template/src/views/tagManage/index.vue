<template>
  <div class="tag-manage-container">
    <el-collapse accordion>
      <el-card style="max-width: 100%;">
        <el-row>
          <el-col :span="18">
            <el-form :inline="true" :model="queryForm" class="demo-form-inline" size="small" label-width="80px">
              <el-form-item label="标签名称">
                <el-input v-model="queryForm.tagName" placeholder="请输入标签名称" style="width: 200px" />
              </el-form-item>
            </el-form>
          </el-col>
          <el-col :span="6" style="text-align: center">
            <el-button :loading="queryLoading" type="primary" size="small" style="width: 80px" @click="handleQuery">查询</el-button>
            <el-button :loading="queryLoading" size="small" style="width: 80px" @click="handleReset">重置</el-button>
            <el-button type="success" size="small" style="width: 80px" @click="openAddDialog">添加</el-button>
          </el-col>
        </el-row>
      </el-card>
    </el-collapse>
    <el-table v-loading="pageLoading" :data="queryResult.records" stripe style="width: 100%">
      <el-table-column type="index" label="ID" />
      <el-table-column prop="tagName" label="标签名称" />
      <el-table-column fixed="right" label="操作" width="150">
        <template #default="scope">
          <el-button type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      :current-page="Number.parseInt(queryResult.current)"
      :page-sizes="[10, 30, 50, 100]"
      :page-size="10"
      layout="total, sizes, prev, pager, next, jumper"
      :total="Number.parseInt(queryResult.total)"
      style="text-align: right; margin-top: 10px"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
    <el-dialog
      :show-close="true"
      title="添加标签"
      :visible.sync="addDialogVisible"
      @close="handleCancelAdd"
      width="400px"
      class="custom-dialog"
    >
      <el-form ref="addForm" :model="addForm" label-width="80px">
        <el-col>
          <el-form-item label="标签名称" prop="tagName">
            <el-input v-model="addForm.tagName" placeholder="请输入标签名称" autocomplete="off" />
          </el-form-item>
        </el-col>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleCancelAdd">取消</el-button>
        <el-button type="primary" :loading="addLoading" @click="handleAdd">添加</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getTags, addTag, deleteTag } from '@/api/tag'
import { Message } from 'element-ui'
export default {
  name: 'TagManage',
  data() {
    return {
      addLoading: false,
      queryLoading: false,
      pageLoading: false,
      addDialogVisible: false,
      queryResult: [],
      queryForm: {
        tagName: undefined,
        page: 1,
        size: 10
      },
      addForm: {
        tagName: undefined
      }
    }
  },
  created() {
    this.pageLoading = true
    this.handleQuery()
  },
  methods: {
    handleQuery() {
      this.queryForm.page = 1
      this.pageLoading = true
      this.queryLoading = true
      getTags(this.queryForm).then(response => {
        this.queryResult = response.data
        this.pageLoading = false
        this.queryLoading = false
      })
    },
    handleReset() {
      this.queryForm.tagName = undefined
      this.pageLoading = true
      getTags(this.queryForm).then(response => {
        this.queryResult = response.data
        this.pageLoading = false
      })
    },
    handleDelete(data) {
      this.$confirm('此操作将删除该标签, 是否继续?', '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteTag(data.tagId).then(response => {
          this.pageLoading = true
          getTags(this.queryForm).then(response => {
            this.queryResult = response.data
            this.pageLoading = false
          })
          this.$message.success(response.msg)
        })
      })
    },
    openAddDialog() {
      this.addDialogVisible = true
    },
    handleAdd() {
      this.$refs['addForm'].validate(valid => {
        if (valid) {
          this.addLoading = true
          addTag(this.addForm).then(response => {
            if (this.queryResult.total % this.queryResult.size === 0) {
              this.queryForm.page++;
            }
            this.pageLoading = true
            getTags(this.queryForm).then(response => {
              this.queryResult = response.data
              this.pageLoading = false
            })
            this.resetAddForm()
            this.addDialogVisible = false
            Message.success(response.msg)
          }).finally(() => {
            this.addLoading = false
          })
        }
      })
    },
    handleCancelAdd() {
      this.addDialogVisible = false
      this.resetAddForm()
      this.addLoading = false
    },
    handleSizeChange(val) {
      this.queryForm.size = val
      this.pageLoading = true
      getTags(this.queryForm).then(response => {
        this.queryResult = response.data
        this.pageLoading = false
      })
    },
    handleCurrentChange(val) {
      this.queryForm.page = val
      this.pageLoading = true
      getTags(this.queryForm).then(response => {
        this.queryResult = response.data
        this.pageLoading = false
      })
    },
    async resetAddForm() {
      this.addForm.tagName = undefined
    }
  }
}
</script>

<style lang="scss">
.tag-manage-container {
  margin: 30px;
}

.dialog-footer {
  text-align: right;
  padding: 10px 0;
}

.el-dialog {
  border-radius: 10px;
  background-color: #f7f9fc;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.el-dialog__header {
  background-color: #f0f2f5;
  border-bottom: 1px solid #ebeef5;
}

.el-dialog__title {
  color: #333;
  font-weight: bold;
}

.el-dialog__body {
  padding: 20px;
}

.el-button--primary {
  background-color: #409eff;
  border-color: #409eff;
}
</style>
