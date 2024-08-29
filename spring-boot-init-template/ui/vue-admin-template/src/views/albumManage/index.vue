<template>
  <div class="photo-manage-container">
    <el-collapse accordion>
      <el-card style="max-width: 100%;">
        <el-row>
          <el-col :span="18">
            <el-form :inline="true" :model="queryForm" class="demo-form-inline" size="small" label-width="80px">
              <el-form-item label="照片描述">
                <el-input v-model="queryForm.photoDescription" placeholder="请输入照片描述" style="width: 200px" />
              </el-form-item>
            </el-form>
          </el-col>
          <el-col :span="6" style="text-align: center">
            <el-button :loading="queryLoading" type="primary" size="small" style="width: 80px" @click="handleQuery">查询</el-button>
            <el-button :loading="queryLoading" size="small" style="width: 80px" @click="handleReset">重置</el-button>
            <el-button type="primary" size="small" style="width: 100px" @click="openAddDialog">添加</el-button>
          </el-col>
        </el-row>
      </el-card>

    </el-collapse>
    <el-table v-loading="pageLoading" :data="queryResult.records" stripe style="width: 100%">
      <el-table-column type="index"  label="ID"/>
      <el-table-column prop="photoDescription" label="照片描述" />
      <el-table-column prop="tagName" label="标签" />
      <el-table-column label="照片">
        <template #default="scope">
          <img :src="scope.row.photoUrl" alt="照片" style="width: 100px; height: auto;" />
        </template>
      </el-table-column>
      <el-table-column prop="userName" label="上传用户" />
      <el-table-column label="上传时间">
        <template #default="scope">
          <span>{{ formatDate(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作" width="150">
        <template #default="scope">
          <el-button type="primary" size="mini" @click="handleEdit(scope.row)">修改</el-button>
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
      title="添加照片"
      :visible.sync="addDialogVisible"
      @close="handleCancelAdd"
      width="400px"
      class="custom-dialog"
    >
      <el-form ref="addForm" :model="addForm" label-width="80px">
        <el-col>
          <el-form-item label="上传照片" prop="photoUrl">
            <el-upload
              class="avatar-uploader"
              action="http://43.142.255.148:38080/api/admin/photo/upload/photo"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
              :limit="1"
            >
              <img v-if="addForm.photoUrl" :src="addForm.photoUrl" class="avatar" />
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </el-form-item>
        </el-col>
        <el-col>
          <el-form-item label="照片描述" prop="photoDescription">
            <el-input v-model="addForm.photoDescription" placeholder="请输入照片描述" autocomplete="off" />
          </el-form-item>
        </el-col>
        <el-col>
          <el-form-item label="选择标签" prop="selectedTagIds">
            <el-select v-model="addForm.selectedTagIds" multiple placeholder="请选择标签">
              <el-option
                v-for="tag in tagList"
                :key="tag.tagId"
                :label="tag.tagName"
                :value="tag.tagId"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleCancelAdd">取消</el-button>
        <el-button type="primary" :loading="addLoading" @click="handleAdd">添加</el-button>
      </div>
    </el-dialog>
    <el-dialog
      :show-close="true"
      title="修改照片"
      :visible.sync="editDialogVisible"
      @close="handleCancelEdit"
      width="400px"
      class="custom-dialog"
    >
      <el-form ref="editForm" :model="editForm" label-width="80px">
        <el-col>
          <el-form-item label="上传照片" prop="photoUrl">
            <el-upload
              class="avatar-uploader"
              action="http://43.142.255.148:38080/api/admin/photo/upload/photo"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
              :limit="1"
            >
              <img v-if="editForm.photoUrl" :src="editForm.photoUrl" class="avatar" />
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </el-form-item>
        </el-col>
        <el-col>
          <el-form-item label="照片描述" prop="photoDescription">
            <el-input v-model="editForm.photoDescription" placeholder="请输入照片描述" autocomplete="off" />
          </el-form-item>
        </el-col>
        <el-col>
          <el-form-item label="选择标签" prop="selectedTagIds">
            <el-select v-model="editForm.selectedTagIds" multiple placeholder="请选择标签">
              <el-option
                v-for="tag in tagList"
                :key="tag.tagId"
                :label="tag.tagName"
                :value="tag.tagId"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleCancelEdit">取消</el-button>
        <el-button type="primary" :loading="addLoading" @click="handleUpdate">修改</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getPhotos, deletePhoto, updatePhotoWithTags, addPhotoWithTags } from '@/api/photo'
import { Message } from 'element-ui'
import { getAllTag } from "@/api/tag";
import moment from 'moment';
export default {
  name: 'PhotoManage',
  data() {
    return {
      imageUrl: '',
      addLoading: false,
      editLoading: false,
      queryLoading: false,
      pageLoading: false,
      addDialogVisible: false,
      editDialogVisible: false,
      queryResult: [],
      queryForm: {
        description: undefined,
        userAccount: undefined,
        uploadTime: undefined,
        page: 1,
        size: 10
      },
      addForm: {
        photoUrl: undefined,
        photoDescription: undefined,
        selectedTagIds: []
      },
      editForm: {
        photoId: undefined,
        photoUrl: undefined,
        photoDescription: undefined,
        selectedTagIds: []
      },
      tagList: []
    }
  },
  created() {
    this.pageLoading = true
    getPhotos(this.queryForm).then(response => {
      this.queryResult = response.data
      this.pageLoading = false
    })
    this.fetchTagList()
  },
  methods: {
    handleEdit(photo) {
      if (!photo) {
        this.$message.error('未找到照片信息');
        return;
      }
      if (!this.tagList || this.tagList.length === 0) {
        this.fetchTagList().then(() => {
          this.processPhotoEdit(photo);
        });
      } else {
        this.processPhotoEdit(photo);
      }
    },

    processPhotoEdit(photo) {
      const tagNames = photo.tagName ? photo.tagName.split(',').map(name => name.trim()) : [];

      const selectedTagIds = tagNames.map(tagName => {
        const tag = this.tagList.find(tag => tag.tagName === tagName);
        return tag ? tag.tagId : null;
      }).filter(tagId => tagId !== null);
      console.log("ID",photo.photoId)
      this.editForm = {
        photoId: photo.photoId,
        photoUrl: photo.photoUrl,
        photoDescription: photo.photoDescription,
        selectedTagIds: selectedTagIds
      };

      this.editDialogVisible = true;
    },

    handleAvatarSuccess(res) {
      this.addForm.photoUrl = res.msg
    },
    beforeAvatarUpload(file) {
      const isLt2M = file.size / 1024 / 1024 < 10
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 10MB!')
      }
      return isLt2M
    },
    handleQuery() {
      this.queryForm.page = 1
      this.pageLoading = true
      this.queryLoading = true
      getPhotos(this.queryForm).then(response => {
        this.queryResult = response.data
        this.pageLoading = false
        this.queryLoading = false
      })
    },
    handleReset() {
      this.queryForm.createTime = undefined
      this.queryForm.photoDescription = undefined
      this.queryForm.userName = undefined
      this.pageLoading = true
      getPhotos(this.queryForm).then(response => {
        this.queryResult = response.data
        this.pageLoading = false
      })
    },
    handleDelete(data) {
      this.$confirm('此操作将删除该照片, 是否继续?', '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deletePhoto(data.photoId).then(response => {
          this.pageLoading = true
          getPhotos(this.queryForm).then(response => {
            this.queryResult = response.data
            this.pageLoading = false
          })
          this.$message.success(response.msg)
        })
      })
    },
    fetchTagList() {
      getAllTag().then(response => {
        console.log(response)
        this.tagList = response.data;
      }).catch(error => {
        this.$message.error('获取标签列表失败');
      });
    },
    // 格式化时间
    formatDate(date) {
      return moment(date).format('YYYY-MM-DD HH:mm:ss');
    },
    openAddDialog() {
      this.addDialogVisible = true;
      this.fetchTagList();  // 打开对话框时获取标签列表
    },
    handleAdd() {
      this.$refs['addForm'].validate(valid => {
        if (valid) {
          this.addLoading = true;
          const data = {
            photoUrl: this.addForm.photoUrl,
            photoDescription: this.addForm.photoDescription
          };
          // 添加照片并关联标签
          addPhotoWithTags(data, this.addForm.selectedTagIds).then(response => {
            if (this.queryResult.total % this.queryResult.size === 0) {
              this.queryForm.page++;
            }
            this.pageLoading = true;
            getPhotos(this.queryForm).then(response => {
              this.queryResult = response.data;
              this.pageLoading = false;
            });
            this.resetAddForm();
            this.addDialogVisible = false;
            Message.success(response.msg);
          }).finally(() => {
            this.addLoading = false;
          });
        }
      });
    },
    handleUpdate() {
      this.$refs['editForm'].validate(valid => {
        if (valid) {
          this.editLoading = true;
          const data = {
            photoId: this.editForm.photoId,
            photoUrl: this.editForm.photoUrl,
            photoDescription: this.editForm.photoDescription
          };
          console.log(data)
          updatePhotoWithTags(data, this.editForm.selectedTagIds).then(response => {
            this.pageLoading = true;
            getPhotos(this.queryForm).then(response => {
              this.queryResult = response.data;
              this.pageLoading = false;
            });
            this.resetEditForm();
            this.editDialogVisible = false;
            Message.success(response.msg);
          }).finally(() => {
            this.editLoading = false;
          });
        }
      });
    },
    handleCancelAdd() {
      this.addDialogVisible = false
      this.resetAddForm()
      this.addLoading = false
    },
    handleSizeChange(val) {
      this.queryForm.size = val
      this.pageLoading = true
      getPhotos(this.queryForm).then(response => {
        this.queryResult = response.data
        this.pageLoading = false
      })
    },
    handleCurrentChange(val) {
      this.queryForm.page = val
      this.pageLoading = true
      getPhotos(this.queryForm).then(response => {
        this.queryResult = response.data
        this.pageLoading = false
      })
    },
    async resetAddForm() {
      this.addForm.photoUrl = undefined
      this.addForm.photoDescription = undefined
      this.addForm.selectedTagIds = []; // 重置选中的标签
    },
    handleCancelEdit() {
      this.editDialogVisible = false;
      this.resetEditForm();
      this.editLoading = false;
    },

    async resetEditForm() {
      this.editForm = {
        photoUrl: undefined,
        photoDescription: undefined,
        selectedTagIds: []
      };
    }
  }
}
</script>
<style lang="scss">
.photo-manage-container {
  margin: 30px;
}

.avatar-uploader {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 178px;
  height: 178px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  background-color: #f5f7fa;
  margin: 0 auto;
}

.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100%;
  height: 100%;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
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
