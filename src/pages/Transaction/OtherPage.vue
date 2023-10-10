<template>
  <Page :breadcrumbs="breadcrumbs" :split="[2, 10]">
    <template #left>
      <!-- Title -->
      <div class="tw-text-3xl tw-font-semibold">{{ $t('field.transaction') }}</div>

      <!-- Filter -->
      <TransactionFilter v-model="filter" @request="onSearch"/>
    </template>
    <template #right>
      <!-- Data table -->
      <DataTable v-model:pagination="pagination" :headers="headers" :items="items" :loading="loading"
                 :operations="operations" no-im-ex
                 @search="onSearch" @request="onRequest">
        <template #default="props">
          <TransactionDetail v-bind="props"/>
        </template>
      </DataTable>
    </template>
  </Page>
</template>

<script>
import DataTable from "components/Manage/DataTable.vue";
import Page from "components/General/Layout/Page.vue";
import TransactionEditor from "components/Manage/Transaction/TransactionEditor.vue";
import TransactionFilter from "components/Manage/Transaction/TransactionFilter.vue";
import TransactionDetail from "components/Manage/Transaction/TransactionDetail.vue";

import {ref} from "vue";
import {usePageSearch} from "src/composables/usePageSearch";
import {usePageRequest} from "src/composables/usePageRequest";
import {useKeyEnforcer} from "src/composables/useKeyEnforcer";
import {watchOnce} from "@vueuse/core";

export default {
  name: 'Other',

  components: {TransactionDetail, TransactionFilter, Page, DataTable},

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
    // Watch once to extend headers
    watchOnce(pageSearch.headers, () => {
      pageSearch.headers.value.splice(1, 0, {
        name: "type", label: "transaction_type", field: "type", isInitDisplay: true
      })
    })

    // Page request functionality
    const pageRequest = usePageRequest(
      TransactionEditor,
      null,
      pageSearch.searchWithPreviousData
    )

    return {
      loading,
      ...pageSearch,
      ...pageRequest,
    }
  },

  computed: {
    // Breadcrumbs
    breadcrumbs() {
      return [
        {label: this.$t('field.transaction'), to: '/transaction', icon: 'fa-arrow-right-arrow-left'},
        {label: this.$t('field.other'), to: '/transaction/other', icon: 'fa-arrow-right-arrow-left'},
      ]
    }
  },
}
</script>
