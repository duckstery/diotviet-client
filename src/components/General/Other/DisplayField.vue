<template>
  <div :class="{'tw-flex': !horizontal}">
    <LabelField :src="src" :label="label" class="tw-pt-2"/>
    <slot/>
    <q-space v-if="space"/>
    <template v-if="!custom">
      <div v-if="textarea">
        <q-input
          :model-value="modelValue"
          dense
          filled
          readonly
          type="textarea"
          @click="onInteract"
          :class="classObject"
          :maxlength="textareaLength"
          :input-class="inputClassObject"
          :input-style="{maxHeight: `${textareaHeight}px`, height: `${textareaHeight}px`}"
        />
      </div>
      <TextField
        v-else
        :model-value="modelValue"
        compact
        readonly
        required
        @click="onInteract"
        :mask="mask"
        :class="classObject"
        :input-class="inputClassObject"
      />
    </template>
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
    // Use custom tag instead of TextField
    custom: Boolean,
    // Interactive mode
    interactive: Boolean,
    // Inner class
    innerClass: {
      type: String,
      default: ''
    },
    // Inner input class
    innerInputClass: {
      type: String,
      default: ''
    }
  },

  emits: ['interact'],

  computed: {
    // Class object
    classObject() {
      return Object.entries({
        'tw-p-0': !this.horizontal,
        'tw-float-right': !this.horizontal,
        'tw-ml-3': !this.space && !this.horizontal,
        'tw-flex-grow': !this.space && !this.horizontal,
        'tw-mt-3': this.horizontal,
        'tw-w-full': this.horizontal,
      })
        .filter(entry => entry[1])
        .map(entry => entry[0])
        .join(' ') + ' ' + this.innerClass
    },
    // Input class object
    inputClassObject() {
      return Object.entries({
        'virtual-scrollbar': this.textarea,
        'tw-font-semibold': !this.textarea,
        'tw-text-center': !this.textarea,
        'tw-p-0': !this.textarea,
        'text-primary': this.interactive,
        '!tw-cursor-pointer': this.interactive,
        'hover:!tw-underline': this.interactive,
        'tw-underline-offset-2': this.interactive,
      })
        .filter(entry => entry[1])
        .map(entry => entry[0])
        .join(' ') + ' ' + this.innerInputClass
    }
  },

  methods: {
    /**
     * On interact (available for interact mode only)
     */
    onInteract($event) {
      if (this.interactive) {
        this.$emit('interact', $event)
      }
    }
  }
}
</script>
