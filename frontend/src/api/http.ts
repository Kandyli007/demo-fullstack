import axios, { AxiosError } from 'axios'

interface ErrorBody {
  message?: string
}

const http = axios.create({
  baseURL: '/api',
  timeout: 10_000
})

http.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ErrorBody>) => {
    const message =
      error.response?.data?.message ??
      (error.code === 'ECONNABORTED'
        ? '请求超时，请稍后重试'
        : '网络请求失败，请确认后端服务已启动')

    return Promise.reject(new Error(message))
  }
)

export default http
