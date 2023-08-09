import {boot} from 'quasar/wrappers'
import {Cookies, date} from "quasar"
import {axios} from "./axios"
import {useAuthStore} from "stores/auth"

// *************************************************
// Typed
// *************************************************

export interface Auth {
  subscribe(jwt: string): void
  user(): User
  isAuthenticated(): boolean
  login(credential: Credential): Promise<string | void>
  logout(): Promise<string | void>
}

// User data
export type User = { name: string, id: string }
// Credential
export type Credential = { email: string, password: string }
// Token
export type Token = {
  role: string,
  iss: string,
  id: number,
  exp: number,
  iat: number,
  jti: string,
  email: string
}

// *************************************************
// Implementation
// *************************************************

// Constant
const tokenKey: string = '_token'
// Init store
const store = useAuthStore();

const auth: Auth = {
  /**
   * Subscribe JWT
   *
   * @param {string} jwt
   */
  subscribe(jwt) {
    try {
      // Decode jwt
      const payload = decodeJWT(jwt)

      // Save JWT to cookie
      Cookies.set(tokenKey, jwt, {expires: new Date(payload.exp * 1000), sameSite: 'Lax'})
      // Save JWT and payload to $store
      store.subscribe({...payload, jwt})
    } catch (e) {
      if (process.env.DEV) {
        console.warn(e)
      }
      // Clear cookie
      Cookies.remove(tokenKey)
      // Reset store
      store.reset()
    }
  },

  /**
   * Get user info
   */
  user(): User {
    return {
      id: store.getUserId,
      name: store.getUserName
    }
  },

  /**
   * Check if user is authenticated
   *
   * @returns {boolean}
   */
  isAuthenticated(): boolean {
    // If expired is greater than now(), consider not expired
    return !!store.getExpiredAt && (date.subtractFromDate(new Date(), {seconds: 5})).getTime() / 1000 < store.getExpiredAt
  },

  /**
   * Login
   *
   * @param {Credential} credential
   * @returns {Promise<void>}
   */
  login(credential: Credential): Promise<string | void> {
    return axios.post(`${process.env.API_BASE_URL}/api/auth/login`, credential)
      .then(res => {
        // Subscribe JWT
        this.subscribe(res.data.payload.token)
        // Resolve promise
        return res.data.message
      })
      .catch(err => Promise.reject(err.response ? err.response.data.message : err.message))
  },

  /**
   * Logout
   *
   * @returns {Promise<void>}
   */
  logout(): Promise<string | void> {
    return axios.get(`${process.env.API_BASE_URL}/api/auth/logout`)
      .then(res => {
        // Clear cookie
        Cookies.remove(tokenKey)
        // Reset store
        store.reset()

        // Resolve promise
        return res.data.message
      })
      .catch(err => Promise.reject(err.response.data.message))
  }
}

/**
 * Decode JWT
 *
 * @param {string} jwt
 * @returns {Token}
 */
function decodeJWT(jwt: string): Token {
  // Split JWT parts
  const base64Url = jwt.split('.')[1];
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

export default boot(({app, router}) => {
  // Register request interceptor to embed JWT and priorly check for JWT validity
  axios.interceptors.request.use(function (config) {
    // Check if target API is /login
    // @ts-ignore
    if (config.url.includes("/api/auth/login")) {
      // This API won't be affect if token is expired
      return config
    }

    // Check if token is expired or not
    if (auth.isAuthenticated()) {
      config.headers.Authorization = `Bearer ${store.getToken}`
    } else {
      // @ts-ignore. Abort request if token is expired
      config.signal = AbortSignal.abort("Token expired")
    }

    return config
  })

  // Register middleware to check if user is authenticated
  router.beforeEach((to, from, next) => {
    // Check if "from" name is undefined
    if (from.name === undefined) {
      // Since this is the first time accessing the page, init store (pinia)
      auth.subscribe(Cookies.get(tokenKey))
    }

    // Check if user is authenticated
    const isAuthenticated = auth.isAuthenticated()

    if (to.name === 'Login' && isAuthenticated) {
      // Redirect to 'Work' if user is authenticated but trying to access 'Login'
      next({name: 'Work'})
    } else if (to.name !== 'Login' && !isAuthenticated) {
      // Redirect to 'Login' if user is not authenticated but trying to access route that is not 'Login'
      next({name: 'Login'})
    } else {
      // Let user through
      next()
    }
  })

  // Register middleware to check if user is authorized
  router.beforeEach((to, from, next) => {
    // Get path privilege (the privilege need to have to access this path)
    const pathPrivilege = to.meta.privilege ?? 4;

    // The lower the privilege, the higher the role
    if (store.getPrivilege <= pathPrivilege) {
      next()
    } else {
      // 403: Forbidden
      next({name: 'Error', params: {status: 403}})
    }
  })

  // Access through this.$auth
  app.config.globalProperties.$auth = auth
})

export {auth}
