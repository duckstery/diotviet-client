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
      r('', 'Work', () => import('pages/WorkPage.vue'), 3),
    ]
  },
  {
    path: '/product',
    component: () => import('layouts/ManageLayout.vue'),
    children: [
      r('', 'Product', () => import('pages/Product/ProductPage.vue'), 3),
    ]
  },
  {
    path: '/transaction',
    component: () => import('layouts/ManageLayout.vue'),
    children: [
      r('', 'Transaction', () => import('pages/Transaction/TransactionPage.vue'), 3),
      r('order', 'Transaction.Order', () => import('pages/Transaction/OrderPage.vue'), 3),
      r('other', 'Transaction.Other', () => import('pages/Transaction/OtherPage.vue'), 2),
      r('history', 'Transaction.History', () => import('pages/Transaction/TransactionHistoryPage.vue'), 2),
    ]
  },
  {
    path: '/partner',
    component: () => import('layouts/ManageLayout.vue'),
    children: [
      r('', 'Partner', () => import('pages/Partner/PartnerPage.vue'), 3),
      r('customer', 'Partner.Customer', () => import('pages/Partner/CustomerPage.vue'), 3),
      r('staff', 'Partner.Staff', () => import('pages/Partner/StaffPage.vue'), 2),
    ]
  },
  {
    path: '/setting',
    component: () => import('layouts/ManageLayout.vue'),
    children: [
      r('', 'Setting', () => import('pages/Setting/SettingPage.vue'), 2),
      r('print', 'Setting.Print', () => import('pages/Setting/PrintPage.vue'), 2),
    ]
  },
  {
    path: '/report',
    component: () => import('layouts/ManageLayout.vue'),
    children: [
      r('', 'Report', () => import('pages/Report/ReportPage.vue'), 2),
      r('income', 'Report.Income', () => import('pages/Report/IncomePage.vue'), 2),
      r('order', 'Report.Order', () => import('pages/Report/ReportOrderPage.vue'), 2),
      r('rank', 'Report.Rank', () => import('pages/Report/RankPage.vue'), 1),
    ]
  },
  {
    path: '/operation',
    component: () => import('layouts/AndroidLayout.vue'),
    children: [
      r('process_order', 'ProcessOrder', () => import('pages/Operation/ProcessOrderPage.vue'), 3),
      r('create_transaction', 'CreateTransaction', () => import('pages/Operation/CreateTransactionPage.vue'), 3),
      r('inspect_identity', 'InspectIdentity', () => import('pages/Operation/InspectIdentityPage.vue'), 3),
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
