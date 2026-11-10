<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { CirclePlus, Delete, Edit, Refresh, Search } from '@element-plus/icons-vue'
import { taskApi } from '@/api/tasks'
import type { Task, TaskPayload, TaskStatus } from '@/types/task'

const statusMeta: Record<TaskStatus, { label: string; tag: 'info' | 'warning' | 'success' }> = {
  TODO: { label: '待办', tag: 'info' },
  IN_PROGRESS: { label: '进行中', tag: 'warning' },
  DONE: { label: '已完成', tag: 'success' }
}

const statusOptions = (Object.keys(statusMeta) as TaskStatus[]).map((value) => ({
  value,
  label: statusMeta[value].label
}))

const tasks = ref<Task[]>([])
const loading = ref(false)
const saving = ref(false)
const keyword = ref('')
const statusFilter = ref<TaskStatus | ''>('')
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const formRef = ref<FormInstance>()

const form = reactive<TaskPayload>({
  title: '',
  description: '',
  status: 'TODO'
})

const rules: FormRules<TaskPayload> = {
  title: [
    { required: true, message: '请输入任务标题', trigger: 'blur' },
    { max: 100, message: '标题不能超过 100 个字符', trigger: 'blur' }
  ],
  description: [{ max: 500, message: '描述不能超过 500 个字符', trigger: 'blur' }]
}

const filteredTasks = computed(() => {
  const term = keyword.value.trim().toLowerCase()

  return tasks.value.filter((task) => {
    const matchesKeyword =
      !term ||
      task.title.toLowerCase().includes(term) ||
      task.description.toLowerCase().includes(term)
    const matchesStatus = !statusFilter.value || task.status === statusFilter.value
    return matchesKeyword && matchesStatus
  })
})

const counts = computed(() =>
  tasks.value.reduce(
    (acc, task) => {
      acc.total += 1
      acc[task.status] += 1
      return acc
    },
    { total: 0, TODO: 0, IN_PROGRESS: 0, DONE: 0 } as Record<'total' | TaskStatus, number>
  )
)

async function loadTasks() {
  loading.value = true
  try {
    tasks.value = await taskApi.list()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '加载任务失败')
  } finally {
    loading.value = false
  }
}

function resetForm() {
  form.title = ''
  form.description = ''
  form.status = 'TODO'
  formRef.value?.clearValidate()
}

function openCreate() {
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

function openEdit(task: Task) {
  editingId.value = task.id
  form.title = task.title
  form.description = task.description
  form.status = task.status
  dialogVisible.value = true
  formRef.value?.clearValidate()
}

async function submitForm() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }

  saving.value = true
  try {
    const payload: TaskPayload = {
      title: form.title.trim(),
      description: form.description.trim(),
      status: form.status
    }

    if (editingId.value === null) {
      await taskApi.create(payload)
      ElMessage.success('任务已创建')
    } else {
      await taskApi.update(editingId.value, payload)
      ElMessage.success('任务已更新')
    }

    dialogVisible.value = false
    await loadTasks()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败')
  } finally {
    saving.value = false
  }
}

async function removeTask(task: Task) {
  try {
    await ElMessageBox.confirm(`确定删除任务「${task.title}」吗？`, '删除确认', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }

  try {
    await taskApi.remove(task.id)
    ElMessage.success('任务已删除')
    await loadTasks()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '删除失败')
  }
}

function formatDate(value: string) {
  return new Date(value).toLocaleString('zh-CN', { hour12: false })
}

onMounted(loadTasks)
</script>

