<template>
  <q-tab-panels :model-value="getActiveId" animated class="tw-h-full tw-w-full tw-bg-transparent">
    <q-tab-panel v-for="order in getOrders" :name="order" class="tw-py-0 tw-pl-0 tw-flex tw-flex-col">
      <q-virtual-scroll
        :items="getActiveOrder.items"
        v-slot="{item, index}"
        class="virtual-scrollbar"
        :style="`max-height: ${maxHeight}px`"
      >
        <OrderItem :value="item" :index="index" class="tw-mt-2 first:tw-mt-0"/>
      </q-virtual-scroll>
    </q-tab-panel>
  </q-tab-panels>
</template>

<script>
import {mapState} from "pinia";
import {useOrderStore} from "stores/order";

import OrderItem from "components/Work/OrderItem.vue";

export default {
  name: 'OrderPanel',

  components: {OrderItem},

  props: {
    // Panel max height
    maxHeight: {
      type: Number,
      default: 0
    }
  },

  computed: {
    // "Order" store
    ...mapState(useOrderStore, ['getOrders', 'getActiveId', 'getActiveOrder'])
  },
}
</script>

<style scoped lang="scss">
.virtual-scrollbar {
  &::-webkit-scrollbar-track {
    -webkit-box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.3);
    border-radius: 10px;
    background-color: #F5F5F5;
  }

  &::-webkit-scrollbar {
    width: 6px;
    background-color: #F5F5F5;
  }

  &::-webkit-scrollbar-thumb {
    border-radius: 10px;
    -webkit-box-shadow: inset 0 0 6px rgba(0, 0, 0, .3);
    background-color: #555;
  }
}

</style>
