<template>
  <!-- Create at filter -->
  <DateFilter v-model:from="model.from" v-model:to="model.to" class="tw-mt-3"
              :title="$t('field.created_at')"/>

  <!--  <FilterPanel :title="title">-->
  <!--    <RadioList :options="displayModeOptions" :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)"/>-->
  <!--  </FilterPanel>-->
</template>

<script>
import FilterPanel from "components/Manage/FilterPanel.vue";
import TextField from "components/General/Other/TextField.vue";
import DateFilter from "components/Manage/DateFilter.vue";
import RadioList from "components/General/Other/RadioList.vue";

import {computed} from "vue";
import {useVModel} from "@vueuse/core";

export default {
  name: "HistoryFilter",

  components: {RadioList, DateFilter, TextField, FilterPanel},

  props: {
    // Model
    modelValue: Object,
  },

  emits: ['update:modelValue'],

  setup(props, {emit}) {
    // Model
    const model = useVModel(props, 'modelValue', emit)

    // Display mode
    const displayModeOptions = computed(() => [
      {value: 'date', label: $t('field.by_date')},
      {value: 'month', label: $t('field.by_month')}
    ])

    return {
      model: model,
      displayModeOptions: displayModeOptions
    }
  },
}
</script>