<template>
  <section class="board">
    <div class="stats">
      <article class="stat stat--total">
        <span class="stat__label">全部任务</span>
        <strong class="stat__value">{{ counts.total }}</strong>
      </article>
      <article class="stat stat--todo">
        <span class="stat__label">待办</span>
        <strong class="stat__value">{{ counts.TODO }}</strong>
      </article>
      <article class="stat stat--progress">
        <span class="stat__label">进行中</span>
        <strong class="stat__value">{{ counts.IN_PROGRESS }}</strong>
      </article>
      <article class="stat stat--done">
        <span class="stat__label">已完成</span>
        <strong class="stat__value">{{ counts.DONE }}</strong>
      </article>
    </div>

    <div class="panel">
      <div class="toolbar">
        <div class="toolbar__filters">
          <el-input
            v-model="keyword"
            :prefix-icon="Search"
            placeholder="搜索标题或描述"
            clearable
            class="toolbar__search"
          />
          <el-select v-model="statusFilter" placeholder="全部状态" clearable class="toolbar__status">
            <el-option
              v-for="option in statusOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </div>

        <div class="toolbar__actions">
          <el-button :icon="Refresh" :loading="loading" @click="loadTasks">刷新</el-button>
          <el-button type="primary" :icon="CirclePlus" @click="openCreate">新建任务</el-button>
        </div>
      </div>

      <el-table v-loading="loading" :data="filteredTasks" row-key="id" class="task-table">
        <el-table-column prop="title" label="任务" min-width="240">
          <template #default="{ row }">
            <div class="task-title">
              <span class="task-title__text">{{ row.title }}</span>
              <span v-if="row.description" class="task-title__desc">{{ row.description }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusMeta[row.status as TaskStatus].tag" effect="light" round>
              {{ statusMeta[row.status as TaskStatus].label }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            <span class="muted">{{ formatDate(row.createdAt) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="150" align="right" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" :icon="Delete" @click="removeTask(row)">删除</el-button>
          </template>
        </el-table-column>

        <template #empty>
          <el-empty description="暂无匹配的任务，试试新建一个吧" />
        </template>
      </el-table>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="editingId === null ? '新建任务' : '编辑任务'"
      width="520px"
      destroy-on-close
      @closed="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        @submit.prevent="submitForm"
      >
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="form.title"
            maxlength="100"
            show-word-limit
            placeholder="例如：完成首页开发"
          />
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            maxlength="500"
            show-word-limit
            placeholder="补充任务的背景、目标或验收标准"
          />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" class="full-width">
            <el-option
              v-for="option in statusOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<style scoped>
.board {
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-width: 0;
}

.stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.stat {
  --accent: #3b82f6;
  position: relative;
  overflow: hidden;
  padding: 20px 22px;
  border: 1px solid rgba(148, 163, 184, 0.18);
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 12px 32px -20px rgba(15, 23, 42, 0.35);
  backdrop-filter: blur(10px);
}

.stat::before {
  content: '';
  position: absolute;
  inset: 0 auto 0 0;
  width: 4px;
  background: var(--accent);
}

.stat__label {
  display: block;
  color: #64748b;
  font-size: 13px;
}

.stat__value {
  display: block;
  margin-top: 8px;
  color: #0f172a;
  font-size: 30px;
  font-weight: 700;
  line-height: 1;
}

.stat--todo {
  --accent: #94a3b8;
}

.stat--progress {
  --accent: #f59e0b;
}

.stat--done {
  --accent: #10b981;
}

.panel {
  min-width: 0;
  padding: 24px;
  border: 1px solid rgba(148, 163, 184, 0.16);
  border-radius: 24px;
  background: #ffffff;
  box-shadow: 0 28px 60px -36px rgba(15, 23, 42, 0.45);
}

.toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.toolbar__filters,
.toolbar__actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.toolbar__search {
  width: 280px;
}

.toolbar__status {
  width: 150px;
}

.task-title {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 4px 0;
}

.task-title__text {
  color: #0f172a;
  font-weight: 600;
}

.task-title__desc {
  overflow: hidden;
  color: #94a3b8;
  font-size: 12px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.muted {
  color: #94a3b8;
  font-size: 13px;
}

.task-table {
  width: 100%;
}

.task-table :deep(th.el-table__cell) {
  background: #f8fafc;
  color: #64748b;
  font-weight: 600;
}

.task-table :deep(td.el-table__cell) {
  padding: 10px 0;
}

.full-width {
  width: 100%;
}

:deep(.el-dialog) {
  border-radius: 18px;
}

:deep(.el-button--primary) {
  --el-button-bg-color: #2563eb;
  --el-button-border-color: #2563eb;
  --el-button-hover-bg-color: #1d4ed8;
  --el-button-hover-border-color: #1d4ed8;
}

@media (max-width: 900px) {
  .stats {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .panel {
    padding: 16px;
    border-radius: 20px;
  }

  .toolbar {
    align-items: stretch;
  }

  .toolbar__filters {
    flex-wrap: wrap;
  }

  .toolbar__search {
    width: 100%;
  }

  .toolbar__status {
    flex: 1;
  }

  .toolbar__actions {
    justify-content: flex-end;
  }
}
</style>
