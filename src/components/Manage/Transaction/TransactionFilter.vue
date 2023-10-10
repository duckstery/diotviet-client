<template>
  <!-- Type -->
  <RadioFilter v-model="filter.type" class="tw-mt-3" :title="$t('field.type')"
               :true-label="$t('field.collect')" :false-label="$t('field.spend')"/>

  <!-- Create at filter -->
  <DateFilter v-model:from="filter.createAtFrom" v-model:to="filter.createAtTo" class="tw-mt-3"
              :title="$t('field.created_at')"/>

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

import {reactive, toRef} from "vue";
import {useDebounceModel} from "src/composables/useDebounceModel";
import {useRangeControl} from "src/composables/useRangeControl";
import {useFilterRequest} from "src/composables/useFilterRequest";
import RadioFilter from "components/Manage/RadioFilter.vue";
import _ from "lodash";
import DateFilter from "components/Manage/DateFilter.vue";

export default {
  name: "TransactionFilter",

  components: {DateFilter, RadioFilter, DatePicker, TextField, FilterPanel},

  props: {
    modelValue: Object,
  },

  emits: ['request', 'update:modelValue'],

  setup(props, context) {
    // Filter
    const filter = reactive(_.defaultsDeep({}, props.modelValue, {
      createAtFrom: null,
      createAtTo: null,
      priceFrom: null,
      priceTo: null,
    }))
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
