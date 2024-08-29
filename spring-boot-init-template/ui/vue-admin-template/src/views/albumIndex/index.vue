<template>
  <div class="photo-list-container">
    <!-- 搜索栏 -->
    <div class="search-bar-container">
      <div class="search-bar">
        <input v-model="searchTag" type="text" placeholder="输入标签/点击下列标签即可快速筛选数据" />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <i class="icon-camera"></i>
      </div>
    </div>

    <!-- 标签列表 -->
    <div class="tag-list">
      <el-tag
        v-for="(tag, index) in tags"
        :key="index"
        @click="handleTagClick(tag.name)"
        class="tag-item"
        :class="{ 'selected-tag': searchTag === tag.name }"
      >
        {{ tag.name }}<span> (<span style="color: #01ff00"> {{ tag.count }} </span>)</span>
      </el-tag>
      <!-- 重置按钮 -->
      <el-button type="warning" @click="resetSearch" class="reset-button">
        重置
      </el-button>
    </div>

    <!-- 图片列表 -->
    <div class="image-gallery">
      <div class="image-item" v-for="(photo, index) in filteredPhotos" :key="index">
        <el-image
          :src="photo.photoUrl"
          :alt="photo.tagName"
          fit="contain"
          :preview-src-list="filteredPhotos.map(p => p.photoUrl)"
        />
        <!-- 标签名称 -->
        <div class="tag-name">{{ photo.tagName || '未标注' }}</div>
      </div>
    </div>
  </div>
</template>


<script>
import { getPhotos } from '@/api/photo'
import { getAllTag } from "@/api/tag"
import { Message } from 'element-ui'

export default {
  name: 'PhotoList',
  data() {
    return {
      queryResult: [],
      searchTag: '', // 用于存储当前搜索的标签
      tags: []  // 用于存储标签及其对应的图片数量
    }
  },
  computed: {
    filteredPhotos() {
      if (this.searchTag) {
        return this.queryResult.filter(photo => {
          const tags = photo.tagName ? photo.tagName.split(', ') : [];
          return tags.includes(this.searchTag);
        });
      } else {
        return this.queryResult;
      }
    }
  },
  created() {
    this.loadPhotos();
  },
  methods: {
    loadPhotos() {
      getPhotos().then(response => {
        this.queryResult = response.data.records;
        this.calculateTagCounts();  // 计算每个标签的出现次数
      }).catch(() => {
        Message.error('Failed to load photos');
      });
    },
    calculateTagCounts() {
      const tagCounts = {};
      this.queryResult.forEach(photo => {
        const tags = photo.tagName ? photo.tagName.split(', ') : [];
        tags.forEach(tag => {
          if (tagCounts[tag]) {
            tagCounts[tag]++;
          } else {
            tagCounts[tag] = 1;
          }
        });
      });
      this.tags = Object.keys(tagCounts).map(tag => ({
        name: tag,
        count: tagCounts[tag]
      }));
    },
    handleSearch() {
      // 调用搜索逻辑
      this.filteredPhotos;
    },
    handleTagClick(tagName) {
      // 点击标签时，将标签名设为搜索标签
      if (this.searchTag === tagName) {
        // 如果点击的标签已经被选中，再次点击取消选择
        this.searchTag = '';
      } else {
        this.searchTag = tagName;
      }
      this.handleSearch();
    },
    resetSearch() {
      this.searchTag = '';
      this.handleSearch();
    }
  }
}
</script>

<style lang="scss">
.photo-list-container {
  padding: 20px;

  .search-bar-container {
    display: flex;
    align-items: center;
    margin-bottom: 20px;

    .logo img {
      height: 40px;
      margin-right: 20px;
    }

    .search-bar {
      display: flex;
      align-items: center;
      flex-grow: 1;
      background-color: #ffffff;
      border-radius: 50px;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
      padding: 8px 20px;

      input {
        flex-grow: 1;
        border: none;
        padding: 10px;
        font-size: 16px;
        outline: none;
        background-color: transparent;
      }

      .el-button {
        background-color: #06c167;
        color: white;
        border-radius: 25px;
        padding: 8px 16px;
        cursor: pointer;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
      }

      .icon-camera {
        font-size: 18px;
        margin-left: 10px;
      }
    }
  }

  .tag-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-bottom: 20px;

    .tag-item {
      cursor: pointer;
      font-size: 14px;
      padding: 1px 15px;
      background-color: #ff6f00;
      border-radius: 20px;
      color: white;
      transition: background-color 0.3s;

      &:hover {
        background-color: #06c167;
        color: white;
      }
      &.selected-tag {  // 高亮选中的标签
        background-color: #06c167;
        color: white;
      }
    }

    .reset-button {
      margin-left: auto;
      margin-right: 30px;
      padding: 8px 15px;
      font-size: 14px;
      border-radius: 20px;
      background-color: #ff6f00;
      color: white;
      transition: background-color 0.3s;

      &:hover {
        background-color: #e65c00;
      }
    }
  }

  .image-gallery {
    column-count: 4; // 图片展示为四列瀑布流布局
    column-gap: 10px; // 列之间的间距

    .image-item {
      break-inside: avoid; // 确保图片不会被打断
      margin-bottom: 15px;
      overflow: hidden;
      border-radius: 10px;
      position: relative;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
      transition: transform 0.3s ease, box-shadow 0.3s ease;

      &:hover {
        transform: scale(1.03); // 悬停时放大
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2); // 悬停时阴影增强
      }

      .el-image {
        width: 100%;
        display: block;
      }

      .tag-name {
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        padding: 10px;
        background-color: rgba(0, 0, 0, 0.5);
        color: white;
        font-size: 14px;
        text-align: center;
        opacity: 0;
        transition: opacity 0.3s;
      }

      &:hover .tag-name {
        opacity: 1;
      }
    }
  }
}

</style>
