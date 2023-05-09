<template>
  <div class="tw-flex">
    <LabelField :src="src" :label="label" class="tw-pt-2"/>
    <q-space v-if="space"/>
    <TextField
      :model-value="modelValue"
      compact
      required
      readonly
      :class="classObject"
      class="innerClass"
      input-class="tw-font-semibold tw-text-center tw-p-0"

      :mask="mask"
    />
  </div>
</template>

<script>
import IconMage from "components/General/Other/IconMage.vue";
import TextField from "components/General/Other/TextField.vue";
import LabelField from "components/General/Other/LabelField.vue";

export default {
  name: "DisplayField",

  components: {LabelField, TextField, IconMage},

  props: {
    // Model-value
    modelValue: String,
    // Put space between label and content
    space: Boolean,
    // IconMage src
    src: String,
    // Label
    label: String,
    // Mask
    mask: String,
    // Inner class
    innerClass: {
      type: String,
      default: ''
    },
  },

  computed: {
    // Class object
    classObject() {
      return Object.entries({
        'tw-p-0': true,
        'tw-float-right': true,
        'tw-ml-3': !this.space,
        'tw-flex-grow': !this.space
      })
        .filter(entry => entry[1])
        .map(entry => entry[0])
        .join(' ') + ' ' + this.innerClass
    }
  }
}
</script>
