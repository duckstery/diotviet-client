import {defineStore} from 'pinia'

export const useEnvStore = defineStore('env', {
  state: () => ({
    language: '',
    display: '',
    optimize: '',
  }),

  actions: {
    /**
     * Set env
     *
     * @param {string} key
     * @param {*} value
     */
    setEnv(key, value) {
      this[key] = value
    },
  }
})
