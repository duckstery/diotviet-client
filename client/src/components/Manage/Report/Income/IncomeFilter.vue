<template>
  <!-- Resolved at filter -->
  <DateFilter v-model:from="model.from" v-model:to="model.to" class="tw-mt-3"
              :title="$t('field.time')"/>
</template>

<script>
import FilterPanel from "components/Manage/FilterPanel.vue";
import DatePicker from "components/General/Other/DatePicker.vue";

import {reactive, watch} from "vue";
import DateFilter from "components/Manage/DateFilter.vue";
import RadioFilter from "components/Manage/RadioFilter.vue";
import {useVModel} from "@vueuse/core";

export default {
  name: "IncomeFilter",

  components: {RadioFilter, DateFilter, DatePicker, FilterPanel},

  props: {
    modelValue: {
      type: Object,
      default: () => ({})
    },
    categories: Array,
    groups: Array
  },

  emits: ['request', 'update:model-value', 'control'],

  setup(props, {emit}) {
    return {model: useVModel(props, 'modelValue', emit)}
  }
}
</script>
