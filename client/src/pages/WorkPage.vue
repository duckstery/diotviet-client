<template>
  <q-page class="bg-grey-3 tw-p-5 tw-pr-20 tw-h-full">
    <div class="row fill-page" :style="`height: ${orderPanelHeight}px`">
      <div class="col-6 ">
        <OrderPanel :max-height="orderPanelHeight" :style="`max-height: ${orderPanelHeight}px`"/>
      </div>
      <div class="col-6">
        <ItemPanel ref="itemPanel"/>
        <StatisticPanel :max-height="statisticPanelHeight" class="tw-mt-5 tw-flex-grow" :style="`height: ${statisticPanelHeight}px`"/>
      </div>
    </div>

  </q-page>
</template>

<script>
import {defineComponent} from 'vue'
import OrderPanel from "components/Work/OrderPanel.vue";
import ItemPanel from "components/Work/SamplePanel.vue";
import StatisticPanel from "components/Work/StatisticPanel.vue";

export default defineComponent({
  name: 'IndexPage',

  components: {StatisticPanel, ItemPanel, OrderPanel},

  data: () => ({
    isMounted: false,
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

  mounted() {
    this.isMounted = true
  }
})
</script>
