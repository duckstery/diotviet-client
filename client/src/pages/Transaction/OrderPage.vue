<template>
  <Page :breadcrumbs="breadcrumbs">
    <div class="col-2 tw-pr-3">
      <!-- Title -->
      <div class="tw-text-3xl tw-font-semibold">{{ $t('field.order') }}</div>

      <!-- Filter -->
      <OrderFilter v-model="filter" :categories="categories" :groups="groups"
                      @request="onSearch" @control="onGroupControl"/>
    </div>
    <div class="col-10">
      <!-- Data table -->
      <DataTable v-model:pagination="pagination" :headers="headers" :items="items" :loading="loading"
                 @search="onSearch" @request="onRequest">
        <template #default="props">
          <CustomerDetail v-bind="props" @request="onRequest"/>
        </template>
      </DataTable>
    </div>
  </Page>
</template>

<script>
import CustomerDetail from "components/Manage/Partner/Customer/CustomerDetail.vue";
import DataTable from "components/Manage/DataTable.vue";
import OrderFilter from "components/Manage/Transaction/Order/OrderFilter.vue";
import Page from "components/General/Layout/Page.vue";
import Breadcrumbs from "components/Manage/Breadcrumbs.vue";
import {ref} from "vue";
import {usePageSearch} from "src/composables/usePageSearch";
import {usePageRequest} from "src/composables/usePageRequest";
import CustomerEditor from "components/Manage/Partner/Customer/CustomerEditor.vue";
import {useGroupControl} from "src/composables/useGroupControl";

export default {
  name: 'OrderPage',

  components: {OrderFilter, CustomerDetail, Page, DataTable, Breadcrumbs},

  setup() {
    // Loading flag
    const loading = ref(false)
    // Page search functionality
    const pageSearch = usePageSearch({
      group: null,
      status: [],
      createAtFrom: null,
      createAtTo: null,
      resolvedAtFrom: null,
      resolvedAtTo: null,
      priceFrom: null,
      priceTo: null,
      lastTransactionAtFrom: null,
      lastTransactionAtTo: null,
      isMale: null
    })
    // Page request functionality
    const pageRequest = usePageRequest(
      CustomerEditor,
      () => ({categories: pageSearch.categories.value, groups: pageSearch.groups.value}),
      pageSearch.searchWithPreviousData
    )
    // Group control
    const groupControl = useGroupControl(pageSearch.groups)

    return {
      loading,
      ...pageSearch,
      ...pageRequest,
      ...groupControl
    }
  },

  computed: {
    // Breadcrumbs
    breadcrumbs() {
      return [
        {label: this.$t('field.transaction'), to: '/transaction', icon: 'fa-arrow-right-arrow-left'},
        {label: this.$t('field.order'), to: '/transaction/order', icon: 'fa-inbox'},
      ]
    },
  },
}
</script>
