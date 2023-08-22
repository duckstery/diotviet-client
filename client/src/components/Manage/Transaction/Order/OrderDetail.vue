<template>
  <q-card flat class="tw-h-full" :style="`width: ${width}px`">
    <q-card-section>
      <Skeleton v-model="isReady" height="28px" width="100px">
        <div class="tw-text-lg tw-font-semibold text-primary">
          {{ $t('field.information') }}
        </div>
      </Skeleton>
      <div class="row">
        <!-- Primary info -->
        <div class="tw-mt-3 col-12 col-lg-4 col-md-6 tw-px-1.5">
          <template v-for="key in ['code', 'groups', 'customer']">
            <Skeleton v-model="isReady" height="30px" skeleton-class="tw-mt-2.5">
              <DisplayField
                :modelValue="detail[key]"
                :src="`/images/${key}.png`"
                :label="$t(`field.${key}`)"
              />
            </Skeleton>
          </template>
          <template v-for="key in ['address', 'phoneNumber', 'email']">
            <Skeleton v-model="isReady" height="30px" skeleton-class="tw-mt-2.5">
              <DisplayField
                interactive
                :modelValue="detail[key]"
                :src="`/images/${$util.camelToSnake(key)}.png`"
                :label="$t(`field.${$util.camelToSnake(key)}`)"
                @interact="onInteract(key, detail[key])"
              />
            </Skeleton>
          </template>
        </div>
        <!-- Secondary info -->
        <div class="tw-mt-3 col-12 col-lg-4 col-md-6 tw-px-1.5">
          <Skeleton v-model="isReady" height="30px" skeleton-class="tw-mt-2.5">
            <DisplayField
              custom
              src="/images/info.png"
              :label="$t('field.status')"
            >
              <OrderStatus :value="detail.status" class="tw-my-auto tw-pt-3 tw-ml-3"/>
            </DisplayField>
          </Skeleton>
          <template v-for="key in ['point', 'createdBy', 'createdAt', 'resolvedAt']">
            <Skeleton v-model="isReady" height="30px" skeleton-class="tw-mt-2.5">
              <DisplayField
                :modelValue="detail[key]"
                :src="`/images/${$util.camelToSnake(key)}.png`"
                :label="$t(`field.${$util.camelToSnake(key)}`)"
              />
            </Skeleton>
          </template>
        </div>
        <!-- Note -->
        <div class="tw-mt-3 col-12 col-lg-4 tw-px-1.5">
          <Skeleton v-model="isReady" height="300px">
            <DisplayField
              :modelValue="this.detail.note"
              textarea
              horizontal
              :src="'/images/note.png'"
              :label="$t(`field.note`)"
              textarea-height="185"
              textarea-length="100"
            />
          </Skeleton>
        </div>
      </div>
      <div class="row">
        <div class="tw-mt-6 col-12 tw-px-1.5">
          <Skeleton v-model="isReady" height="220px" skeleton-class="tw-mt-2.5">
            <MarkupTable :headers="itemHeaders" :items="items"/>
          </Skeleton>
        </div>
      </div>
      <div class="row">
        <q-space v-if="$q.screen.gt.sm"/>
        <div class="tw-mt-6 col-lg-4 col-12 tw-px-1">
          <template v-for="key in ['totalQuantity', 'provisionalAmount', 'discount', 'paymentAmount']">
            <Skeleton v-model="isReady" height="30px" skeleton-class="tw-mt-2.5">
              <DisplayField
                space
                mask="###,###,###,###"
                :modelValue="detail[key]"
                :src="`/images/${$util.camelToSnake(key)}.png`"
                :label="$t(`field.${$util.camelToSnake(key)}`)"
              />
            </Skeleton>
          </template>
        </div>
      </div>
    </q-card-section>
    <q-card-section v-if="!$constant.isStatusAborted(detail.status)" class="tw-flex tw-pt-0">
      <q-space/>
      <Skeleton v-model="isReady" height="40px" width="300px" skeleton-class="tw-w-full">
        <Button :label="$t('field.history')" icon="fa-solid fa-clock-rotate-left"
                stretch color="secondary" class="tw-ml-2" no-caps @click="request('history')"/>
        <Button :label="$t('field.print')" icon="fa-solid fa-print"
                stretch color="secondary" class="tw-ml-2" no-caps @click="onPrint"/>
        <template v-if="!$constant.isStatusResolved(detail.status)">
          <q-separator class="tw-ml-2" inset vertical/>
          <Button :label="$t('field.process')" icon="fa-solid fa-circle-dot"
                  stretch color="info" class="tw-ml-2" no-caps @click="onHandleOrder"/>
          <Button :label="$t('field.resolve')" icon="fa-solid fa-circle-check"
                  stretch color="positive" class="tw-ml-2" no-caps
                  @click="onHandleOrder"/>
        </template>
        <q-separator class="tw-ml-2" inset vertical/>
        <Button :label="$t('field.abort')" icon="fa-solid fa-circle-stop"
                stretch color="negative" class="tw-ml-2" no-caps
                @click="onHandleOrder"/>
      </Skeleton>
    </q-card-section>
  </q-card>
