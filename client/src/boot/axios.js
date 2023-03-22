import {boot} from 'quasar/wrappers'
import axios, {CanceledError} from 'axios'

// Be careful when using SSR for cross-request state pollution
// due to creating a Singleton instance here;
// If any client changes this (global) instance, it might be a
// good idea to move this instance creation inside of the
// "export default () => {}" function below (which runs individually
// for each client)
axios.interceptors.response.use(function (response) {
  // Any status code that lie within the range of 2xx cause this function to trigger
  // Do something with response data
  return response;
}, function (error) {
  // Do something if error is a CanceledError
  if (error instanceof CanceledError) {
    throw new Error(error.config.signal.reason)
  }
  // Any status codes that falls outside the range of 2xx cause this function to trigger
  // Do something with response error
  return Promise.reject(error);
})

// Config Axios
axios.defaults.baseURL = `${process.env.API_BASE_URL}${process.env.API_PREFIX_PATH}`
axios.defaults.headers = {
  'Accept': 'application/json;charset=utf-8',
  'Cache-Control': 'no-store',
  'Content-Type': 'application/json;charset=utf-8'
}
axios.defaults.withCredentials = false

export default boot(({app}) => {
  // for use inside Vue files (Options API) through this.$axios and this.$api

  app.config.globalProperties.$axios = axios
  // ^ ^ ^ this will allow you to use this.$axios (for Vue Options API form)
  //       so you won't necessarily have to import axios in each vue file

  app.config.globalProperties.$api = axios
  // ^ ^ ^ this will allow you to use this.$api (for Vue Options API form)
  //       so you won't necessarily have to import axios in each vue file
})

export {axios}
