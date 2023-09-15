<template>
  <PopupTextField :value="pickedOption === '' ? '' : $t(`field.${pickedOption}`)">
    <template #default="{hide}">
      <q-card>
        <q-card-section>
          <div class="tw-flex tw-space-x-6">
            <div v-for="col in structure" :key="col.key">
              <div class="tw-shrink-0 tw-font-semibold tw-mb-1">{{ $t(`field.by_${col.key}`) }}</div>
              <div
                v-for="option in col.options"
                class="tw-mt-2 tw-text-sky-500 tw-cursor-pointer hover:!tw-underline hover:!tw-text-blue-700"
                @click="onPick(option, hide)"
              >
                <span :class="activeClasses(option)"> {{ $t(`field.${option}`) }}</span>
              </div>
            </div>
          </div>
        </q-card-section>
      </q-card>
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

export default {
  name: "PredefinedDatePicker",

  components: {PopupTextField, Button},

  props: {
    modelValue: Object | String
  },

  emits: ['update:model-value', 'finish'],

  setup(props, {emit}) {
    // i18n
    const $t = useI18n().t
    // Model
    const model = useVModel(props, 'modelValue', emit)

    // Convert string to a radio option
    const toRadio = (value) => ({value: value, label: $t(`field.${value}`)})
    // Predefined option structure
    const structure = computed(() => ([
      {key: 'day', options: ['day_now', 'day_prev', 'day_backward_2']},
      {key: 'week', options: ['week_now', 'week_prev', 'week_backward_7']},
      {key: 'month', options: ['month_now', 'month_prev', 'month_backward_30']},
      {key: 'quarter', options: ['quarter_now', 'quarter_prev']},
      {key: 'year', options: ['year_now', 'year_prev']},
    ]))

    // Picked option
    const pickedOption = ref('')
    // On picking an option
    const onPick = (value) => pickedOption.value = value
    // Watch for option changed
    watch(pickedOption, value => {
      // Check if value is unset
      if (util.isUnset(value)) {
        model.value = null
        return
      }

      // Split option to get [unit]-[moment]
      const [unit, moment, days] = value.split('_')
      // Get current dayjs
      const now = dayjs()
      // Create output
      let output = null

      if (moment === 'now') {
        // Model will be a single Date string
        output = now.format('YYYY-MM-DD')
      } else if (moment === 'backward') {
        // Subtract the [days] days from now
        output = {
          from: now.subtract(parseInt(days), "day").format('YYYY-MM-DD'),
          to: now.format('YYYY-MM-DD')
        }
      } else if (moment === 'prev') {
        // Get the start of previous [unit]
        output = {
          from: now.startOf(unit).add(3 - moment.length, unit).format('YYYY-MM-DD'),
          to: now.format('YYYY-MM-DD')
        }
      }

      // Assign model
      model.value = output

      emit('finish')
    })

    return {
      // Predefined option
      pickedOption: pickedOption, structure: structure,
      onPick: onPick,
    }
  }
}
</script>
