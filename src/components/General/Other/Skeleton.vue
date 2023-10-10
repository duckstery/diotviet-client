<template>
  <slot v-if="modelValue" />
  <q-skeleton v-else :class="skeletonClass" :width="width" :height="height" :square="square" :type="type"/>
</template>

<script>
export default {
  name: "Skeleton",
  props: {
    modelValue: Boolean,
    width: String,
    height: String,
    square: Boolean,
    type: String,
    skeletonClass: String
  }
}
</script>
