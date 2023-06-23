<template>
  <!-- Group filter -->
  <DynamicFilter v-model="filter.group" :items="groups" :title="$t('field.group')" class="tw-mt-3"
                 @control="$emit('control', ...$event)"/>

  <!-- Status filter -->
  <CheckboxFilter v-model="filter.status" :items="$constant.statuses()" :title="$t('field.status')" class="tw-mt-3"/>

  <!-- Create at filter -->
  <FilterPanel :title="$t('field.created_at')" class="tw-mt-3">
    <DatePicker v-model="filter.createAtFrom" :label="$t('field.from_date')"/>
    <DatePicker v-model="filter.createAtTo" :label="$t('field.to_date')" class="tw-mt-3"/>
  </FilterPanel>

  <!-- Resolved at filter -->
  <FilterPanel :title="$t('field.resolved_at')" class="tw-mt-3">
    <DatePicker v-model="filter.resolvedAtFrom" :label="$t('field.from_date')"/>
    <DatePicker v-model="filter.resolvedAtTo" :label="$t('field.to_date')" class="tw-mt-3"/>
  </FilterPanel>

  <!-- Price range -->
  <FilterPanel :title="$t('field.price_range')" class="tw-mt-3">
    <TextField v-model="debounce.priceFrom.value" :label="$t('field.from')" mask="###,###,###,###"/>
    <TextField v-model="debounce.priceTo.value" :label="$t('field.to')" mask="###,###,###,###" class="tw-mt-3"/>
  </FilterPanel>
</template>

<script>
import FilterPanel from "components/Manage/FilterPanel.vue";
import TextField from "components/General/Other/TextField.vue";
import DatePicker from "components/General/Other/DatePicker.vue";
import DynamicFilter from "components/Manage/DynamicFilter.vue";
import RadioFilter from "components/Manage/RadioFilter.vue";
import CheckboxFilter from "components/Manage/CheckboxFilter.vue";
import {reactive, toRef} from "vue";
import {useDebounceModel} from "src/composables/useDebounceModel";
import {useRangeControl} from "src/composables/useRangeControl";
import {useFilterRequest} from "src/composables/useFilterRequest";

export default {
  name: "OrderFilter",

  components: {CheckboxFilter, RadioFilter, DynamicFilter, DatePicker, TextField, FilterPanel},

  props: {
    modelValue: Object,
    categories: Array,
    groups: Array
  },

  emits: ['request', 'update:modelValue', 'control'],

  setup(props, context) {
    // Filter
    const filter = reactive({
      group: null,
      status: [],
      createAtFrom: null,
      createAtTo: null,
      resolvedAtFrom: null,
      resolvedAtTo: null,
      priceFrom: null,
      priceTo: null,
      lastTransactionAtFrom: null,
      lastTransactionAtTo: null,
      isMale: null
    })
    // Setup filter request
    useFilterRequest(filter, context)

    // Setup debounce model
    const debounce = {
      priceFrom: useDebounceModel(toRef(filter, 'priceFrom')),
      priceTo: useDebounceModel(toRef(filter, 'priceTo'))
    }
    // Setup rangeControl
    useRangeControl(debounce.priceFrom)
    useRangeControl(debounce.priceTo)

    return {filter, debounce}
  },
}
</script>
