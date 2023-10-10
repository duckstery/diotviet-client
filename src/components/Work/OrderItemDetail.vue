<template>
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

    <q-space/>

    <Button icon="fa-solid fa-trash" flat :tooltip="$t('field.remove')" size="sm" @click="onRemoveItem"/>
    <Button icon="fa-solid fa-pen" flat :tooltip="$t('field.note')" size="sm" @click="onNote"/>
  </div>
  <!-- Note section -->
  <div v-if="showNote" class="row tw-mt-3">
    <TextField v-model="note" borderless
               type="textarea" class="tw-ml-10 tw-w-3/4" input-class="virtual-scrollbar" required autogrow
               :placeholder="$t('field.note')"/>
  </div>
</template>

<script>
import {mapActions} from "pinia";
import {useOrderStore} from "stores/order";
import {reactive, ref, toRef} from "vue";
import {usePriceControl} from "src/composables/usePriceControl";
import {useRangeControl} from "src/composables/useRangeControl";

import TextField from "components/General/Other/TextField.vue";
import Button from "components/General/Other/Button.vue";

export default {
  name: 'OrderItemDetail',
  components: {TextField, Button},

  props: {
    // Item's value
    value: {
      type: Object,
      default: () => ({
        uid: "",
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
        quantity: '1',
        note: "",
      })
    },

  },

  emits: ['update:value', 'remove'],

  setup(props) {
    // Note section control
    const showNote = ref(typeof props.value === 'string' && props.value !== '')
    // Create bill reactive
    const bill = reactive({
      quantity: null,
      originalPrice: null,
      discount: null,
      discountUnit: null,
    })

    return {
      // Note section control
      showNote: showNote,
      // Bill
      bill: bill, note: ref(''),
      // Put range control on bill.quantity
      ...useRangeControl(toRef(bill, 'quantity'), 99, 1),
      // Put price control on bill.quantity
      ...usePriceControl(bill, 'originalPrice', 'actualPrice'),
    }
  },

  computed: {
    // Calculate total price
    totalPrice() {
      return `${parseInt(this.actualPrice) * parseInt(this.bill.quantity)}`
    }
  },

  watch: {
    // If quantity is changed and committed by range control, update item's data
    rangeControlCommit() {
      this.onEdit()
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
      this.$emit('update:value', this.replaceItem(this.value.uid, {
        note: this.note,
        ...this.value,
        ...this.bill,
        ...{
          actualPrice: this.actualPrice,
          totalPrice: this.totalPrice,
          note: this.note
        }
      }))
    },

    /**
     * Remove item
     */
    onRemoveItem() {
      this.replaceItem(this.value.uid)
      this.$emit('remove')
    },

    onNote() {
      this.showNote = !this.showNote
      this.note = ""
    },

    // "Order" store
    ...mapActions(useOrderStore, ['replaceItem'])
  },
}
</script>
