<template>
  <FilterPanel :title="title" reloadable @reload="reload">
    <q-radio v-model="optionProvider" size="xs" val="1" class="tw-w-full" @click="invoke('predefined')">
      <PredefinedDatePicker v-model="predefinedOption" ref="predefined"/>
    </q-radio>
    <q-radio v-model="optionProvider" size="xs" val="2" class="tw-w-full tw-mt-3" @click="invoke('custom')">
      <DatePicker v-model="customOption" ref="custom" range min-year-month="2000/01"/>
    </q-radio>
  </FilterPanel>
</template>

<script>
import FilterPanel from "components/Manage/FilterPanel.vue";
import TextField from "components/General/Other/TextField.vue";
import DatePicker from "components/General/Other/DatePicker.vue";
import PredefinedDatePicker from "components/Manage/PredefinedDatePicker.vue";

import {ref, watch} from "vue";
import {templateRef, useVModel} from "@vueuse/core";
import {useApplyDateOption} from "src/composables/useApplyDateOption";
import {util} from "src/boot";

export default {
  name: "DateFilter",

  components: {PredefinedDatePicker, DatePicker, TextField, FilterPanel},

  props: {
    // From (model)
    from: String,
    // To (model),
    to: String,
    // Title
    title: String,
    // Exclude
    exclude: Array
  },

  emits: ['update:from', 'update:to'],

  setup(props, {emit}) {
    // Final date range
    const fromDate = useVModel(props, 'from', emit)
    const toDate = useVModel(props, 'to', emit)

    // Option 1 (by predefined pick) value
    const {option: predefinedOption, applyModel: applyPredefined} = useApplyDateOption(fromDate, toDate)
    // Option 2 (by custom pick) value
    const {option: customOption, applyModel: applyCustom} = useApplyDateOption(fromDate, toDate)

    // Option provider
    const optionProvider = ref(util.isUnset(predefinedOption.value) && util.isUnset(customOption.value) ? null : '1')
    // Watch for provider changed
    watch(optionProvider, value => value === '1' ? applyPredefined() : applyCustom())

    // Template ref
    const $refs = {
      predefined: templateRef('predefined'),
      custom: templateRef('custom')
    }

    // Backup
    const backup = {
      fromDate: fromDate.value,
      toDate: toDate.value,
      optionProvider: optionProvider.value,
      predefinedOption: predefinedOption.value,
      customOption: customOption.value
    }
    // Reload data
    const reload = () => {
      fromDate.value = backup.fromDate
      toDate.value = backup.toDate
      optionProvider.value = backup.optionProvider
      predefinedOption.value = backup.predefinedOption
      customOption.value = backup.customOption
    }

    return {
      // Predefined option
      predefinedOption: predefinedOption,
      // Custom option
      customOption: customOption,
      // Option provider
      optionProvider: optionProvider,
      // Invoke Picker
      invoke: (key) => $refs[key].value.$el.click(),
      // Reload data
      reload: reload
    }
  }
}
</script>
