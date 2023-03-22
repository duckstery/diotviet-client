<template>
  <q-card class="sample-panel" flat>
    <q-card-section class="tw-w-full tw-overflow-y-auto virtual-scrollbar tw-py-0"
                    :style="`max-height: ${maxHeight}px`">
      <div class="row">
        <div class="col-5 tw-px-2">
            <div class="tw-text-lg tw-pt-2">
              <IconMage src="images/note.png" size="20px"/>
              <span class="tw-ml-2 tw-underline tw-underline-offset-4">{{ $t('field.note') }}:</span>
            </div>

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
          <div class="flex">
            <div class="tw-text-lg tw-pt-2">
              <IconMage src="images/provisional_amount.png" size="20px"/>
              <span class="tw-ml-2 tw-underline tw-underline-offset-4">{{ $t('field.provisional_amount') }}:</span>
            </div>
            <q-space/>
            <div>
              <TextField
                :model-value="getActiveOrder.provisionalAmount"

                compact
                required
                readonly
                mask="###,###,###,###"
                class="tw-w-28 tw-p-0 tw-float-right"
                input-class="tw-font-semibold tw-text-center tw-p-0"
              />
            </div>
          </div>

          <!-- Discount -->
          <div class="flex">
            <div class="tw-text-lg tw-pt-2">
              <IconMage src="images/discount.png" size="20px"/>
              <span class="tw-ml-2 tw-underline tw-underline-offset-4">{{ $t('field.discount') }}:</span>
            </div>
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
                :mask="discountUnitMask"
                class="tw-w-28 tw-p-0 tw-float-right"
                input-class="tw-font-semibold tw-text-center tw-p-0"
              />
            </div>
          </div>

          <!-- Discount -->
          <div>
            <div class="flex">
              <div class="tw-text-lg tw-pt-2">
                <IconMage src="images/payment_amount.png" size="20px"/>
                <span class="tw-ml-2 tw-underline tw-underline-offset-4">{{ $t('field.payment_amount') }}:</span>
              </div>
              <q-space/>
              <TextField
                :model-value="getActiveOrder.paymentAmount"

                compact
                required
                readonly
                mask="###,###,###,###"
                class="tw-w-28 tw-p-0 tw-float-right"
                input-class="tw-font-semibold tw-text-center tw-p-0"
              />
            </div>
          </div>
        </div>
      </div>
    </q-card-section>
  </q-card>
</template>

<script>
import {useOrderStore} from "stores/order";
import {mapState} from "pinia";

import TextField from "components/General/Other/TextField.vue";
import IconMage from "components/General/Other/IconMage.vue";

export default {
  name: 'StatisticPanel',

  components: {IconMage, TextField},

  props: {
    maxHeight: Number
  },

  computed: {
    // Icon of discountUnit switch
    discountUnitIcon() {
      return 'fa-solid ' + (this.getActiveOrder.discountUnit === 'cash' ? 'fa-money-bill-wave' : 'fa-percent')
    },
    // Label of discountUnit text
    discountUnitLabel() {
      return this.$t('field.discount_by') + ' ' + this.getActiveOrder.discountUnit
    },
    // TextField mask of discountUnit
    discountUnitMask() {
      return this.getActiveOrder.discountUnit === 'cash' ? '###,###,###,###' : '##%'
    },
    // Calculate paymentAmount
    paymentAmount() {
      const discountAmount = this.getActiveOrder.discountUnit === '%'
        // Discount by percentage
        ? parseInt(this.getActiveOrder.provisionalAmount) / 100 * parseInt(this.getActiveOrder.discount)
        // Discount by plain value
        : parseInt(this.getActiveOrder.discount)

      return `${parseInt(this.getActiveOrder.provisionalAmount) - Math.round(discountAmount)}`
    },

    // "Order" store
    ...mapState(useOrderStore, ['getActiveOrder'])
  },

  watch: {
    // Update order's payment amount when computed "payment amount" is changed
    paymentAmount(value) {
      this.getActiveOrder.paymentAmount = value
    },
    // Reset discount if discountUnit is changed
    'getActiveOrder.discountUnit'() {
      this.getActiveOrder.discount = '0'
    },
    // Control discount max value
    'getActiveOrder.discount'(value) {
      // Get int value of discount
      const intValue = parseInt(value)

      // Check for max base on discountUnit
      if (this.getActiveOrder.discountUnit === '%' && intValue > 100) {
        this.$nextTick(() => this.getActiveOrder.discount = '100')
      } else if (this.getActiveOrder.discountUnit === 'cash' && intValue > this.getActiveOrder.provisionalAmount) {
        this.$nextTick(() => this.getActiveOrder.discount = this.getActiveOrder.provisionalAmount)
      }
    }
  },
}
</script>
