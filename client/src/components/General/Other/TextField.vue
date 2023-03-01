<template>
  <q-input
    v-model="text"

    v-bind="$attrs"

    outlined
    dense
    bg-color="white"
    clearable
  >
    <template #prepend>
      <q-icon :name="icon"/>
    </template>
  </q-input>
</template>

<script>
export default {
  name: "TextField",

  props: {
    // Value
    modelValue: String,
    // Icon
    icon: {
      type: String,
      default: 'search'
    },
  },

}
</script>

