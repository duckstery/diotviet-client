<template>
  <Page>
    <div class="col-12">
      <div class="row tw-h-full">
        <div class="col-6">
          <OrderPanel :max-height="orderPanelHeight" :style="`max-height: ${orderPanelHeight}px`"/>
        </div>
        <div class="col-6">
          <ItemPanel :items="items" ref="itemPanel" @order="onOpe('order')" @purchase="onOpe('purchase')"/>
          <StatisticPanel class="tw-mt-5 tw-flex-grow"
                          :max-height="statisticPanelHeight" :style="`height: ${statisticPanelHeight}px`"/>
        </div>
      </div>
    </div>
  </Page>
</template>

<script>
import Page from "components/General/Layout/Page.vue";
import OrderPanel from "components/Work/OrderPanel.vue";
import ItemPanel from "components/Work/SamplePanel.vue";
import StatisticPanel from "components/Work/StatisticPanel.vue";

import {computed, defineComponent, ref, unref} from 'vue'
import {useMounted} from "@vueuse/core";
import {useQuasar} from 'quasar';
import {useOrderStore} from "stores/order";
import {axios, error, util, notify} from "src/boot";
import {useI18n} from "vue-i18n";
import _ from "lodash";
import {usePrinter} from "src/composables/usePrinter";
import {useAdvanceStorage} from "src/composables/useAdvanceStorage";

export default defineComponent({
  name: 'WorkPage',

  components: {Page, StatisticPanel, ItemPanel, OrderPanel},

  setup() {
    // Get "Order" store
    const orderStore = useAdvanceStorage(useOrderStore)

    // Get i18n
    const $t = useI18n().t
    // Get quasar
    const $q = useQuasar()

    // Validate before create Order
    const validate = () => {
      if (util.isUnset(orderStore.getActiveCustomer)) {
        notify($t("message.specify_customer"), 'negative')
      } else if (_.isEmpty(orderStore.getActiveOrder.items)) {
        notify($t("message.specify_least_item"), 'negative')
      } else {
        return true
      }

      return false
    }

    // Setup Printer
    const resources = usePrinter('/product/display', res => items.value = res.data.payload.items)
    // On operation
    const onOpe = (type) => {
      if (validate()) {
        axios.post(`/order/${type}`, unref(orderStore.getCleanActiveOrder))
          // Get Order print data
          .then(res => {
            notify($t('message.order_create'))
            axios.get(`/order/print/${res.data.payload}`).then(resources.print).catch(error.any)
          })
          .catch(error.any)
      }
    }

    // Check if page is mounted
    const isMounted = useMounted()
    // Products for display
    const items = ref([])

    // ItemPanel's reference
    const itemPanel = ref(null)
    // Calculate OrderPanel (by subtract header height and top, bot padding from screen height
    const orderPanelHeight = computed(() => $q.screen.height - 50 - 40)
    // Calculate StatisticPanel height (by subtract ItemPanel height from OrderPanel)
    const statisticPanelHeight = computed(() => isMounted.value
      ? Math.max(orderPanelHeight.value - itemPanel.value?.$el.offsetHeight - 20, 134)
      : 0)

    return {
      // Products for display
      items: items,
      // Handler
      onOpe: onOpe,
      // Screen properties
      itemPanel: itemPanel, orderPanelHeight: orderPanelHeight, statisticPanelHeight: statisticPanelHeight
    }
  },
})
</script>
