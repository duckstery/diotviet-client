<template>
  <span v-if="span" :class="classObject" v-bind="$attrs" v-on="$listeners">
    <slot/>
  </span>
  <div v-else :class="classObject" v-bind="$attrs" v-on="$listeners">
    <slot/>
  </div>
</template>

<script>
export default {
  name: "Typo",

  props: {
    span: {
      type: Boolean,
      default: false
    },
    /**
     * Override default class
     */
    override: {
      type: Boolean,
      default: false
    }
  },

  data() {
    return {
      classObject: {
        'text--primary': !this.override,
        ...this.$attrs.class
      }
    }
  }
}
</script>
