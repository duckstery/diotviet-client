<template>
  <q-select
    v-model="text"

    v-bind="$attrs"
    :options="options"
    :label="label"

    options-dense
    outlined
    dense
    bg-color="white"
    clearable
  >
    <template v-if="icon" #prepend>
      <q-icon :name="icon"/>
    </template>
  </q-select>
</template>

<script>
export default {
  name: "Select",

  props: {
    // Value
    modelValue: String,
    // Label
    label: String,
    // Select options
    options: {
      type: Array,
      default: () => (['Google', 'Facebook', 'Twitter', 'Apple', 'Oracle'])
    },
    // Icon
    icon: {
      type: String,
      default: null
    },
  },

}
</script>

