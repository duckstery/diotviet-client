<template>
  <q-card class="sample-panel" flat>
    <q-card-section class="tw-w-full tw-overflow-y-auto virtual-scrollbar tw-py-0"
                    :style="`max-height: ${maxHeight}px`">
      <div class="row">
        <div class="col-5 tw-px-2">
          <LabelField src="images/note.png" :label="$t('field.note')" class="tw-pt-2"/>
          <div class="">
            <q-input
              v-model="getActiveOrder.note"
              type="textarea"
              dense
              filled
              maxlength="100"
              class="tw-w-full tw-mt-2 tw-max-h-[76px]"
              input-class="virtual-scrollbar"
              :input-style="{maxHeight: '70px'}"
            />
          </div>

        </div>
        <div class="col-7 tw-px-2">
          <!-- Total -->
          <DisplayField
            :model-value="getActiveOrder.provisionalAmount"
            mask="###,###,###,###"
            src="images/provisional_amount.png"
            inner-class="tw-w-28"
            :label="$t('field.provisional_amount')"
            space
          />

          <!-- Discount -->
          <div class="flex">
            <LabelField src="images/discount.png" :label="$t('field.discount')" class="tw-pt-2"/>
            <q-toggle
              v-model="getActiveOrder.discountUnit"

              true-value="cash"
              false-value="%"
              :icon="discountUnitIcon"
              :label="discountUnitLabel"
            />
            <q-space/>
            <div>
              <TextField
                v-model="getActiveOrder.discount"

                compact
                required
                :mask="discountMask"
                class="tw-w-28 tw-p-0 tw-float-right"
                input-class="tw-font-semibold tw-text-center tw-p-0"
              />
            </div>
          </div>

          <!-- Discount -->
          <div>
            <DisplayField
              :model-value="getActiveOrder.paymentAmount"
              mask="###,###,###,###"
              src="images/payment_amount.png"
              inner-class="tw-w-28"
              :label="$t('field.payment_amount')"
              space
            />
          </div>
        </div>
      </div>
    </q-card-section>
  </q-card>
</template>

<script>
import {useOrderStore} from "stores/order";
import {toReactive} from "@vueuse/core";
import {mapState, storeToRefs} from "pinia";
import {usePriceControl} from "src/composables/usePriceControl";

import TextField from "components/General/Other/TextField.vue";
import DisplayField from "components/General/Other/DisplayField.vue";
import LabelField from "components/General/Other/LabelField.vue";

export default {
  name: 'StatisticPanel',

  components: {LabelField, DisplayField, TextField},

  props: {
    maxHeight: Number
  },

  computed: {
    // "Order" store
    ...mapState(useOrderStore, ['getActiveOrder'])
  },

  setup() {
    // Turn store to ref and get activeOrder only
    const {getActiveOrder} = storeToRefs(useOrderStore())

    return {
      getActiveOrder,
      ...usePriceControl(toReactive(getActiveOrder), 'provisionalAmount', 'paymentAmount')
    }
  }
}
</script>
