<template>
  <!-- Rank -->
  <FilterPanel :title="$t('field.top')" class="tw-mt-3">
    <RadioList v-model="targetModel" :options="targetOptions"/>
  </FilterPanel>

  <!-- Resolved at range -->
  <DateFilter v-model:from="model.from" v-model:to="model.to" class="tw-mt-3"
              :title="$t('field.time')"/>

  <!-- Sort -->
  <FilterPanel :title="$t('field.sort')" class="tw-mt-3">
    <RadioList v-model="model.sort" :options="sortOptions"/>
  </FilterPanel>
</template>

<script>
import FilterPanel from "components/Manage/FilterPanel.vue";
import DatePicker from "components/General/Other/DatePicker.vue";
import DateFilter from "components/Manage/DateFilter.vue";
import RadioFilter from "components/Manage/RadioFilter.vue";
import RadioList from "components/General/Other/RadioList.vue";
import CheckboxFilter from "components/Manage/CheckboxFilter.vue";

import {computed} from "vue";
import {useI18n} from "vue-i18n";
import {useVModel} from "@vueuse/core";

export default {
  name: "RankFilter",

  components: {CheckboxFilter, RadioList, RadioFilter, DateFilter, DatePicker, FilterPanel},

  props: {
    modelValue: Object,
    target: String,
  },

  emits: ['update:model-value', 'update:target'],

  setup(props, {emit}) {
    // i18n
    const $t = useI18n().t

    // Model
    const model = useVModel(props, 'modelValue', emit)
    const targetModel = useVModel(props, 'target', emit)

    // Method options
    const targetOptions = computed(() => [
      {value: 'product', label: $t('field.product')},
      {value: 'customer', label: $t('field.customer')},
    ])
    // Sort options
    const sortOptions = computed(() => [
      {value: 1, label: $t('field.asc')},
      {value: -1, label: $t('field.desc')},
    ])
    // Top options
    const topOptions = computed(() => [
      {value: 10, label: $t('field.top_number', {attr: 10})},
      {value: 100, label: $t('field.top_number', {attr: 100})}
    ])

    return {
      // Model
      model: model, targetModel: targetModel,
      // Options
      targetOptions: targetOptions,
      sortOptions: sortOptions,
      topOptions: topOptions
    }
  }
}
</script>
