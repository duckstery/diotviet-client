<template>
  <q-btn-dropdown
    v-bind="$attrs"
    v-model="state"

    :color="color"
    :dense="!stretch"

    :flat="flat"
    unelevated
    :label="isUsingImage ? undefined : label"
    :icon="isUsingImage ? undefined : icon"
    :disable="disable"
  >
    <template v-if="isUsingImage">
      <img class="d-btn-img" :src="src">
      <span class="tw-ml-3">{{ label }}</span>
    </template>
    <Tooltip v-if="tooltip" :content="tooltip"/>
    <slot/>
  </q-btn-dropdown>
</template>

<script>
import Tooltip from "components/General/Other/Tooltip.vue";

export default {
  name: "DropdownButton",
  components: {Tooltip},

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
    },
    // Color
    disable: {
      type: Boolean,
      default: false
    }
  },

  data: () => ({
    size: '28px',
    state: false
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
