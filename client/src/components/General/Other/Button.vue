<template>
  <q-btn
    v-bind="$attrs"

    :color="color"
    :dense="!stretch"

    :flat="flat"
    unelevated
    :label="isUsingImage ? undefined : label"
    :icon="getIcon"
  >
    <template v-if="isUsingImage">
      <IconMage class="d-btn-img" :src="src" color="light"/>
      <span class="tw-ml-3">{{ label }}</span>
    </template>
    <q-tooltip v-if="tooltip" transition-show="scale" transition-hide="scale" class="tw-text-sm">
      {{ tooltip }}
    </q-tooltip>
    <slot/>
  </q-btn>
</template>

<script>
import IconMage from "components/General/Other/IconMage.vue";

export default {
  name: "Button",
  components: {IconMage},

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
    },
    // Color
    color: {
      type: String,
      default: 'brand'
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
    },
    /**
     * Get icon for button only if not using image or is optimized for performance
     *
     * @return {undefined|string}
     */
    getIcon() {
      return this.isUsingImage
        ? this.$env.isOptimizeVisual() ? undefined : this.$util.getMatchedIcon(this.src.slice(7, -4))
        : this.icon
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
