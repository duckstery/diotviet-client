<template>
  <Page :breadcrumbs="breadcrumbs">
    <div class="col-12">
      <div class="row tw-h-full">
        <div class="col-6">
          <OrderPanel :max-height="orderPanelHeight" :style="`max-height: ${orderPanelHeight}px`"/>
        </div>
        <div class="col-6">
          <ItemPanel :items="items" ref="itemPanel"/>
          <StatisticPanel :max-height="statisticPanelHeight" class="tw-mt-5 tw-flex-grow" :style="`height: ${statisticPanelHeight}px`"/>
        </div>
      </div>
    </div>
  </Page>
</template>

<script>
import {defineComponent} from 'vue'

import Page from "components/General/Layout/Page.vue";
import OrderPanel from "components/Work/OrderPanel.vue";
import ItemPanel from "components/Work/SamplePanel.vue";
import StatisticPanel from "components/Work/StatisticPanel.vue";

export default defineComponent({
  name: 'WorkPage',

  components: {Page, StatisticPanel, ItemPanel, OrderPanel},

  data: () => ({
    isMounted: false,
    items: [],
  }),

  computed: {
    // Calculate OrderPanel (by subtract header height and top, bot padding from screen height
    orderPanelHeight() {
      return this.$q.screen.height - 50 - 40
    },
    // Calculate StatisticPanel height (by subtract ItemPanel height from OrderPanel)
    statisticPanelHeight() {
      return this.isMounted ? Math.max(this.orderPanelHeight - this.$refs['itemPanel']?.$el.offsetHeight - 20, 134) : 0
    }
  },

  methods: {
    /**
     * Initiate load
     */
    index() {
      // Call API to get data for table
      this.$axios.get('/product/display')
        .then(this.applyItems)
    },

    /**
     * Apply items
     * @param res
     */
    applyItems(res) {
      this.items = res.data.payload
    }
  },

  mounted() {
    this.isMounted = true
    this.index()
  }
})
</script>
