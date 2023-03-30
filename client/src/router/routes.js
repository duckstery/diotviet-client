/**
 * Craft simple route object
 *
 * @param {string} path
 * @param {string} name
 * @param {function(): Promise} component
 * @param {number} privilege
 * @returns {{path, component, meta: {privilege}, name}}
 */
const r = (path, name, component, privilege= 4) => {
  return {path, name, component, meta: {privilege}}
}

const routes = [
  {
    path: '/',
    component: () => import('layouts/BlankLayout.vue'),
    children: [
      r('', 'Login', () => import('pages/IndexPage.vue')),
    ]
  },
  {
    path: '/work',
    component: () => import('layouts/WorkLayout.vue'),
    children: [
      r('', 'Work', () => import('pages/WorkPage.vue')),
    ]
  },
  {
    path: '/product',
    component: () => import('layouts/ManageLayout.vue'),
    children: [
      r('', 'Product', () => import('pages/Product/ProductPage.vue')),
    ]
  },
  {
    path: '/transaction',
    component: () => import('layouts/ManageLayout.vue'),
    children: [
      r('', 'Transaction', () => import('pages/Transaction/OrderPage.vue')),
    ]
  },
  {
    path: '/partner',
    component: () => import('layouts/ManageLayout.vue'),
    children: [
      r('', 'Partner', () => import('pages/Partner/CustomerPage.vue')),
    ]
  },
  {
    path: '/staff',
    component: () => import('layouts/ManageLayout.vue'),
    children: [
      r('', 'Staff', () => import('pages/Staff/StaffPage.vue')),
    ]
  },

  // Error page
  {
    path: '/error/:status',
    name: 'Error',
    component: () => import('pages/ErrorPage.vue'),
  },
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorPage.vue'),
  }
]

export default routes
