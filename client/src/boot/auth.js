import {boot} from 'quasar/wrappers'
import {Cookies} from "quasar";
import {axios} from "./axios"

const auth = {
  /**
   * Subscribe JWT
   *
   * @param {jwt} jwt
   */
  subscribe(jwt) {
    // Decode jwt
    const payload = decodeJWT(jwt)
    console.warn(payload)

    // Save JWT to cookie
    // Cookies.set('_token', jwt, {expires})

  },

  /**
   * Get user info
   */
  user() {

  },
  /**
   * Check if user is authenticated
   */
  isAuthenticated() {

  },
  /**
   * Login
   *
   * @param {{email: string, password: string}} credential
   */
  login(credential) {
    console.warn(axios)
    axios.post(`${process.env.API_BASE_URL}/api/auth/login`, credential)
      .then(res => console.log(res.body))
      .catch(console.warn)
  }
}

/**
 * Decode JWT
 *
 * @param {string} jwt
 * @returns {object}
 */
function decodeJWT(jwt) {
  // Split JWT parts
  const base64Url = token.split('.')[1];
  // Replace - with + and _ with / (+ and / is not URL friendly)
  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  // Decode
  const jsonPayload = decodeURIComponent(
    window
      .atob(base64)
      .split('')
      .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)).join('')
  );

  return JSON.parse(jsonPayload);
}

export default boot(({app}) => {
  // Add request interceptor to embed JWT and priorly check for JWT validity
  axios.interceptors.request.use(function (config) {
    // Get $auth reference
    const $auth = app.config.globalProperties.$auth
    console.warn('ahihi')
    return {
      ...config,
      // signal: AbortSignal.abort("Token expired")
    }
  })

  // Access through this.$auth
  app.config.globalProperties.$auth = auth
})

export {auth}
