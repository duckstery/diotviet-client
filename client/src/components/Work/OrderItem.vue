<template>
  <q-card class="order-item hover:tw-border-sky-400 tw-border-transparent tw-border-solid tw-cursor-pointer" flat>
    <q-card-section class="">
      <!-- Title section -->
      <div class="row tw-text-[16px]">
        <div class="tw-font-semibold">{{ this.index + 1 }}.&nbsp;</div>
        <div class="tw-overflow-hidden tw-text-ellipsis tw-h-6 tw-max-w-sm tw-line-clamp-1">
          {{ value.title }}
          <q-tooltip class="tw-text-[14px]">{{ value.title }}</q-tooltip>
        </div>
        <div class="tw-ml-2">(Code: {{ value.code }})</div>

        <q-space/>

        <q-chip v-if="value.measureUnit" color="info" text-color="white" class="tw-mr-2">
          {{ value.measureUnit }}
        </q-chip>
        <Button icon="fa-solid fa-trash" text-color="dark" :tooltip="$t('field.remove')" size="sm"
                @click="onRemoveItem"/>
        <Button icon="fa-solid fa-ellipsis-vertical" text-color="dark" :tooltip="$t('field.more')" size="sm"
                @click="onMore"/>
      </div>

      <!-- Detail section -->
      <div class="row tw-mt-0.5">
        <TextField v-model="bill.quantity" compact required type="number" class="tw-ml-10 tw-w-14"
                   :label="$t('field.quantity')"/>
        <TextField
          :model-value="value.actualPrice"

          compact
          required
          readonly
          mask="###,###,###"
          class="tw-ml-10 tw-w-24"
          :label="$t('field.price')"
        >
          <!-- Edit price section -->
          <q-popup-proxy @hide="onResetPrice">
            <q-card>
              <q-card-section>
                <div class="tw-text-lg tw-font-medium">{{ $t('field.edit_price') }}</div>
                <TextField
                  v-model="bill.originalPrice"

                  compact
                  required
                  class="tw-w-36"
                  mask="###,###,###"
                  :label="$t('field.original_price')"
                >
                  <template #before>
                    <q-icon name="fa-solid fa-money-bill-wave" class="tw-mr-1"/>
                  </template>
                </TextField>
                <TextField
                  v-model="bill.discount"

                  compact
                  required
                  icon="fa-solid fa-tag"
                  class="tw-w-36"
                  :mask="discountMask"
                  :label="$t('field.discount')"
                >
                  <template #before>
                    <q-icon name="fa-solid fa-tag" class="tw-mr-1"/>
                  </template>
                </TextField>
                <q-toggle
                  v-model="bill.discountUnit"

                  class="tw-ml-2"
                  true-value="cash"
                  false-value="%"
                  :icon="discountUnitIcon"
                  :label="discountUnitLabel"
                />
                <q-separator class="tw-mt-3"/>
                <TextField
                  :model-value="actualPrice"

                  compact
                  readonly
                  mask="###,###,###"
                  label-color="warning"
                  class="tw-w-36 tw-mt-3"
                  :label="$t('field.actual_price')"
                >
                  <template #before>
                    <q-icon name="fa-solid fa-coins" color="warning" class="tw-mr-1"/>
                  </template>
                </TextField>
              </q-card-section>

              <q-card-actions>
                <Button :label="$t('field.cancel')" flat color="negative" icon="fa-solid fa-xmark" v-close-popup
                        @click="onResetPrice"/>
                <Button :label="$t('field.save')" flat color="positive" icon="fa-solid fa-check" v-close-popup
                        @click="onEdit"/>
              </q-card-actions>
            </q-card>
          </q-popup-proxy>
        </TextField>

        <TextField
          :model-value="value.totalPrice"

          compact
          required
          readonly
          mask="###,###,###,###"
          class="tw-ml-10 tw-w-24"
          input-class="tw-font-semibold"
          :label="$t('field.total')"
        />

        <TextField
          :model-value="value.note"

          compact
          readonly
          class="tw-ml-10 tw-w-24"
          :label="$t('field.note')"
        >
          <q-popup-edit
            buttons
            v-model="note"
            v-slot="scope"
          >
            <q-input
              type="textarea"
              v-model="scope.value"
              autofocus
              counter
              @keyup.enter.stop
              input-class="virtual-scrollbar"
            />
          </q-popup-edit>
        </TextField>

      </div>
    </q-card-section>
  </q-card>
</template>

<script>
import {mapActions} from "pinia";
import {useOrderStore} from "stores/order";

import TextField from "components/General/Other/TextField.vue";
import Button from "components/General/Other/Button.vue";
import usePriceControl from "src/composables/usePriceControl";
import {reactive} from "vue";

export default {
  name: 'OrderItem',
  components: {TextField, Button},

  props: {
    // Item's index
    index: Number,
    // Item's value
    value: {
      type: Object,
      default: () => ({
        id: 1,
        code: '000',
        title: 'Title of cdddd ddddd ddddd ddd dddddddddd ddddddddd'.toUpperCase(),
        measureUnit: 'Kg',
        src: 'https://cdn.quasar.dev/img/parallax2.jpg',
        originalPrice: '50000',
        discount: '10',
        discountUnit: '%',
        actualPrice: '45000',
        totalPrice: '45000',
        quantity: '1'
      })
    }
  },

  setup() {
    // Create bill reactive
    const bill = reactive({
      quantity: null,
      originalPrice: null,
      discount: null,
      discountUnit: null,
    })

    console.warn(bill)
    return {
      bill,
      note: '',
      ...usePriceControl(bill, 'originalPrice', 'actualPrice')
    }
  },

  computed: {
    // Calculate total price
    totalPrice() {
      return `${parseInt(this.actualPrice) * parseInt(this.bill.quantity)}`
    }
  },

  watch: {
    // If quantity is changed, update item's data
    'bill.quantity'(value) {
      // Get value as integer
      const intValue = parseInt(value);

      if (intValue > 99) {
        this.$nextTick(() => this.bill.quantity = '99')
      } else if (intValue < 1) {
        this.$nextTick(() => this.bill.quantity = '1')
      } else {
        this.onEdit()
      }
    },
    // Update item when add note
    note() {
      this.onEdit()
    },
    value: {
      immediate: true,
      handler(value) {
        if (this.bill.quantity === null) {
          // If is not init, try to init
          this.onResetPrice()
        } else if (value['quantity'] !== this.bill.quantity) {
          // Else if quantity is changed by pressing SampleItem in SamplePanel, update bill quantity
          this.bill.quantity = value['quantity']
        }
      }
    }
  },

  methods: {
    /**
     * On remove item event handler
     */
    onRemoveItem() {
      this.removeItem(this.index)
    },

    /**
     * On click "More" event handler
     */
    onMore() {
      this.$notifyWarn('Under development!')
    },

    /**
     * On reset local bill
     */
    onResetPrice() {
      this.bill.originalPrice = this.value.originalPrice
      this.bill.discount = this.value.discount
      this.bill.discountUnit = this.value.discountUnit
      this.bill.quantity = this.value.quantity
    },

    /**
     * On item edit event handler
     */
    onEdit() {
      // Send request to update item's data
      this.editItem(this.index, {
        note: this.note,
        ...this.value,
        ...this.bill,
        ...{
          actualPrice: this.actualPrice,
          totalPrice: this.totalPrice,
          note: this.note
        }
      })
    },

    // "Order" store
    ...mapActions(useOrderStore, ['editItem', 'removeItem'])
  },
}
</script>
