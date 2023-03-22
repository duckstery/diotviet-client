<template>
  <q-input
    :model-value="modelValue"
    v-bind="$attrs"
    :label="label"

    :outlined="!compact"
    dense
    color="brand"
    bg-color="brand"
    :clearable="!required"
    :unmasked-value="!!mask"
    :reverse-fill-mask="!!mask"
    :mask="mask"
  >
    <template v-if="!compact" #prepend>
      <q-icon :name="icon"/>
    </template>
    <template v-for="(_, slot) of $slots" #[slot]="scope">
      <slot v-if="slot !== 'prepend'" :name="slot" v-bind="scope"/>
    </template>
  </q-input>
</template>

<script>
export default {
  name: "TextField",

  props: {
    // Value
    modelValue: String,
    // Label
    label: String,
    // Icon
    icon: {
      type: String,
      default: 'search'
    },
    // Compact mode
    compact: {
      type: Boolean,
      default: false
    },
    // Required
    required: {
      type: Boolean,
      default: false
    },
    // Mask
    mask: {
      type: String,
      default: "",
    }
  },

}
</script>

