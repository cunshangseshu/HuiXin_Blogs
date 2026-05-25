<template>
  <div class="admin-process-page">
    <div class="card border-0 shadow-sm">
      <div class="card-body p-0">
        <table class="table table-hover align-middle mb-0">
          <thead class="table-light">
            <tr>
              <th scope="col" class="ps-4">申请人</th>
              <th scope="col">申请理由</th>
              <th scope="col">提交时间</th>
              <th scope="col">状态</th>
              <th scope="col" class="text-end pe-4">操作批复</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td colspan="5" class="text-center py-5 text-muted">拼命加载申请记录中...</td>
            </tr>
            <tr v-else-if="!records.length">
              <td colspan="5" class="text-center py-5 text-muted">暂无任何用户的入驻申请哦。</td>
            </tr>
            <tr v-else v-for="record in records" :key="record.id">
              <td class="ps-4">
                <div class="d-flex align-items-center gap-2">
                  <img :src="record.avatarUrl || defaultAvatar" width="32" height="32" class="rounded-circle" alt="">
                  <div class="fw-semibold">{{ record.nickname || record.username }}</div>
                </div>
              </td>
              <td style="max-width: 300px;" class="text-truncate" :title="record.applyReason">
                {{ record.applyReason }}
              </td>
              <td>{{ formatDate(record.createTime) }}</td>
              <td>
                <span class="badge" :class="statusBadgeMap[record.status].class">
                  {{ statusBadgeMap[record.status].text }}
                </span>
              </td>
              <td class="text-end pe-4">
                <button 
                  v-if="record.status === 0"
                  class="btn btn-sm btn-outline-success me-2" 
                  @click="auditApply(record.id, 1)"
                >同意</button>
                <button 
                  v-if="record.status === 0"
                  class="btn btn-sm btn-outline-danger" 
                  @click="auditApply(record.id, 2)"
                >驳回</button>
                <span v-else class="text-muted small">已经审批结束</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
// TODO: 等后端对应的管理员专属权限 API 接口写好后，这里接入 Axios 逻辑
import { formatDate } from '@/utils/format'
import { DEFAULT_AVATAR as defaultAvatar } from '@/utils/constants'

// 模拟前端展示的数据（因为你目前还在建表，我就打点假数据让你先看看骨感和排版效果！）
const loading = ref(false)
const records = ref([
  { id: 1, username: '小萌新', applyReason: '想要发布自己学习 Spring Boot 第十天的长文感悟，请同意呀！', status: 0, createTime: new Date().getTime() },
  { id: 2, username: '张三', applyReason: '我原来有技术博客想搬家过来记录心路历程。', status: 1, createTime: new Date().getTime() - 86400000 },
  { id: 3, username: '水军404', applyReason: '加VX带发财...', status: 2, createTime: new Date().getTime() - 96400000 }
])

const statusBadgeMap = {
  0: { text: '待审批 (Pending)', class: 'bg-warning text-dark' },
  1: { text: '已通过 (Approved)', class: 'bg-success' },
  2: { text: '已驳回 (Rejected)', class: 'bg-danger' }
}

async function auditApply(recordId, statusAction) {
  // TODO: 后续接入真实的 Admin API，发送附带系统发信内容的网络请求
  alert(`向后端发送把记录 ID：${recordId} 的状态置为：${statusAction === 1 ? '同意' : '驳回'}，并顺便发一条站内信系统通知此用户。`)
}

onMounted(() => {
  // loadAdminApplies() // 后续等后端 API 完工解除注释。
})
</script>

<style scoped>
.table-hover tbody tr:hover {
  background-color: var(--huixin-bg-soft);
}
</style>
