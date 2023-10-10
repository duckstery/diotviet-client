<template>
  <Page :breadcrumbs="breadcrumbs" :split="[2, 10]">
    <template #left>
      <!-- Title -->
      <div class="tw-text-3xl tw-font-semibold">{{ $t('field.transaction') }}</div>

      <!-- Filter -->
      <HistoryFilter v-model="filter"/>
    </template>
    <template #right>
      <div>
        <Timeline :value="timeline" @continue="fetch($event)"/>
      </div>
    </template>
  </Page>
</template>

<script>
import Page from "components/General/Layout/Page.vue";
import TransactionDetail from "components/Manage/Transaction/TransactionDetail.vue";
import DataTable from "components/Manage/DataTable.vue";
import TransactionFilter from "components/Manage/Transaction/TransactionFilter.vue";
import Timeline from "components/Manage/Timeline.vue";
import HistoryFilter from "components/Manage/HistoryFilter.vue";

import {onMounted, ref, watch} from "vue";
import {axios, dayjs, error} from "src/boot";

export default {
  name: 'TransactionHistoryPage',

  components: {HistoryFilter, Timeline, TransactionFilter, DataTable, TransactionDetail, Page},

  setup() {
    const now = dayjs()
    // Filter
    const filter = ref({
      from: now.startOf('month').format('YYYY-MM-DD'),
      to: now.format('YYYY-MM-DD'),
    })
    // Watch filter and fetch on changed
    watch(filter, () => fetch(), {deep: true})

    // Timeline's value
    const timeline = ref({})
    // Fetch
    const fetch = (page = 0) => axios.get('/transaction/history', {params: {page: page, ...filter.value}})
        .then(res => timeline.value = {new: page === 0, ...res.data.payload})
        .catch(error.any)
    // Fetch on mounted
    onMounted(fetch)

    return {
      filter: filter,
      timeline: timeline,
      fetch: fetch
    }
  },

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
