import {defineStore} from 'pinia'

export const useProductStore = defineStore('product', {
  state: () => ({
    items: [],
  }),

  actions: {
    /**
     * Get orders size
     *
     * @param {string} query
     * @returns {array}
     */
    find(query) {
      // Preprocess query string
      const q = query.toLowerCase()
      return this.items.filter(item => item.code.toLowerCase().includes(q) || item.title.toLowerCase().includes(query))
    },
  }
})
