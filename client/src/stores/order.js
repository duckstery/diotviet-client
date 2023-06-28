import {defineStore} from 'pinia'
import {util} from 'src/boot'

export const useOrderStore = defineStore('order', {
  state: () => ({
    counter: 1,
    activeId: 0,
    activeIndex: 0,
    orders: [{
      id: 0,
      customer: null,
      items: [],
      provisionalAmount: '0',
      discount: '0',
      discountUnit: '%',
      paymentAmount: '0',
      note: '',
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
    getActiveOrder: (state) => state.orders.at(state.activeIndex),

    /**
     * Find index of active order in orders
     *
     * @param state
     * @returns {number}
     */
    getActiveIndex: (state) => state.activeIndex,

    /**
     * Get active customer
     *
     * @param state
     * @return {object}
     */
    getActiveCustomer: (state) => state.orders.at(state.activeIndex).customer
  },

  actions: {
    /**
     * Set customer for active order
     *
     * @param customer
     */
    setCustomer(customer) {
      // Get activeOrder reference
      this.orders.at(this.getActiveIndex).customer = util.isUnset(customer) ? null : {...customer}
    },

    /**
     * Add item to active order
     *
     * @param {Object} item
     */
    addItem(item) {
      // Get activeOrder reference
      const activeOrder = this.orders.at(this.getActiveIndex)

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
        activeOrder.items.push({
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
      // Get order
      const order = this.orders.at(this.getActiveIndex);
      // Splice old item and push new item in
      order.items.splice(index, 1, item)
      // Update provisional amount
      this.updateProvisionalAmount()
    },

    /**
     * Remove item from active order
     *
     * @param {Number} index
     */
    removeItem(index) {
      this.orders.at(this.getActiveIndex).items.splice(index, 1)
      // Update provisional amount
      this.updateProvisionalAmount()
    },

    /**
     * Create new order
     */
    createOrder() {
      // Add new order
      this.orders.push({
        id: this.counter++,
        items: [],
        provisionalAmount: '0',
        discount: '0',
        discountUnit: '%',
        paymentAmount: '0',
        note: ''
      })
      // Added order will be active
      this.setActive(this.orders.at(-1).id)

      return this.activeId
    },

    /**
     * Update active order
     *
     * @param {object} order
     */
    editActiveOrder(order) {
      this.orders.splice(this.getActiveIndex, 1, {
        ...this.getActiveOrder,
        ...order
      })
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
        this.setActive(this.orders.at(0).id)
      } else {
        // Re-activate activeId to force getting activeIndex
        this.setActive(this.activeId)
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
      this.activeIndex = this.orders.findIndex(order => order.id === this.activeId)
    },

    /**
     * Update provisional amount
     */
    updateProvisionalAmount() {
      // Get active order
      const order = this.orders.at(this.getActiveIndex);
      // Update provisional amount
      order.provisionalAmount = order.items.reduce((acc, cur) => acc + parseInt(cur.totalPrice), 0) + ""
    }
  }
})
