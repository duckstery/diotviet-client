<template>
  <!-- Category filter -->
  <CheckboxFilter v-model="filter.categories" :items="categories" :title="$t('field.category')" class="tw-mt-3"/>

  <!-- Group filter -->
  <DynamicFilter v-model="filter.group" :items="groups" :title="$t('field.group')" class="tw-mt-3"
                 @control="$emit('control', ...$event)"/>

  <!-- Price range filter -->
  <FilterPanel :title="$t('field.price_range')" class="tw-mt-3">
    <TextField v-model="debounce.priceFrom.value" :label="$t('field.from')" mask="###,###,###,###"/>
    <TextField v-model="debounce.priceTo.value" :label="$t('field.to')" mask="###,###,###,###" class="tw-mt-3"/>
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
import {reactive, toRef} from "vue";
import {useDebounceModel} from "src/composables/useDebounceModel";
import {useRangeControl} from "src/composables/useRangeControl";
import {useFilterRequest} from "src/composables/useFilterRequest";

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

  emits: ['request', 'update:model-value', 'control'],

  setup(props, context) {
    // Filter
    const filter = reactive({
      categories: [],
      group: null,
      minPrice: '',
      maxPrice: '',
      canBeAccumulated: null,
      isInBusiness: null
    })
    // Setup filter request
    useFilterRequest(filter, context)

    // Setup debounce model
    const debounce = {
      priceFrom: useDebounceModel(toRef(filter, 'minPrice')),
      priceTo: useDebounceModel(toRef(filter, 'maxPrice'))
    }
    // Setup rangeControl
    useRangeControl(debounce.priceFrom)
    useRangeControl(debounce.priceTo)

    return {filter, debounce}
  }
}
</script>
