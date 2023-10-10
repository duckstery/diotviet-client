import {boot} from 'quasar/wrappers'
import axios, {CanceledError, AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig} from 'axios'
import Qs from "qs";

// *************************************************
// Typed
// *************************************************

export interface LocalAxiosRequestConfig<D = any> extends AxiosRequestConfig<D> {
  loading?: boolean,
}

export interface LocalAxiosInternalRequestConfig<D = any> extends InternalAxiosRequestConfig<D> {
  loading?: boolean,
}

// Response shape
export type LocalAxiosResponse = {
  success: boolean,
  message: string,
  payload: any
}

// Local axios shape
export interface LocalAxios extends AxiosInstance {
  request<T = any, R = AxiosResponse<LocalAxiosResponse>, D = any>(config: LocalAxiosRequestConfig<D>): Promise<R>
  get<T = any, R = AxiosResponse<LocalAxiosResponse>, D = any>(url: string, config?: LocalAxiosRequestConfig<D>): Promise<R>
  delete<T = any, R = AxiosResponse<LocalAxiosResponse>, D = any>(url: string, config?: LocalAxiosRequestConfig<D>): Promise<R>
  head<T = any, R = AxiosResponse<LocalAxiosResponse>, D = any>(url: string, config?: LocalAxiosRequestConfig<D>): Promise<R>
  options<T = any, R = AxiosResponse<LocalAxiosResponse>, D = any>(url: string, config?: LocalAxiosRequestConfig<D>): Promise<R>
  post<T = any, R = AxiosResponse<LocalAxiosResponse>, D = any>(url: string, data?: D, config?: LocalAxiosRequestConfig<D>): Promise<R>
  put<T = any, R = AxiosResponse<LocalAxiosResponse>, D = any>(url: string, data?: D, config?: LocalAxiosRequestConfig<D>): Promise<R>
  patch<T = any, R = AxiosResponse<LocalAxiosResponse>, D = any>(url: string, data?: D, config?: LocalAxiosRequestConfig<D>): Promise<R>
  postForm<T = any, R = AxiosResponse<LocalAxiosResponse>, D = any>(url: string, data?: D, config?: LocalAxiosRequestConfig<D>): Promise<R>
  putForm<T = any, R = AxiosResponse<LocalAxiosResponse>, D = any>(url: string, data?: D, config?: LocalAxiosRequestConfig<D>): Promise<R>
  patchForm<T = any, R = AxiosResponse<LocalAxiosResponse>, D = any>(url: string, data?: D, config?: LocalAxiosRequestConfig<D>): Promise<R>
}

// *************************************************
// Implementation
// *************************************************

// Create axios instance
const axiosInstance: LocalAxios = axios.create({
  paramsSerializer: {
    serialize: params => Qs.stringify(params, {arrayFormat: 'repeat'})
  }
})

// Be careful when using SSR for cross-request state pollution
// due to creating a Singleton instance here;
// If any client changes this (global) instance, it might be a
// good idea to move this instance creation inside the
// "export default () => {}" function below (which runs individually
// for each client)
axiosInstance.interceptors.response.use(function (response) {
  // Any status code that lie within the range of 2xx cause this function to trigger
  // Do something with response data
  return response;
}, function (error) {
  // Do something if error is a CanceledError
  if (error instanceof CanceledError) {
    // @ts-ignore
    throw new Error(error.config.signal.reason)
  }
  // Any status codes that falls outside the range of 2xx cause this function to trigger
  // Do something with response error
  return Promise.reject(error);
})

// Config Axios
axiosInstance.defaults.baseURL = `${process.env.API_BASE_URL}${process.env.API_PREFIX_PATH}`
// @ts-ignore
axiosInstance.defaults.headers = {
  'Accept': 'application/json;charset=utf-8',
  'Cache-Control': 'no-store',
  'Content-Type': 'application/json;charset=utf-8'
}
axiosInstance.defaults.withCredentials = false

export default boot(({app}) => {
  // for use inside Vue files (Options API) through this.$axios and this.$api

  app.config.globalProperties.$axios = axiosInstance
  // ^ ^ ^ this will allow you to use this.$axios (for Vue Options API form)
  //       so you won't necessarily have to import axios in each vue file

  app.config.globalProperties.$api = axiosInstance
  // ^ ^ ^ this will allow you to use this.$api (for Vue Options API form)
  //       so you won't necessarily have to import axios in each vue file
})

export {axiosInstance as axios}
