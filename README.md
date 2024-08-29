# album 设计文档
# 代码仓库

GitHub：[album/spring-boot-init-template at main · AtwoodPa/album · GitHub](https://github.com/AtwoodPa/album/tree/main/spring-boot-init-template)

# 部署方案

**部署地址**： http://43.142.255.148:3000

```xml
# 管理员
账号：admin
密码：123456
# 普通用户
账号：user
密码：123456
账号：panpan
密码：123456
账号：karl
密码：123456
```

云服务器：腾讯云 - 轻量云

![](http://imgcom.static.suishenyun.net/202408291014953.png)

静态代理：**Nginx**

配置文件仅展示重要部分配置

```xml
 server {
        listen 3000;
        server_name localhost;

        location / {
            root /data/album/dist;
            index index.html index.htm;
            try_files $uri $uri/ /index.html;
        }

        location /api/ {
            proxy_set_header Host $http_host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header REMOTE-HOST $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_pass http://localhost:38080/api/; # 请求转发
        }

        location ~* \.(css|js|gif|jpg|jpeg|png|bmp|swf)$ {
            root /data/album/dist;  # 指定静态文件所在目录
            expires 30d;
        }
    }
```

# 技术选型

- 后端
  - **Java 17**
  - **SpringBoot 3.2.5**
  - **MySQL**
  - **MyBatis**
  - **Sa-Token**
  - **Redis**
  - **Minio**
- 前端
  - **Vue**
  - **Element-UI**

# 数据表设计

用户表

```sql
CREATE TABLE `t_user` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_account` varchar(64) NOT NULL COMMENT '用户账号',
  `user_password` varchar(64) NOT NULL COMMENT '用户密码',
  `user_email` varchar(255) NOT NULL COMMENT '用户邮箱',
  `user_login_num` int(11) NOT NULL DEFAULT '0' COMMENT '用户连续登录失败次数',
  `user_name` varchar(64) DEFAULT NULL COMMENT '用户昵称',
  `user_avatar` varchar(255) DEFAULT NULL COMMENT '用户头像',
  `user_role` varchar(32) DEFAULT NULL COMMENT '用户角色（admin/user）',
  `user_state` tinyint(4) NOT NULL DEFAULT '0' COMMENT '用户状态（0表示启用，1表示禁用）',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除（0表示未删除，1表示已删除）',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
```

日志表

```sql
CREATE TABLE `t_log` (
  `log_id` bigint(20) NOT NULL COMMENT '日志ID',
  `log_uri` varchar(255) DEFAULT NULL COMMENT '日志接口URI',
  `log_description` varchar(255) DEFAULT NULL COMMENT '日志操作描述',
  `log_operator` int(11) DEFAULT NULL COMMENT '日志操作类型（0其他1增2删3查4改5导入6导出）',
  `log_request_method` varchar(10) DEFAULT NULL COMMENT '日志请求方法（RESTFul风格）',
  `log_method` varchar(255) DEFAULT NULL COMMENT '日志方法名称',
  `log_user_id` bigint(20) DEFAULT NULL COMMENT '日志操作用户ID',
  `log_ip` varchar(128) DEFAULT NULL COMMENT '日志操作用户IP',
  `log_location` varchar(255) DEFAULT NULL COMMENT '日志操作用户地点',
  `log_param` varchar(2048) DEFAULT NULL COMMENT '日志操作参数',
  `log_result` int(11) DEFAULT NULL COMMENT '日志操作结果（0正常1异常）',
  `log_json` varchar(2048) DEFAULT NULL COMMENT '日志响应内容',
  `log_time` bigint(20) DEFAULT NULL COMMENT '日志接口访问耗时',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除（0表示未删除，1表示已删除）',
  PRIMARY KEY (`log_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
```

照片表

```sql
CREATE TABLE `t_photo` (
  `photo_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '照片ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `photo_url` varchar(255) NOT NULL COMMENT '照片存储URL',
  `photo_description` varchar(255) DEFAULT NULL COMMENT '照片描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除（0表示未删除，1表示已删除）',
  PRIMARY KEY (`photo_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
```

标签表

```sql
CREATE TABLE `t_tag` (
  `tag_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `tag_name` varchar(64) NOT NULL COMMENT '标签名',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`tag_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
```

照片标签表

```sql
CREATE TABLE `t_photo_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `photo_id` bigint(20) NOT NULL COMMENT '照片ID',
  `tag_id` bigint(20) NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`id`),
  KEY `idx_id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2007302147 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
```

# 接口设计

## 用户认证接口设计

#### 登录接口

- **接口路径**: `/auth/login`
- **请求方式**: `POST`
- **描述**: 用户登录。
- **请求参数**:
  - `authLoginDto` (body) - 用户登录信息，包含以下字段：
    - `username` (String): 用户名
    - `password` (String): 密码
- **响应结果**:
  - `200 OK`: 登录成功，返回用户信息和令牌

#### 获取登录信息接口

- **接口路径**: `/auth/info`
- **请求方式**: `GET`
- **描述**: 获取当前登录用户的信息。
- **请求参数**: 无
- **响应结果**:
  - `200 OK`: 返回当前登录用户的信息。

#### 退出登录接口

- **接口路径**: `/auth/logout`
- **请求方式**: `DELETE`
- **描述**: 用户退出登录。
- **请求参数**: 无
- **响应结果**:
  - `200 OK`: 退出成功。

## 管理员用户管理接口设计

#### 分页查询用户信息

- **接口路径**: `/admin/user/page`
- **请求方式**: `GET`
- **描述**: 管理员分页查询用户信息。
- **请求参数**:
  - `adminUserPageDto` (query) - 用户信息查询条件，包含用户状态、用户名等过滤条件。
  - `pageModel` (query) - 分页模型，包含页码和每页条数。
- **响应结果**:
  - `200 OK`: 返回分页查询结果，包括用户信息的分页列表。

#### 添加用户

- **接口路径**: `/admin/user/add`
- **请求方式**: `POST`
- **描述**: 管理员添加新用户。
- **请求参数**:
  - `adminUserAddDto` (body) - 被添加用户信息，包含用户名、邮箱、角色等字段。
- **响应结果**:
  - `200 OK`: 添加成功，返回提示信息。

#### 根据ID删除用户

- **接口路径**: `/admin/user/delete/{id}`
- **请求方式**: `DELETE`
- **描述**: 管理员根据用户ID删除用户。
- **请求参数**:
  - `id` (path) - 被删除用户的ID。
- **响应结果**:
  - `200 OK`: 删除成功，返回提示信息。

#### 修改用户信息

- **接口路径**: `/admin/user/update/info`
- **请求方式**: `PUT`
- **描述**: 管理员修改用户信息。
- **请求参数**:
  - `adminUserUpdateInfoDto` (body) - 被修改后的用户信息，包含用户名、邮箱、角色等字段。
- **响应结果**:
  - `200 OK`: 修改信息成功，返回提示信息。

#### 修改用户状态

- **接口路径**: `/admin/user/update/state`
- **请求方式**: `PUT`
- **描述**: 管理员修改用户状态。
- **请求参数**:
  - `adminUserUpdateStateDto` (body) - 被修改用户的ID及新状态。
- **响应结果**:
  - `200 OK`: 修改状态成功，返回提示信息。

#### 重置用户密码

- **接口路径**: `/admin/user/reset/password`
- **请求方式**: `PUT`
- **描述**: 管理员重置用户密码。
- **请求参数**:
  - `adminUserResetPasswordDto` (body) - 被重置密码信息，包含用户ID及新密码。
- **响应结果**:
  - `200 OK`: 重置密码成功，返回提示信息。

#### 导出用户表格

- **接口路径**: `/admin/user/export`
- **请求方式**: `GET`
- **描述**: 管理员导出用户表格为Excel文件。
- **请求参数**: 无
- **响应结果**:
  - `200 OK`: 导出表格成功，返回表格文件下载。

## 用户接口设计

#### 更新账号

- **接口路径**: `/user/update/account`
- **请求方式**: `PUT`
- **描述**: 用户更新自身的账号信息。
- **请求参数**:
  - `userUpdateAccountDto` (body) - 用户更新账号Dto类，包含新账号名称。
- **响应结果**:
  - `200 OK`: 更新成功，返回提示信息。

#### 更新名称

- **接口路径**: `/user/update/name`
- **请求方式**: `PUT`
- **描述**: 用户更新自身的名称。
- **请求参数**:
  - `userUpdateNameDto` (body) - 用户更新名称Dto类，包含新名称。
- **响应结果**:
  - `200 OK`: 更新成功，返回提示信息。

#### 更新密码

- **接口路径**: `/user/update/password`
- **请求方式**: `PUT`
- **描述**: 用户更新自身的密码。
- **请求参数**:
  - `userUpdatePasswordDto` (body) - 用户更新密码Dto类，包含旧密码、新密码和确认新密码。
- **响应结果**:
  - `200 OK`: 更新成功，返回提示信息，并提示用户重新登录。

#### 更新头像

- **接口路径**: `/user/update/avatar`
- **请求方式**: `PUT`
- **描述**: 用户更新自身的头像。
- **请求参数**:
  - `userUpdateAvatarDto` (body) - 用户更新头像Dto类，包含上传的头像文件。头像文件要求不超过1MB，仅支持png、jpg和jpeg格式。
- **响应结果**:
  - `200 OK`: 更新成功，返回提示信息。

## 日志管理接口设计

#### 分页查询日志信息

- **接口路径**: `/admin/log/page`
- **请求方式**: `GET`
- **描述**: 管理员分页查询日志信息，根据指定的条件和分页模型返回日志数据。
- **请求参数**:
  - `adminLogPageDto` (query) - 日志信息查询条件。
  - `pageModel` (query) - 分页模型参数。
- **响应结果**:
  - `200 OK`: 返回分页查询结果，包含日志信息的分页数据。

#### 删除日志信息

- **接口路径**: `/admin/log/delete/{id}`
- **请求方式**: `DELETE`
- **描述**: 管理员根据日志ID删除指定的日志信息。
- **请求参数**:
  - `id` (path) - 被删除的日志信息ID。
- **响应结果**:
  - `200 OK`: 删除成功，返回提示信息。

#### 清空日志信息

- **接口路径**: `/admin/log/clear`
- **请求方式**: `DELETE`
- **描述**: 管理员清空所有日志信息。
- **请求参数**: 无。
- **响应结果**:
  - `200 OK`: 清空成功，返回提示信息。

#### 导出日志表格

- **接口路径**: `/admin/log/export`
- **请求方式**: `GET`
- **描述**: 管理员导出所有日志信息为Excel表格文件。
- **请求参数**: 无。
- **响应结果**:
  - `200 OK`: 导出成功，返回下载链接或触发下载操作。

## 照片管理接口设计

#### 添加照片

- **接口路径**: `/admin/photo/addPhotoWithTags`
- **请求方式**: `POST`
- **描述**: 管理员在添加照片的同时，可以将标签与照片关联。
- **请求参数**:
  - `request` (body) - 请求体，包含照片信息和关联标签ID。
    - `photo` (object) - 照片信息对象，包含照片的详细信息。
    - `selectedTagIds` (array of Long) - 被选中的标签ID列表，用于将标签与照片关联。
- **响应结果**:
  - `200 OK`: 添加成功，返回成功提示信息。

#### 图片上传OSS

- **接口路径**: `/admin/photo/upload/photo`
- **请求方式**: `POST`
- **描述**: 上传照片至OSS（默认存储策略采用Minio，支持阿里云、腾讯云），并返回照片的URL。
- **请求参数**:
  - `file` (part) - 上传的照片文件。
- **请求头**:
  - `Content-Type` - `multipart/form-data`
- **响应结果**:
  - `200 OK`: 返回照片的URL，如果上传成功。
  - `400 Bad Request`: 文件大小超出限制或文件类型不匹配。

#### 分页查询照片信息

- **接口路径**: `/admin/photo/page`
- **请求方式**: `GET`
- **描述**: 管理员分页查询照片信息，根据指定条件和分页模型返回照片数据。
- **请求参数**:
  - `adminPhotoPageDto` (query) - 照片信息查询条件。
  - `pageModel` (query) - 分页模型参数。
- **响应结果**:
  - `200 OK`: 返回分页查询结果，包含照片信息的分页数据。

#### 删除照片

- **接口路径**: `/admin/photo/delete/{id}`
- **请求方式**: `DELETE`
- **描述**: 管理员根据照片ID删除指定的照片信息。
- **请求参数**:
  - `id` (path) - 被删除照片的ID。
- **响应结果**:
  - `200 OK`: 删除成功，返回提示信息。

#### 修改照片信息

- **接口路径**: `/admin/photo/update`
- **请求方式**: `PUT`
- **描述**: 管理员修改照片的信息。
- **请求参数**:
  - `request` (body) - 修改后的照片信息。
- **响应结果**:
  - `200 OK`: 修改成功，返回提示信息。

## 标签管理接口设计

#### 页查询标签信息

- **接口路径**: `/admin/tag/page`
- **请求方式**: `GET`
- **描述**: 管理员分页查询标签信息。
- **请求参数**:
  - `adminTagPageDto` (query) - 标签信息查询条件。
  - `pageModel` (query) - 分页模型。
- **响应结果**:
  - `200 OK`: 返回分页查询结果，包含标签信息。

#### 添加标签

- **接口路径**: `/admin/tag/add`
- **请求方式**: `POST`
- **描述**: 管理员添加标签。
- **请求参数**:
  - `adminTagAddDto` (body) - 被添加标签的信息。
- **响应结果**:
  - `200 OK`: 添加成功，返回成功提示信息。

#### 删除标签

- **接口路径**: `/admin/tag/delete/{id}`
- **请求方式**: `DELETE`
- **描述**: 管理员根据ID删除标签。
- **请求参数**:
  - `id` (path) - 被删除标签的ID。
- **响应结果**:
  - `200 OK`: 删除成功，返回成功提示信息。

#### 修改标签信息

- **接口路径**: `/admin/tag/update`
- **请求方式**: `PUT`
- **描述**: 管理员修改标签信息。
- **请求参数**:
  - `adminTagUpdateDto` (body) - 被修改后的标签信息。
- **响应结果**:
  - `200 OK`: 修改成功，返回成功提示信息。

#### 获取所有标签接口

**接口路径**: `/tag/allTags`

**描述**: 获取所有标签，支持缓存（Redis）。

**请求方式**: `GET`

**请求参数**: 无

**响应结果**:

- `200 OK`: 返回所有标签列表。如果缓存中存在标签，直接返回缓存中的数据；否则从数据库中查询并更新缓存。

**缓存策略**:

- **缓存键**: `all_tags`
- **缓存有效期**: 10分钟
