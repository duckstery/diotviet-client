<template>
  <q-tabs
    :model-value="getActiveId"
    @update:model-value="setActive"

    flat
    no-caps
    inline-label
    outside-arrows
    switch-indicator
    align="left"
    active-bg-color="grey-3"
    active-color="primary"
    active-class="d-tab-shape"
    indicator-color="transparent"
    class="text-white tw-h-[50px] tw-max-w-[720px] q-tabs--scrollable"
  >
    <q-tab
      v-for="(id, index) in getOrders"
      :name="id"
      :ripple="false"
      class="d-tab"
    >
      <q-icon class="q-tab__icon" name="receipt"/>
      <div class="q-tab__label">{{`${$t('field.order')} ${index + 1}`}}</div>
      <Button
        flat
        size="sm"
        icon="close"
        v-if="getTotalSize > 1"
        class="tw--right-2 tw-bottom-2"
        :color="id === getActiveId ? 'primary' : 'white'"
        @click.stop="removeOrder(index)"
      />
    </q-tab>
  </q-tabs>
  <Button icon="add" @click="onCreateOrder" color="white" flat :tooltip="$t('field.new_order')"/>
</template>

<script>
import {mapState, mapActions} from "pinia";
import {useOrderStore} from "stores/order";

import Button from "components/General/Other/Button.vue";

export default {
  name: 'OrderBar',

  components: {Button},

  computed: {
    // "Order" store
    ...mapState(useOrderStore, ["getActiveId", "getTotalSize", "getOrders"])
  },

  methods: {
    /**
     * Add new order to tabList (order list)
     */
    onCreateOrder() {
      // Check if number of receipt is exceed 10
      if (this.getTotalSize < 10) {
        // Add new tab
        this.createOrder();
      } else {
        // Notify and prevent adding new tab
        this.$notifyWarn(this.$t('error.limit_exceeded'))
      }
    },

    // Order store
    ...mapActions(useOrderStore, ['setActive', 'createOrder', 'removeOrder'])
  },
}
</script>

<style scoped lang="scss">
.d-tab {
  -webkit-border-top-left-radius: 10px;
  -webkit-border-top-right-radius: 10px;
  -moz-border-radius-topleft: 10px;
  -moz-border-radius-topright: 10px;
  border-top-left-radius: 10px;
  border-top-right-radius: 10px;
}
.d-tab-shape:before,
.d-tab-shape:after {
  content: "";
  position: absolute;
  z-index: -1000;

  height: 10px;
  width: 20px;

  bottom: 0;
  animation: fadeIn 800ms;
}

.d-tab-shape:after {
  right: -20px;

  border-radius: 0 0 0 10px;
  -moz-border-radius: 0 0 0 10px;
  -webkit-border-radius: 0 0 0 10px;

  -webkit-box-shadow: -10px 0 0 0 #eee;
  box-shadow: -10px 0 0 0 #eee;
}

.d-tab-shape:before {
  left: -20px;

  border-radius: 0 0 10px 0;
  -moz-border-radius: 0 0 10px 0;
  -webkit-border-radius: 0 0 10px 0;

  -webkit-box-shadow: 10px 0 0 0 #eee;
  box-shadow: 10px 0 0 0 #eee;
}

.body--dark {
  .d-tab-shape:after {
    -webkit-box-shadow: -10px 0 0 0 $grey-10;
    box-shadow: -10px 0 0 0 $grey-10;

  }

  .d-tab-shape:before {
    -webkit-box-shadow: 10px 0 0 0 $grey-10;
    box-shadow: 10px 0 0 0 $grey-10;
  }
}

@keyframes fadeIn {
  0% { opacity: 0; }
  100% { opacity: 1; }
}
</style>
