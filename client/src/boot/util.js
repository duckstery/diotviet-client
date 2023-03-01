const util = {
  /**
   * Check if all field is true
   *
   * @param {object} obj
   * @returns {boolean}
   */
  pass(obj) {
    return Object.values(obj).every(value => value === true)
  },
  /**
   *
   * Stringify object
   *
   * @param object
   * @param innerSeparator
   * @param outerSeparator
   * @returns {string}
   */
  stringify(object, innerSeparator = '=', outerSeparator = ',') {
    return Object.entries(object)
      .map(entry => entry.join(innerSeparator))
      .join(outerSeparator)
  }
}

export default boot(({ app }) => {
  app.config.globalProperties.$util = util
})
