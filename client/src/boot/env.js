export default ({app}, inject) => {
  inject('env', (config = {}) => {
    Object.entries(config).forEach(([k, v]) => {
      localStorage.setItem(k, v)
    })

    return {
      'theme': localStorage.getItem('theme'),
      'locale': localStorage.getItem('locale')
    }
  })
}
