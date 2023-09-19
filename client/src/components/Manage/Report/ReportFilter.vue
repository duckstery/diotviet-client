<template>
  <!-- Chart type -->
  <FilterPanel :title="$t('field.chart_type')" class="tw-mt-3">
    <RadioList v-model="chartType" :options="$constant.chartTypes()"/>
  </FilterPanel>

  <!-- Resolved at range -->
  <DateFilter v-model:from="range.from" v-model:to="range.to" class="tw-mt-3"
              :title="$t('field.time')"/>
  <!-- Chart type -->
  <FilterPanel :title="$t('field.display_mode')" class="tw-mt-3">
    <RadioList :model-value="displayBy" :options="displayMode" @update:model-value="changeDisplayMode($event)"/>
  </FilterPanel>
</template>

<script>
import FilterPanel from "components/Manage/FilterPanel.vue";
import DatePicker from "components/General/Other/DatePicker.vue";
import DateFilter from "components/Manage/DateFilter.vue";
import RadioFilter from "components/Manage/RadioFilter.vue";
import RadioList from "components/General/Other/RadioList.vue";

import {computed, reactive, ref, watch} from "vue";
import {useI18n} from "vue-i18n";
import {dayjs, util} from "src/boot";
import CheckboxFilter from "components/Manage/CheckboxFilter.vue";
import {useVModel} from "@vueuse/core";

export default {
  name: "ReportFilter",

  components: {CheckboxFilter, RadioList, RadioFilter, DateFilter, DatePicker, FilterPanel},

  props: {
    modelValue: {
      type: Object,
      default: () => ({})
    },
    type: String,
    categories: Array,
    groups: Array
  },

  emits: ['request', 'update:model-value', 'control'],

  setup(props, {emit}) {
    // i18n
    const $t = useI18n().t

    // Chart type
    const chartType = useVModel(props, 'type', emit)

    // Date range
    const range = reactive({from: props.modelValue.from, to: props.modelValue.to})
    // Watch range change
    watch(range, () => {
      // If month diff is more than 1 month, auto change displayMode to month
      if (isMoreThanAMonth.value) {
        displayBy.value = 'month'
      }
      // Emit model
      emit('update:model-value', {...range, displayMode: displayBy.value})
    })

    // Display by
    const displayBy = ref(props.modelValue.displayMode)
    // Watch displayBy
    const changeDisplayMode = (value) => {
      // Check if user try to change displayMode to 'date'
      if (isMoreThanAMonth.value && value === 'date') {
        // Confirm if user want to change mode
        util.promptConfirm($t('message.date_range_to_big'))
            .onOk(() => {
              // Emit model
              displayBy.value = value
              emit('update:model-value', {...range, displayMode: value})
            })
      } else {
        // Emit model
        displayBy.value = value
        emit('update:model-value', {...range, displayMode: value})
      }
    }

    // Month diff between [range.from] and [range.to]
    const isMoreThanAMonth = computed(() => dayjs(range.to).diff(dayjs(range.from), 'month') > 1)

    return {
      // Chart type
      chartType: chartType,
      // Model
      range: range,
      // Display mode
      displayBy: displayBy, changeDisplayMode: changeDisplayMode,
      displayMode: computed(() => [
        {value: 'date', label: $t('field.by_date')},
        {value: 'month', label: $t('field.by_month')}
      ])
    }
  }
}
</script>
