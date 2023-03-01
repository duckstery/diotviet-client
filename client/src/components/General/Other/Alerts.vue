<template>
  <div class="tw-w-full">
    <template v-if="loading">
      <v-alert class="tw-w-full" type="warning" dense icon="mdi-alert">
        {{ $t('general.pleaseWait') }}
      </v-alert>
    </template>
    <template v-else>
      <v-alert
        v-for="(item, index) in items"
        dense
        class="tw-w-full"

        :key="index"
        :type="caution ? 'error' : (item.type ?? 'info')"
      >
        {{ item.text ?? '' }}
      </v-alert>
    </template>
  </div>
</template>

<script>
export default {
  name: "Alerts",

  props: {
    /**
     * List of alerts
     */
    items: {
      type: Array,
      default: () => ([])
    },
    /**
     * Show warning alert
     */
    loading: {
      type: Boolean,
      default: false
    },
    /**
     * Force apply all alert to error
     */
    caution: {
      type: Boolean,
      default: false
    }
  }
}
</script>

<style scoped>

</style>
