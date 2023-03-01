<template>
  <v-tooltip
    v-model="tooltip"
    v-bind="$attrs"
    v-on="$listeners"

    :open-on-hover="false"
    :open-on-click="false"
    :nudge-left="left ? 50 : 0"
    :nudge-right="left ? 0 : 50"
    :right="!left"

    @input="$emit('tooltipInput', $event)"
  >
    <template #activator="on">
      <slot :on="localListener"/>
    </template>
    <template #default>
      <div class="d-flex flex-column justify-center align-start">
        <div v-for="(rule, index) in rules" :key="index">
          <v-icon v-if="testcase[index]" class="mr-1" color="success">mdi-check</v-icon>
          <v-icon v-else class="mr-1" color="error">mdi-close</v-icon>
          {{ rule.description }}
        </div>
      </div>
    </template>
  </v-tooltip>
</template>

<script>
export default {
  name: "ValidateTooltip",

  props: {
    /**
     * True if target is valid, otherwise, false
     */
    value: {
      type: Boolean,
      default: false
    },
    /**
     * Rules
     */
    rules: {
      type: Array,
      default: () => ([])
    },
    /**
     * Validate target
     */
    target: {
      type: String | Number,
      default: '',
      required: true
    },
    /**
     * Nudge left instead
     */
    left: {
      type: Boolean,
      default: false
    },
  },

  data() {
    return {
      /**
       * Workaround of activator.
       * Tooltip will be show manually instead of using activator slot event handler
       * Because tooltip @input will be called if using activator slot event handler
       * And activator slot event handler will be passed to parent (outside)
       * So local @input will be mixed with tooltip @input (tooltip @input will be catch outside
       */
      tooltip: false,
      localListener: {
        focus: () => this.tooltip = true,
        blur: () => this.tooltip = false
      }
    }
  },

  computed: {
    /**
     * Test overall result. True if all testcase result is true, otherwise, false
     *
     * @returns {boolean}
     */
    overall() {
      return this.testcase && this.testcase.every(value => value === true)
    },
    /**
     * Rule's results array
     *
     * @returns {boolean|*[]}
     */
    testcase() {
      // Target must exist or not empty
      return !!this.target && this.rules.map(rule => rule.pass(this.target))
    }
  },

  watch: {
    /**
     * Return if target is valid or not (v-model)
     *
     * @param value
     */
    overall(value) {
      this.$emit('input', value)
    },
  },
}
</script>
