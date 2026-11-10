import http from './http'
import type { Task, TaskPayload } from '@/types/task'

export const taskApi = {
  async list(): Promise<Task[]> {
    const { data } = await http.get<Task[]>('/tasks')
    return data
  },

  async create(payload: TaskPayload): Promise<Task> {
    const { data } = await http.post<Task>('/tasks', payload)
    return data
  },

  async update(id: number, payload: TaskPayload): Promise<Task> {
    const { data } = await http.put<Task>(`/tasks/${id}`, payload)
    return data
  },

  async remove(id: number): Promise<void> {
    await http.delete(`/tasks/${id}`)
  }
}
