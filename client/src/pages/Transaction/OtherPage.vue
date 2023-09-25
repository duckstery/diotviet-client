<template>
  <Page :breadcrumbs="breadcrumbs">
    <div class="col-12 col-md-2 tw-pr-3">
      <!-- Title -->
      <div class="tw-text-3xl tw-font-semibold">{{ $t('field.transaction') }}</div>

      <!-- Filter -->
      <TransactionFilter v-model="filter" @request="onSearch"/>
    </div>
    <div class="col-12 col-md-10">
      <!-- Data table -->
      <DataTable v-model:pagination="pagination" :headers="headers" :items="items" :loading="loading"
                 :operations="operations"
                 @search="onSearch" @request="onRequest">
        <template #default="props">
<!--          <OrderDetail v-bind="props" @request="onRequest"/>-->
        </template>
      </DataTable>
    </div>
  </Page>
</template>

<script>
import DataTable from "components/Manage/DataTable.vue";
import Page from "components/General/Layout/Page.vue";
import TransactionEditor from "components/Manage/Transaction/TransactionEditor.vue";
import TransactionFilter from "components/Manage/Transaction/TransactionFilter.vue";

import {ref} from "vue";
import {usePageSearch} from "src/composables/usePageSearch";
import {usePageRequest} from "src/composables/usePageRequest";
import {useKeyEnforcer} from "src/composables/useKeyEnforcer";

export default {
  name: 'Other',

  components: {TransactionFilter, Page, DataTable},

  setup() {
    // Use key enforcer
    useKeyEnforcer('transaction')

    // Loading flag
    const loading = ref(false)
    // Page search functionality
    const pageSearch = usePageSearch({
      type: null,
      createAtFrom: null,
      createAtTo: null,
      priceFrom: null,
      priceTo: null,
    })
    // Page request functionality
    const pageRequest = usePageRequest(
      TransactionEditor,
      null,
      pageSearch.searchWithPreviousData
    )

    return {loading, ...pageSearch, ...pageRequest}
  },

  computed: {
    // Breadcrumbs
    breadcrumbs() {
      return [
        {label: this.$t('field.transaction'), to: '/transaction', icon: 'fa-arrow-right-arrow-left'},
        {label: this.$t('field.other'), to: '/transaction/other', icon: 'fa-arrow-right-arrow-left'},
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
