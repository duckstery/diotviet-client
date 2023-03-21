import {defineStore} from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    jwt: '',
    exp: '',
    id: '',
    name: '',
    privilege: 4,
  }),

  getters: {
    /**
     * Get orders size
     *
     * @param state
     * @returns {number}
     */
    getToken: (state) => state.jwt,

    /**
     * Get items in order
     *
     * @param state
     * @returns {*}
     */
    getExpiredAt: (state) => state.exp,

    /**
     * Get order selected id
     *
     * @param state
     * @returns {*}
     */
    getUserId: (state) => state.id,

    /**
     * Get items in order
     *
     * @param state
     * @returns {*}
     */
    getUserName: (state) => state.name,
    /**
     * Get role
     *
     * @param state
     * @returns {string|string|*}
     */
    getPrivilege: (state) => state.privilege
  },

  actions: {
    /**
     * Add item to active order
     *
     * @param {Object} payload
     */
    subscribe(payload) {
      // Normal set
      this.id = payload.id
      this.exp = payload.exp
      this.name = payload.sub
      this.jwt = payload.jwt

      // Privilege set
      const role = payload.role.substring(5)
      if (role === 'ADMIN') {
        this.privilege = 1
      } else if (role === 'SUPER') {
        this.privilege = 2
      } else if (role === 'STAFF') {
        this.privilege = 3
      } else {
        this.privilege = 4
      }
    },

    /**
     * Reset store
     */
    reset() {
      this.id = '';
      this.exp = '';
      this.name = '';
      this.jwt = '';
      this.privilege = 4;
    }
  }
})
