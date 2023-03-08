<template>
  <q-btn
    v-bind="$attrs"

    color="white"
    :dense="!stretch"

    :flat="flat"
    unelevated
    :label="isUsingImage ? undefined : label"
    :icon="isUsingImage ? undefined : icon"
  >
    <template v-if="isUsingImage">
      <img class="d-btn-img" :src="src">
      <span class="tw-ml-3">{{ label }}</span>
    </template>
    <q-tooltip v-if="tooltip" transition-show="scale" transition-hide="scale" class="tw-text-sm">
      {{ tooltip }}
    </q-tooltip>
  </q-btn>
</template>

<script>
export default {
  name: "Button",

  props: {
    // Image src
    src: {
      type: String,
      default: null
    },
    // Icon
    icon: {
      type: String,
      default: 'search'
    },
    // Label
    label: {
      type: String,
      default: ''
    },
    // No flat
    flat: {
      type: Boolean,
      default: false
    },
    // No dense
    stretch: {
      type: Boolean,
      default: false
    },
    // Tooltip
    tooltip: {
      type: String,
      default: null
    }
  },

  data: () => ({
    size: '28px'
  }),

  computed: {
    /**
     * Check if button is not using image as icon
     *
     * @returns {boolean}
     */
    isUsingImage() {
      return !!this.src
    }
  }
}
</script>

<style scoped lang="scss">
.d-btn-img {
  height: v-bind(size) !important;
  width: v-bind(size) !important;
}
</style>
