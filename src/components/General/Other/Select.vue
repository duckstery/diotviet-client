<template>
  <q-select
    v-bind="$attrs"

    :label="label"
    :color="color"
    :bg-color="bgColor"
    :options="options"
    :clearable="clearable"
    :emit-value="emitValue"
    :map-options="emitValue"
    :model-value="modelValue"
    :option-label="optionLabel"
    :option-value="optionValue"

    dense
    outlined
    options-dense

    @update:model-value="$emit('update:modelValue', $event)"
  >
    <template v-if="icon" #prepend>
      <q-icon :name="icon"/>
    </template>
    <template v-for="(_, slot) of $slots" #[slot]="scope">
      <slot v-if="slot !== 'prepend'" :name="slot" v-bind="scope"/>
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
    // Emit value instead
    emitValue: Boolean,
    // Clearable
    clearable: Boolean,
    // Color
    color: String,
    // Background color
    bgColor: String,
    // Option label field
    optionLabel: {
      type: String,
      default: 'name'
    },
    // Option value field
    optionValue: {
      type: String,
      default: 'id',
    },
  },

  emits: ['update:modelValue']
}
</script>

