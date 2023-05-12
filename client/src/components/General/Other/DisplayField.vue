<template>
  <div :class="{'tw-flex': !horizontal}">
    <LabelField :src="src" :label="label" class="tw-pt-2"/>
    <slot/>
    <q-space v-if="space"/>
    <div v-if="textarea">
      <q-input
        :model-value="modelValue"
        dense
        filled
        readonly
        type="textarea"
        :class="classObject"
        :maxlength="textareaLength"
        input-class="virtual-scrollbar"
        :input-style="{maxHeight: `${textareaHeight}px`, height: `${textareaHeight}px`}"
      />
    </div>
    <TextField
      v-else
      :model-value="modelValue"
      compact
      readonly
      required
      :mask="mask"
      :class="classObject"
      input-class="tw-font-semibold tw-text-center tw-p-0"
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
    // Use textarea instead
    textarea: Boolean,
    // Display horizontally
    horizontal: Boolean,
    // Textarea max character length
    textareaLength: String,
    // Textarea max height
    textareaHeight: String,
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
        'tw-p-0': !this.horizontal,
        'tw-float-right': !this.horizontal,
        'tw-ml-3': !this.space && !this.horizontal,
        'tw-flex-grow': !this.space && !this.horizontal,
        'tw-mt-3': this.horizontal,
        'tw-w-full': this.horizontal
      })
        .filter(entry => entry[1])
        .map(entry => entry[0])
        .join(' ') + ' ' + this.innerClass
    }
  }
}
</script>
