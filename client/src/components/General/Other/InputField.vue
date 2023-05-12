<template>
  <div :class="{'tw-flex': !horizontal}">
    <LabelField :src="src" :label="label" class="tw-pt-2"/>
    <slot name="before"/>
    <q-space v-if="space"/>
    <slot :class="classObject"/>
  </div>
</template>

<script>
import IconMage from "components/General/Other/IconMage.vue";
import TextField from "components/General/Other/TextField.vue";
import LabelField from "components/General/Other/LabelField.vue";

export default {
  name: "InputField",

  components: {LabelField, TextField, IconMage},

  props: {
    // Put space between label and content
    space: Boolean,
    // IconMage src
    src: String,
    // Label
    label: String,
    // Display horizontally
    horizontal: Boolean,
  },

  computed: {
    // Class object
    classObject() {
      return {
        'tw-p-0': !this.horizontal,
        'tw-float-right': !this.horizontal,
        'tw-ml-3': !this.space && !this.horizontal,
        'tw-flex-grow': !this.space && !this.horizontal,
        'tw-mt-3': this.horizontal,
        'tw-w-full': this.horizontal
      }
    }
  }
}
</script>
