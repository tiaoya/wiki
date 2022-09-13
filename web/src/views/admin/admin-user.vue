<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-input v-model:value="param.loginName" placeholder="登陆名">
            </a-input>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleQuery({page: 1, size: pagination.pageSize})">
              查询
            </a-button>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="add()">
              新增
            </a-button>
          </a-form-item>
        </a-form>
      </p>

      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="users"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <template #cover="{ text:cover }">
          <img v-if="cover" :src="cover" alt="avatar" style="width:95px;height:95px">
        </template>
        <template v-slot:action="{text,record}">
         <a-space size="small">
           <a-button type="primary" @click="edit(record)">
             编辑
           </a-button>
           <a-popconfirm
              title="删除后不可恢复,确认删除"
              ok-text="是"
              cancel-text="否"
              @confirm = "handleDelete(record.id)"
           >
             <a-button type="danger">
               删除
             </a-button>
           </a-popconfirm>
         </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>

  <a-modal
      title="用户表单"
      v-model:visible = "modalVisible"
      :confirm-loading="modalLoading"
      @ok="handleModalOK"
  >
      <a-form :model="user" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="登陆名">
          <a-input v-model:value="user.loginName" :disabled="!!user.id"/>
        </a-form-item>
        <a-form-item label="昵称">
          <a-input v-model:value="user.name" />
        </a-form-item>
        <a-form-item label="密码" v-show="!user.id">
          <a-input v-model:value="user.password"/>
        </a-form-item>
      </a-form>
  </a-modal>
</template>

<script lang="ts">
// import { SmileOutlined, DownOutlined } from '@ant-design/icons-vue';
import { defineComponent,onMounted,ref } from 'vue';
import axios from 'axios';
import { message } from 'ant-design-vue';
import {Tool} from "@/util/tool";


export default defineComponent({
  name:'AdminUser',

  setup() {

    const param = ref();
    param.value = {};
    const users = ref();
    const pagination = ref({
      current:1,
      pageSize:10,
      total:0
  });
  const loading = ref(false);

    const columns = [
      {
        title: '登陆名',
        dataIndex: 'loginName'
      },
      {
        title: '名称',
        dataIndex: 'name'
      },
      {
        title: '密码',
        dataIndex: 'password'
      },
      {
        title: 'Action',
        key: 'action',
        slots: { customRender: 'action' }
      }
    ];

    /**
     * 数据查询
     **/
    const handleQuery = (params: any) => {
      loading.value = true;
      // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
      users.value = [];
      axios.get("/user/list",{
        params:{
          page: params.page,
          size: params.size,
          loginName: param.value.loginName
        }
      }).then((response)=>{

        loading.value = false;
        const data = response.data;
        if (data.success){
          users.value = data.content.list;

          // 重置分页按钮
          pagination.value.current = params.page;
          pagination.value.total = data.content.total;
        }else {
          message.error(data.message);
        }
      });
    };

    /**
     * 表格点击页码时触发
     */
    const handleTableChange = (pagination: any) => {
      console.log("看看自带的分页参数都有啥：" + pagination);
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      });
    };


    //-------------- 表单 ------------------
    /**
     * 数组，[100, 101]对应：前端开发 / Vue
     */
    const user = ref();
    const modalVisible = ref(false);
    const modalLoading = ref(false);

    const handleModalOK= () => {
      modalLoading.value = true;

      axios.post("/user/save",user.value).then((response)=>{

        modalLoading.value = false;
        const data = response.data;
        if (data.success){
          modalVisible.value = false;

          // 重新加载列表
          handleQuery({
            page:pagination.value.current,
            size:pagination.value.pageSize
          });
        }else {
          message.error(data.message);
        }
      });
    };

    // 编辑
    const edit = (record:any) =>{
      modalVisible.value = true;
      user.value = Tool.copy(record);

    }

    // 新增
    const add = () =>{
      modalVisible.value = true;
      user.value = {};
    }

    // 删除
    const handleDelete = (id:number) =>{
      axios.delete("/user/delete/"+id).then((response)=>{
        const data = response.data; // date = commonResp
        if (data.success){
          // 重新加载列表
          handleQuery({
            page:pagination.value.current,
            size:pagination.value.pageSize
          });
        } else {
          message.error(data.message);
        }
      });
    };

    // const level1 =  ref();
    // let categorys: any;
    //
    // /**
    //  * 查询所有分类
    //  **/
    // const handleQueryCategory = () => {
    //   loading.value = true;
    //   axios.get("/category/all").then((response) => {
    //     loading.value = false;
    //     const data = response.data;
    //     if (data.success) {
    //       categorys = data.content;
    //       console.log("原始数组：", categorys);
    //
    //       level1.value = [];
    //       level1.value = Tool.array2Tree(categorys, 0);
    //       console.log("树形结构：", level1.value);
    //
    //       // 加载完分类后，再加载电子书，否则如果分类树加载很慢，则电子书渲染会报错
    //       handleQuery({
    //         page: 1,
    //         size: pagination.value.pageSize,
    //       });
    //     } else {
    //       message.error(data.message);
    //     }
    //   });
    // };
    //
    // const getCategoryName = (cid:number) => {
    //   // console.log(cid)
    //   let result = "";
    //   categorys.forEach((item: any) => {
    //     if (item.id === cid) {
    //       // return item.name; // 注意，这里直接return不起作用
    //       result = item.name;
    //     }
    //   });
    //   return result;
    // };
    //


    onMounted(() => {
      handleQuery({
        page:1,
        size:pagination.value.pageSize
      });
    });

    return{
      param,
      users,
      pagination,
      columns,
      loading,
      handleTableChange,
      handleQuery,

      edit,
      add,

      user,
      modalVisible,
      modalLoading,
      handleModalOK,
      handleDelete,

    }
  }
});

</script>
