<template>
  <q-avatar :square="!rounded" :rounded="rounded" :size="size">
    <img :src="src" alt="">
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
    }
  }
}
</script>
