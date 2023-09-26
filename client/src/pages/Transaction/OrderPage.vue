<template>
  <Page :breadcrumbs="breadcrumbs" :split="[2, 10]">
    <template #left>
      <!-- Title -->
      <div class="tw-text-3xl tw-font-semibold">{{ $t('field.order') }}</div>

      <!-- Filter -->
      <OrderFilter v-model="filter" :groups="groups"
                   @request="onSearch" @control="onGroupControl"/>
    </template>
    <template #right>
      <!-- Data table -->
      <DataTable v-model:pagination="pagination" :headers="headers" :items="items" :loading="loading"
                 :operations="operations"
                 @search="onSearch" @request="onRequest">
        <template #default="props">
          <OrderDetail v-bind="props" @request="onRequest"/>
        </template>
      </DataTable>
    </template>
  </Page>
</template>

<script>
import DataTable from "components/Manage/DataTable.vue";
import OrderFilter from "components/Manage/Transaction/Order/OrderFilter.vue";
import Page from "components/General/Layout/Page.vue";
import OrderDetail from "components/Manage/Transaction/Order/OrderDetail.vue";

import {ref} from "vue";
import {usePageSearch} from "src/composables/usePageSearch";
import {usePageRequest} from "src/composables/usePageRequest";
import {useGroupControl} from "src/composables/useGroupControl";
import {useRouter} from "vue-router";
import {usePrinter} from "src/composables/usePrinter";

export default {
  name: 'OrderPage',

  components: {OrderDetail, OrderFilter, Page, DataTable},

  setup() {
    // Setup printer
    usePrinter('/order/init/print')

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

    return {loading, ...pageSearch, ...pageRequest, ...useGroupControl(pageSearch.groups)}
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
