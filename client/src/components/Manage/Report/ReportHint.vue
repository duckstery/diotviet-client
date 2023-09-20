<template>
  <div>
    <div class="tw-text-3xl tw-font-bold">Hint</div>
    <div v-for="dataset in datasets" :key="dataset.label">
      <q-list dense>
        <q-item>
          <q-item-section avatar>
            <span class="tw-w-[40px] tw-h-[15px] tw-block tw-mt-1" :style="getStyle(dataset.color)"/>
          </q-item-section>
          <q-item-section class="tw-max-w-[200px]">
            <div class="tw-ml-3">{{ $t(`field.${dataset.key}`) }}:</div>
          </q-item-section>
          <q-item-section>
            <div class="tw-ml-3"> {{ $t(`message.${dataset.hint}`) }}</div>
          </q-item-section>
        </q-item>
      </q-list>
    </div>
  </div>
</template>

<script>

import {constant} from "src/boot";

export default {
  name: "ReportHint",

  props: {
    datasets: Array,
  },

  emits: ['request', 'update:model-value', 'control'],

  setup() {
    // Get style
    const getStyle = (color) => {
      // Apply ChartJs color to border and background
      const borderColor = constant.chartColors(color)
      const background = constant.chartColors(color, 0.5)

      return `background: ${background}; border: solid 3px ${borderColor}`
    }

    return {getStyle: getStyle}
  }
}
</script>
