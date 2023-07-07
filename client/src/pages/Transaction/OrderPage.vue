<template>
  <Page :breadcrumbs="breadcrumbs">
    <div class="col-2 tw-pr-3">
      <!-- Title -->
      <div class="tw-text-3xl tw-font-semibold">{{ $t('field.order') }}</div>

      <!-- Filter -->
      <OrderFilter v-model="filter" :groups="groups"
                      @request="onSearch" @control="onGroupControl"/>
    </div>
    <div class="col-10">
      <!-- Data table -->
      <DataTable v-model:pagination="pagination" :headers="headers" :items="items" :loading="loading" :operations="operations"
                 @search="onSearch" @request="onRequest">
        <template #default="props">
          <OrderDetail v-bind="props" @request="onRequest"/>
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
import OrderDetail from "components/Manage/Transaction/Order/OrderDetail.vue";
import {ref} from "vue";
import {usePageSearch} from "src/composables/usePageSearch";
import {usePageRequest} from "src/composables/usePageRequest";
import {useGroupControl} from "src/composables/useGroupControl";
import {useRouter} from "vue-router";

export default {
  name: 'OrderPage',

  components: {OrderDetail, OrderFilter, CustomerDetail, Page, DataTable, Breadcrumbs},

  setup() {
    // Use router
    const router = useRouter()
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
    })
    // Page request functionality
    const pageRequest = usePageRequest(
      () => router.push('/work'),
      null,
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
    // Operations
    operations() {
      return [
        {key: 'resolve', event: 'patch', target: 'status', option: 2, icon: 'fa-circle-check', color: 'positive'},
        {key: 'abort', event: 'patch', target: 'status', option: 3, icon: 'fa-circle-stop', color: 'negative', reasonable: true}
      ]
    }
  },
}
</script>
