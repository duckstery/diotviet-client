<template>
  <div class="tw-text-lg tw-flex">
    <q-icon :name="icon" :color="color" size="20px" class="tw-pt-1"/>
    <span class="tw-ml-3 tw-underline tw-underline-offset-4">{{ label }}</span>
    <q-space/>
    <span>:</span>
  </div>
</template>

<script>
export default {
  name: "SimpleLabelField",

  props: {
    // Icon's color
    color: {
      type: String,
      default: 'primary'
    },
    // Icon
    icon: String,
    // Label
    label: String,
  }
}
</script>
