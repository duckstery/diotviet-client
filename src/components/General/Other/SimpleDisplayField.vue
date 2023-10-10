<template>
  <div class="tw-text-lg tw-flex">
    <SimpleLabelField :color="color" :icon="icon" :label="label" :class="labelClass"/>
    <span v-if="dense">&nbsp;</span>
    <q-space v-else/>
    <slot>{{ content }}</slot>
  </div>
</template>

<script>
import SimpleLabelField from "components/General/Other/SimpleLabelField.vue";

export default {
  name: "SimpleDisplayField",
  components: {SimpleLabelField},

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
    // Content
    content: String,
    // Label's class
    labelClass: String,
    // Dense mode
    dense: Boolean,
  }
}
</script>