</template>

<script>
import DisplayField from "components/General/Other/DisplayField.vue";
import Button from "components/General/Other/Button.vue";
import Skeleton from "components/General/Other/Skeleton.vue";
import OrderStatus from "components/Manage/Constant/OrderStatus.vue";
import MarkupTable from "components/Manage/MarkupTable.vue";

import {toRefs} from "vue";
import {usePageRowDetail} from "src/composables/usePageRowDetail";
import {useInteractiveField} from "src/composables/useInteractiveField";
import {useDiscountCalculator} from "src/composables/useDiscountCalculator";
import {useOrderProcessor} from "src/composables/useOrderProcessor";
import {error} from "src/boot";

export default {
  name: 'OrderDetail',

  components: {MarkupTable, OrderStatus, Skeleton, Button, DisplayField},

  props: {
    // Product props
    item: {
      type: Object,
      default: () => ({
        items: []
      })
    },
    // Width
    width: {
      type: Number,
      default: 500
    },
    // Active status
    active: {
      type: Boolean,
      default: false
    }
  },

  inject: ['printer'],

  emits: ['request'],

  setup(props, context) {
    // Use page row detail
    const pageRowDetail = usePageRowDetail(toRefs(props), context)

    return {
      ...pageRowDetail,
      ...useInteractiveField(),
      getDiscountAmount: useDiscountCalculator(),
      onHandleOrder: useOrderProcessor(pageRowDetail.detail),
    }
  },

  computed: {
    // Headers of item
    itemHeaders() {
      return [
        {name: 'code', field: 'code', align: 'left', sortable: false},
        {name: 'title', field: 'title', align: 'left', sortable: false},
        {name: 'quantity', field: 'quantity', align: 'right', sortable: false},
        {name: 'originalPrice', field: 'originalPrice', align: 'right', format: this.$util.formatMoney, sortable: false},
        {name: 'discount', field: 'discount', align: 'right', format: this.$util.formatMoney, sortable: false},
        {name: 'actualPrice', field: 'actualPrice', align: 'right', format: this.$util.formatMoney, sortable: false},
        {name: 'totalPrice', field: 'totalPrice', align: 'right', format: this.$util.formatMoney, sortable: false},
      ]
    },
    // Preprocessed item
    items() {
      // Return blank Array if detail or detail's items is unset
      if (this.$util.isUnset(this.detail) || this.$util.isUnset(this.detail.items)) {
        return []
      }

      // Preprocess
      let preprocessedItem = this.detail.items.map(item => ({
        ...item,
        discount: this.getDiscountAmount(item.originalPrice, item.discountUnit, item.discount),
        totalPrice: `${parseInt(item.actualPrice) * item.quantity}`,
      }))

      return this.$_.sortBy(preprocessedItem, o => o.code)
    },
  },

  watch: {
    items() {
      // Setup some data
      this.detail.totalQuantity = `${this.items.reduce((total, item) => total + item.quantity, 0)}`
      this.detail.discount = `${this.getDiscountAmount(this.detail.provisionalAmount, this.detail.discountUnit, this.detail.discount)}`
    }
  },

  methods: {
    onPrint() {
      this.$axios.get(`/order/print/${this.getItemId}`).then(this.printer.print).catch(error.any)
    },
  }
}
</script>
