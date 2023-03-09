<template>
  <q-card class="sample-panel">
    <q-card-section class="flex tw-w-full tw-overflow-y-auto" :style="`max-height: ${maxHeight}`">
      ahihi
    </q-card-section>
  </q-card>
</template>

<script>
export default {
  name: 'StatisticPanel',

  props: {
    maxHeight: Number
  }
}
</script>
