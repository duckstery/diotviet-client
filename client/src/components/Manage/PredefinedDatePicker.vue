<template>
  <PopupTextField :value="pickedOption === '' ? '' : $t(`field.${pickedOption}`)">
    <template #default="{hide}">
      <div class="tw-flex tw-space-x-1 tw-px-1 tw-py-4">
        <template v-for="(col, index) in structure" :key="col.key">
          <div>
            <div class="tw-shrink-0 tw-font-semibold tw-mb-2 tw-text-center">{{ $t(`field.by_${col.key}`) }}</div>
            <RadioList :model-value="pickedOption" :options="col.options" @update:model-value="onPick($event, hide)"/>
          </div>
          <q-separator v-if="index < 4" vertical inset/>
        </template>
      </div>
    </template>
  </PopupTextField>
</template>

<script>
import Button from "components/General/Other/Button.vue";
import PopupTextField from "components/General/Other/PopupTextField.vue";

import {computed, ref, watch} from "vue";
import {useVModel} from "@vueuse/core";
import {dayjs, util} from "src/boot"
import {useI18n} from "vue-i18n";
import RadioList from "components/General/Other/RadioList.vue";

export default {
  name: "PredefinedDatePicker",

  components: {RadioList, PopupTextField, Button},

  props: {
    modelValue: Object | String,
  },

  emits: ['update:modelValue'],

  setup(props, {emit}) {
    // i18n
    const $t = useI18n().t

    // Model
    const model = useVModel(props, 'modelValue', emit)
    // If model turn to null, it means that it should be reloaded
    watch(model, value => value === null && (pickedOption.value = ''))

    // Convert string to a radio option
    const toRadio = (value) => ({value: value, label: $t(`field.${value}`)})
    // Predefined option structure
    const structure = computed(() => ([
      {key: 'day', options: ['day_now', 'day_prev', 'day_backward_2'].map(toRadio)},
      {key: 'week', options: ['week_now', 'week_prev', 'week_backward_7'].map(toRadio)},
      {key: 'month', options: ['month_now', 'month_prev', 'month_backward_30'].map(toRadio)},
      {key: 'quarter', options: ['quarter_now', 'quarter_prev'].map(toRadio)},
      {key: 'year', options: ['year_now', 'year_prev'].map(toRadio)},
    ]))

    // Picked option
    const pickedOption = ref(util.isUnset(util.nullIfEmpty(props.modelValue)) ? '' : 'month_now')
    // On picking an option
    const onPick = (value) => pickedOption.value = value
    // Watch for option changed
    watch(pickedOption, value => {
      // Check if value is unset or empty
      if (util.isUnset(util.nullIfEmpty(value))) {
        model.value = null
        return
      }

      // Split option to get [unit]-[moment]
      const [unit, moment, days] = value.split('_')
      // Get current dayjs
      const now = dayjs()
      // Create output
      let output = {from: null, to: now.format('YYYY-MM-DD')}

      if (moment === 'now') {
        // Model will be a single Date string
        output.from = now.startOf(unit).format('YYYY-MM-DD')
      } else if (moment === 'backward') {
        // Subtract the [days] days from now
        output.from = now.subtract(parseInt(days), "day").format('YYYY-MM-DD')
      } else if (moment === 'prev') {
        // Get the start of previous [unit]
        output.from = now.startOf(unit).add(3 - moment.length, unit).format('YYYY-MM-DD')
      }

      // Assign model
      model.value = output
    })

    return {
      // Predefined option
      pickedOption: pickedOption, structure: structure,
      onPick: onPick,
    }
  }
}
</script>
