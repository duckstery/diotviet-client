<template>
  <q-btn
    v-bind="$attrs"

    flat
    color="white"
    dense

    :label="isUsingImage ? undefined : label"
    :icon="isUsingImage ? undefined : icon"
  >
    <template v-if="isUsingImage">
      <img class="d-btn-img" :src="src">
      <span class="tw-ml-3">{{label}}</span>
    </template>
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
