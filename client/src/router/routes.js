/**
 * Craft simple route object
 *
 * @param {string} path
 * @param {string} name
 * @param {function(): Promise} component
 * @param {number} privilege
 * @returns {{path, component, meta: {privilege}, name}}
 */
const r = (path, name, component, privilege = 4) => {
  return {path, name, component, meta: {privilege: privilege, key: path === '' ? name.toLowerCase() : path}}
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
      r('', 'Transaction', () => import('pages/Transaction/TransactionPage.vue')),
      r('order', 'Transaction.Order', () => import('pages/Transaction/OrderPage.vue')),
      r('receipt', 'Transaction.Receipt', () => import('pages/Transaction/ReceiptPage.vue')),
    ]
  },
  {
    path: '/partner',
    component: () => import('layouts/ManageLayout.vue'),
    children: [
      r('', 'Partner', () => import('pages/Partner/PartnerPage.vue')),
      r('customer', 'Partner.Customer', () => import('pages/Partner/CustomerPage.vue')),
      r('staff', 'Partner.Staff', () => import('pages/Partner/StaffPage.vue')),
    ]
  },
  {
    path: '/setting',
    component: () => import('layouts/ManageLayout.vue'),
    children: [
      r('', 'Setting', () => import('pages/Setting/SettingPage.vue')),
      r('print', 'Setting.Print', () => import('pages/Setting/PrintPage.vue')),
    ]
  },
  {
    path: '/report',
    component: () => import('layouts/ManageLayout.vue'),
    children: [
      r('', 'Report', () => import('pages/Report/ReportPage.vue')),
      r('income', 'Report.Income', () => import('pages/Report/IncomePage.vue')),
      r('order', 'Report.Order', () => import('pages/Report/ROrderPage.vue')),
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
