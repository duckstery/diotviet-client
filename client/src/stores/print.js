import {defineStore} from 'pinia'

export const usePrintStore = defineStore('print', {
  state: () => ({
    tags: [],
    template: '',
  }),

  getters: {
    /**
     * Get orders size
     *
     * @param state
     * @returns {number}
     */
    getTags: (state) => state.tags,

    /**
     * Get items in order
     *
     * @param state
     * @returns {*}
     */
    getTemplate: (state) => state.template,
  }
})
