<template>
  <!-- Category filter -->
  <CheckboxFilter v-model="filter.categories" :items="categories" :title="$t('field.category')" class="tw-mt-3" />

  <!-- Group filter -->
  <DynamicFilter v-model="filter.group" :title="$t('field.group')" :items="groups" class="tw-mt-3"/>

  <!-- Price range filter -->
  <FilterPanel :title="$t('field.price_range')" class="tw-mt-3">
    <q-scroll-area class="tw-h-[130px] tw-mt-3">
      <q-item v-for="range in priceRanges" tag="label" dense>
        <q-item-section avatar>
          <q-radio :model-value="currentPriceRange" :val="`${range.min}-${range.max}`" color="primary" dense
                   @update:model-value="onChangePriceRange"/>
        </q-item-section>
        <q-item-section>
          <q-item-label>
            <span v-if="range.max === ''">
              {{ $t('field.all') }}
            </span>
            <span v-else>
              {{ $t('message.from_to', {from: $util.formatMoney(range.min), to: $util.formatMoney(range.max)}) }}
            </span>
          </q-item-label>
        </q-item-section>
      </q-item>
    </q-scroll-area>
  </FilterPanel>

  <!-- Can be accumulated -->
  <RadioFilter v-model="filter.canBeAccumulated" :title="$t('field.can_be_accumulated')" class="tw-mt-3"/>

  <!-- Is in business -->
  <RadioFilter v-model="filter.isInBusiness" :title="$t('field.is_in_business')" class="tw-mt-3"/>
</template>

<script>
import FilterPanel from "components/Manage/FilterPanel.vue";
import TextField from "components/General/Other/TextField.vue";
import DynamicFilter from "components/Manage/DynamicFilter.vue";
import RadioFilter from "components/Manage/RadioFilter.vue";
import CheckboxFilter from "components/Manage/CheckboxFilter.vue";

export default {
  name: "ProductFilter",

  components: {CheckboxFilter, RadioFilter, DynamicFilter, TextField, FilterPanel},

  props: {
    modelValue: Object,
    categories: Array,
    groups: Array
  },

  computed: {
    // Get price range
    priceRanges() {
      return [
        {min: '', max: ''},
        {min: '0', max: '10000'},
        {min: '10000', max: '100000'},
        {min: '100000', max: '1000000'}
      ]
    },
    // Get current price range
    currentPriceRange() {
      return `${this.modelValue.minPrice}-${this.modelValue.maxPrice}`
    },
  },

  data: () => ({
    // Filter
    filter: {
      categories: [],
      group: null,
      minPrice: '',
      maxPrice: '',
      canBeAccumulated: null,
      isInBusiness: null
    },
  }),

  emits: ['request', 'update:modelValue'],

  watch: {
    // Watch to emit filter event
    filter: {
      deep: true,
      handler(value) {
        this.$emit('update:modelValue', value)
        this.$emit('request')
      }
    }
  },

  methods: {
    /**
     * On change price range
     *
     * @param value
     */
    onChangePriceRange(value) {
      // Split by -
      const range = value.split('-');

      // Set value to filter
      this.filter.minPrice = this.$util.nullIfEmpty(range[0])
      this.filter.maxPrice = this.$util.nullIfEmpty(range[1])
    }
  }
}
</script>

<style scoped>

</style>
