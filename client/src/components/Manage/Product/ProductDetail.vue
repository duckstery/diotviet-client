<template>
  <q-card flat class="tw-h-full" :style="`width: ${width}px`">
    <q-card-section>
      <Skeleton v-model="isReady" height="28px" width="100px">
        <div class="tw-text-lg tw-font-semibold text-primary">
          {{ detail.title ?? 'Title' }}
        </div>
      </Skeleton>
      <div class="tw-mt-2 tw-flex tw-text-sm">
        <Skeleton v-model="isReady" height="20px" width="200px">
          <div v-for="key in ['canBeAccumulated', 'isInBusiness']">
            <div class="tw-flex tw-mr-3">
              <q-icon
                :name="`fa-solid fa-${detail[key] ? 'check' : 'xmark'}`"
                :color="detail[key] ? 'positive' : 'negative'"
                class="tw-mt-1"
              />
              <div class="tw-ml-2">{{ $t(`field.${$util.camelToSnake(key)}`) }}</div>
            </div>
          </div>
        </Skeleton>
      </div>
      <div class="row">
        <!-- Image -->
        <div class="tw-mt-3 col-12 col-lg-4 col-md-6 tw-px-1.5">
          <Skeleton v-model="isReady" height="300px">
            <q-img
              no-native-menu
              no-spinner
              no-transition
              width="300"
              height="300"
              style="max-width: 300px; max-height: 300px"
              :src="this.detail.src"
            />
          </Skeleton>
        </div>
        <!-- Primary info -->
        <div class="tw-mt-3 col-12 col-lg-4 col-md-6 tw-px-1.5">
          <template v-for="key in ['code', 'category', 'groups']">
            <Skeleton v-model="isReady" height="30px" skeleton-class="tw-mt-2.5">
              <DisplayField
                :modelValue="detail[key]"
                :src="`images/${key}.png`"
                :label="$t(`field.${key}`)"
              />
            </Skeleton>
          </template>

          <div class="tw-mt-5"/>
          <template v-for="key in ['measureUnit', 'weight', 'originalPrice', 'actualPrice']">
            <Skeleton v-model="isReady" height="30px" skeleton-class="tw-mt-2.5">
              <DisplayField
                space
                inner-class="tw-w-48"
                :mask="key === 'measureUnit' || key === 'weight' ? '' : '###,###,###,###'"
                :suffix="key === 'weight' ? 'Kg' : ''"
                :modelValue="detail[key]"
                :src="`images/${$util.camelToSnake(key)}.png`"
                :label="$t(`field.${$util.camelToSnake(key)}`)"
              />
            </Skeleton>
          </template>
          <Skeleton v-model="isReady" height="30px" skeleton-class="tw-mt-2.5">
            <DisplayField
              :modelValue="discountAmount"
              space
              inner-class="tw-w-48"
              :src="'images/discount.png'"
              :label="$t(`field.discount`)"
            />
          </Skeleton>
        </div>
        <!-- Secondary info -->
        <div class="tw-mt-3 col-12 col-lg-4 tw-px-1.5">
          <Skeleton v-model="isReady" height="300px">
            <DisplayField
              :modelValue="this.detail.description"
              textarea
              horizontal
              :src="'images/note.png'"
              :label="$t(`field.description`)"
              textarea-height="248"
              textarea-length="100"
            />
          </Skeleton>
        </div>
      </div>
    </q-card-section>
    <q-card-section class="tw-flex tw-pt-0">
      <q-space/>
      <Skeleton v-model="isReady" height="40px" width="300px" skeleton-class="tw-w-full">
        <Button :label="$t('field.history')" icon="fa-solid fa-clock-rotate-left"
                stretch color="info" class="tw-ml-2" no-caps @click="request('history')"/>
        <q-separator class="tw-ml-2" inset vertical/>
        <Button :label="$t('field.edit')" icon="fa-solid fa-pen-to-square"
                stretch color="primary" class="tw-ml-2" no-caps @click="request('update')"/>
        <Button :label="$t('field.copy')" icon="fa-solid fa-copy"
                stretch color="positive" class="tw-ml-2" no-caps @click="request('copy')"/>
        <q-separator class="tw-ml-2" inset vertical/>
        <Button v-for="operation in statusOperations"
                :label="$t(`field.${operation.key}`)" :icon="`fa-solid ${operation.icon}`"
                stretch :color="operation.color" class="tw-ml-2" no-caps @click="request(operation.key, true)"/>
        <Button :label="$t('field.delete')" icon="fa-solid fa-trash"
                stretch color="negative" class="tw-ml-2" no-caps @click="request('delete', true)"/>
      </Skeleton>
    </q-card-section>
  </q-card>
</template>

<script>
import DisplayField from "components/General/Other/DisplayField.vue";
import Button from "components/General/Other/Button.vue";
import Skeleton from "components/General/Other/Skeleton.vue";

export default {
  name: 'ProductDetail',

  components: {Skeleton, Button, DisplayField},

  props: {
    // Product props
    item: {
      type: Object,
      default: () => ({})
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

  emits: ['request'],

  data: () => ({
    detail: {},
  }),

  computed: {
    // Check if component is ready to display data
    isReady() {
      return !(this.$util.isUnset(this.detail) || Object.keys(this.detail).length === 0)
    },
    // Get item id
    getItemId() {
      return this.item.find(col => col.name === 'id').value
    },
    // Icon of discountUnit switch
    discountUnitIcon() {
      return 'fa-solid ' + (this.detail.discountUnit === 'cash' ? 'fa-money-bill-wave' : 'fa-percent')
    },
    // Discount amount base on discount unit
    discountAmount() {
      // Create output holder
      let output = ''

      try {
        if (this.detail.discountUnit === 'cash') {
          // Calculate estimated percentage
          const estimatedPercentage = Math.floor(parseInt(this.detail.discount) / parseInt(this.detail.originalPrice) * 100)
          // Set up output
          output = `${this.$util.formatMoney(this.detail.discount)} (~${estimatedPercentage}%)`
        } else if (this.detail.discountUnit === '%') {
          // Calculate estimated amount
          const estimatedAmount = Math.floor(parseInt(this.detail.originalPrice) / 100 * parseInt(this.detail.discount))
          // Set up output
          output = `${this.detail.discount}% (~${this.$util.formatMoney(estimatedAmount.toString())})`
        }
      } catch (e) {
      }

      return output
    },
    // Dynamic status operation
    statusOperations() {
      return [
        this.detail.isInBusiness
          ? {key: 'stop_business', icon: 'fa-ban', color: 'negative'}
          : {key: 'start_business', icon: 'fa-check', color: 'positive'},
        this.detail.canBeAccumulated
          ? {key: 'stop_accumulating', icon: 'fa-stop', color: 'negative'}
          : {key: 'start_accumulating', icon: 'fa-play', color: 'positive'}
      ]
    }
  },

  watch: {
    /**
     * Trigger each time component is active
     */
    active(value) {
      // If component is active, fetch data
      if (value) {
        this.fetch()
      }
    }
  },

  methods: {
    /**
     * Fetch item detail
     */
    async fetch() {
      this.$axios.get(`/product/${this.getItemId}`, {loading: false})
        .then(res => this.detail = res.data.payload)
    },
    /**
     * Request an operation
     *
     * @param {string} key
     * @param {boolean} asMany
     */
    request(key, asMany = false) {
      this.$emit('request', key, asMany ? [this.getItemId] : this.getItemId)
    }
  }
}
</script>
