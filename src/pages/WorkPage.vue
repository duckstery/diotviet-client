<template>
  <Page>
    <div class="col-12">
      <div v-if="$q.platform.is.capacitor" class="tw-h-full">
        <ManagePanel/>
      </div>
      <div v-else class="row tw-h-full">
        <div class="col-6">
          <OrderPanel :max-height="orderPanelHeight" :style="`max-height: ${orderPanelHeight}px`"/>
        </div>
        <div class="col-6">
          <SamplePanel :items="items" ref="itemPanel" @order="onOpe('order')" @purchase="onOpe('purchase')"/>
          <StatisticPanel class="tw-mt-3.5 tw-flex-grow"
                          :max-height="statisticPanelHeight" :style="`height: ${statisticPanelHeight}px`"/>
        </div>
      </div>
    </div>
  </Page>
</template>

<script>
import Page from "components/General/Layout/Page.vue";
import OrderPanel from "components/Work/OrderPanel.vue";
import SamplePanel from "components/Work/SamplePanel.vue";
import StatisticPanel from "components/Work/StatisticPanel.vue";
import ManagePanel from "components/Work/ManagePanel.vue";

import {computed, defineComponent, ref, toRaw, unref, watch} from 'vue'
import {useMounted, templateRef} from "@vueuse/core";
import {Platform, useQuasar} from 'quasar';
import {useOrderStore} from "stores/order";
import {axios, error, util, notify} from "src/boot";
import {useI18n} from "vue-i18n";
import {usePrinter} from "src/composables/usePrinter";
import {useAdvanceStorage} from "src/composables/useAdvanceStorage";
import {useProductStore} from "stores/product";
import {storeToRefs} from "pinia";
import _ from "lodash";

export default defineComponent({
  name: 'WorkPage',

  components: {ManagePanel, Page, StatisticPanel, SamplePanel, OrderPanel},

  setup() {
    // Run nothing if using Capacitor
    if (Platform.is.capacitor) return {};

    // Get "Order" store
    const orderStore = useAdvanceStorage(useOrderStore)
    // Get activeCustomer
    const activeCustomer = computed(() => toRaw(useOrderStore().getActiveCustomer))

    // Get i18n
    const $t = useI18n().t
    // Get quasar
    const $q = useQuasar()

    // Validate before create Order
    const validate = () => {
      if (util.isUnset(activeCustomer.value)) {
        notify($t("message.specify_customer"), 'negative')
      } else if (_.isEmpty(orderStore.getActiveOrder.items)) {
        notify($t("message.specify_least_item"), 'negative')
      } else {
        return true
      }

      return false
    }

    // Check if order info is changed to avoid duplication bug
    const recentCreatedOrder = ref(null)
    watch(() => orderStore.getActiveOrder, () => recentCreatedOrder.value = null, {deep: true})
    // Setup Printer
    const resources = usePrinter('/product/display', res => items.value = res.data.payload.items)
    // On operation
    const onOpe = (type) => {
      // Only printing Order
      if (recentCreatedOrder.value !== null && recentCreatedOrder.value >= 0) return fetchAndPrint()
      // Check if should creating order
      if (validate()) {
        // Set recent order id as -1, to prevent duplication process
        recentCreatedOrder.value = -1
        // Send api
        axios.post(`/order/${type}`, unref(orderStore.getCleanActiveOrder))
          // Get Order print data
          .then(res => {
            notify($t('message.order_create'))
            recentCreatedOrder.value = res.data.payload
            fetchAndPrint()
          })
          .catch(error.any)
      }
    }
    // Fetch and print recent order
    const fetchAndPrint = () => axios.get(`/order/print/${recentCreatedOrder.value}`).then(resources.print).catch(error.any)

    // Check if page is mounted
    const isMounted = useMounted()
    // Products for display
    const {items} = storeToRefs(useProductStore())

    // SamplePanel's reference
    const itemPanel = templateRef('itemPanel')
    // Calculate OrderPanel (by subtract header height and top, bot padding from screen height
    const orderPanelHeight = computed(() => $q.screen.height - 50 - 40)
    // Calculate StatisticPanel height (by subtract SamplePanel height from OrderPanel)
    const statisticPanelHeight = computed(() => isMounted.value
      ? Math.max(orderPanelHeight.value - itemPanel.value?.$el.offsetHeight - 20, 134)
      : 0)

    return {
      // Products for display
      items: items,
      // Handler
      onOpe: onOpe,
      // Screen properties
      orderPanelHeight: orderPanelHeight, statisticPanelHeight: statisticPanelHeight
    }
  },
})
</script>
