import {defineStore} from 'pinia'

export const useOrderStore = defineStore('order', {
  state: () => ({
    counter: 1,
    activeId: 0,
    orders: [{
      id: 0,
      items: [],
      total: '',
      discount: '',
    }]
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
    getActiveOrder: (state) => state.orders.at(state.activeId),
  },

  actions: {
    /**
     * Add item to active order
     *
     * @param {Object} item
     */
    addItem(item) {
      // Get activeOrder reference
      const activeOrder = this.orders.at(this.activeId)

      // Check if item is already exist in order's item list
      const index = activeOrder.items.findIndex(i => i.id === item.id)

      if (index !== -1) {
        // If item is existed, edit item with increased quantity
        this.editItem(index, {
          ...item,
          quantity: `${parseInt(activeOrder.items.at(index).quantity) + 1}`,
          totalPrice: '',
          note: activeOrder.items.at(index).note,
        })
      } else {
        // Else, just add item
        this.orders.at(this.activeId).items.push({
          ...item,
          quantity: '1',
          totalPrice: item.actualPrice,
          note: ''
        })
      }
    },

    /**
     * Edit item to active order
     *
     * @param {number} index
     * @param {Object} item
     */
    editItem(index, item) {
      // Splice old item and push new item in
      this.orders.at(this.activeId).items.splice(index, 1, item)
    },

    /**
     * Remove item from active order
     *
     * @param {Number} index
     */
    removeItem(index) {
      this.orders.at(this.activeId).items.splice(index, 1)
    },

    /**
     * Create new order
     */
    createOrder() {
      // Add new order
      this.orders.push({
        id: this.counter++,
        items: [],
        total: '0',
        discount: '0',
        discountUnit: '%',
      })
      // Added order will be active
      this.activeId = this.orders.at(-1).id

      return this.activeId
    },

    /**
     * Remove order at index
     *
     * @param {number} index
     * @returns {Object}
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
     * @param {number} id
     */
    setActive(id) {
      this.activeId = id
    }
  }
})
