<template>
  <Page>
    <div class="col-12">
      <div class="row tw-h-full">
        <div class="col-6">
          <OrderPanel :max-height="orderPanelHeight" :style="`max-height: ${orderPanelHeight}px`"/>
        </div>
        <div class="col-6">
          <ItemPanel :items="items" ref="itemPanel" @order="onOrder" @purchase="onPurchase"/>
          <StatisticPanel :max-height="statisticPanelHeight" class="tw-mt-5 tw-flex-grow" :style="`height: ${statisticPanelHeight}px`"/>
        </div>
      </div>
    </div>
  </Page>
</template>

<script>
import {defineComponent, unref} from 'vue'

import Page from "components/General/Layout/Page.vue";
import OrderPanel from "components/Work/OrderPanel.vue";
import ItemPanel from "components/Work/SamplePanel.vue";
import StatisticPanel from "components/Work/StatisticPanel.vue";
import {useOrderStore} from "stores/order";

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
        .then(res => this.items = res.data.payload)
    },

    onOrder() {
      if (this.validate()) {
        this.$axios.post('/order/order', unref(useOrderStore().getActiveOrder))
          .then(console.warn)
          .catch(this.$error.any)
      }
    },

    onPurchase() {

    },

    /**
     * Validate before create Order
     *
     * @return {boolean}
     */
    validate() {
      if (this.$util.isUnset(useOrderStore().getActiveCustomer)) {
        this.$notifyErr(this.$t("message.specify_customer"))
      } else if (this.$_.isEmpty(useOrderStore().getActiveOrder.items)) {
        this.$notifyErr(this.$t("message.specify_least_item"))
      } else {
        return true
      }

      return false
    }
  },

  mounted() {
    this.isMounted = true
    this.index()
  }
})
</script>
