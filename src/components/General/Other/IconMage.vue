<template>
  <q-avatar :square="!rounded" :rounded="rounded" :size="size">
    <img v-if="isOptimizeVisual" :src="src" alt="">
    <q-icon v-else :name="getParsedIcon" size="18px" :color="color"/>
  </q-avatar>
</template>

<script>
export default {
  name: 'IconMage',

  props: {
    // Image icon
    size: {
      type: String,
      default: '32px'
    },
    // Image source
    src: {
      type: String
    },
    // Make avatar rounded
    rounded: {
      type: Boolean,
      default: false
    },
    // Only work if it is optimizing for performance
    color: {
      type: String,
      default: 'primary'
    },
    // Force using visual (image)
    forceVisual: {
      type: Boolean
    }
  },

  computed: {
    // Check if optimize for visual
    isOptimizeVisual() {
      return this.forceVisual || this.$env.isOptimizeVisual()
    },
    // Parse img source to icon
    getParsedIcon() {
      return this.$constant.matchedIcon(this.src.slice(7, -4))
    },
  }
}
</script>
