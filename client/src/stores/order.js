import { defineStore } from 'pinia'

export const useOrderStore = defineStore('order', {
  state: () => ({
    counter: 1,
    activeId: 0,
    orders: [{id: 0, items: []}]
  }),

  getters: {
    /**
     * Get orders size
     *
     * @param state
     * @returns {number}
     */
    getTotalSize: (state) => state.orders.length,

    /**
     * Get items in order
     *
     * @param state
     * @returns {*}
     */
    getOrders: (state) => state.orders.map(order => order.id),

    /**
     * Get order selected id
     *
     * @param state
     * @returns {*}
     */
    getActiveId: (state) => state.activeId,

    /**
     * Get items in order
     *
     * @param state
     * @returns {*}
     */
    getActiveOrder: (state) => state.orders.at(state.activeId)
  },

  actions: {
    /**
     * Add item to active order
     *
     * @param item
     */
    addItem(item) {
      this.orders.at(this.activeId).items.push(item)
    },

    /**
     * Remove item out of order
     *
     * @param index
     */
    createOrder() {
      // Add new order
      this.orders.push({id: this.counter++, items: []})
      // Added order will be active
      this.activeId = this.orders.at(-1).id

      return this.activeId
    },

    /**
     * Set new order
     *
     * @param index
     */
    removeOrder(index) {
      // Get index of removed item
      const removedOrder = this.orders.splice(index, 1).at(0)
      // Check if index is 0 (first item)
      if (removedOrder.id === this.activeId) {
        // Set tab to new first item
        this.activeId = this.orders.at(0).id
      }

      return removedOrder
    },

    /**
     * Set active order
     *
     * @param id
     */
    setActive(id) {
      this.activeId = id
    }
  }
})
