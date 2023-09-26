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
import Page from "components/General/Layout/Page.vue";
import TransactionDetail from "components/Manage/Transaction/TransactionDetail.vue";
import DataTable from "components/Manage/DataTable.vue";
import TransactionFilter from "components/Manage/Transaction/TransactionFilter.vue";

export default {
  name: 'StaffPage',
  components: {TransactionFilter, DataTable, TransactionDetail, Page},

  computed: {
    // Breadcrumbs
    breadcrumbs() {
      return [
        {label: this.$t('field.transaction'), to: '/transaction', icon: 'fa-arrow-right-arrow-left'},
        {label: this.$t('field.history'), to: '/transaction/history', icon: 'fa-timeline'},
      ]
    },
  }
}
</script>
