<template>
  <q-page class="bg-grey-3">
    <div :class="classes" :style="style">
      <!-- Breadcrumbs -->
      <Breadcrumbs v-if="breadcrumbs" :items="breadcrumbs" ref="breadcrumbs" class="tw-mb-5"/>
      <div class="row" :style="`min-height: ${contentHeight}px`">
        <template v-if="shouldSplit">
          <div :class="leftColClasses">
            <slot name="left"/>
          </div>
          <div :class="rightColClasses">
            <slot name="right"/>
          </div>
        </template>

        <template v-else>
          <div class="col-12">
            <slot/>
          </div>
        </template>
      </div>
    </div>
  </q-page>
</template>

<script>
import Breadcrumbs from "components/Manage/Breadcrumbs.vue";

export default {
  name: 'Page',

  components: {Breadcrumbs},

  inject: ['globalVars'],

  props: {
    breadcrumbs: Object,
    breakpoint: {
      type: String,
      default: 'md',
    },
    split: {
      type: Array,
      default: null
    }
  },

  computed: {
    style() {
      return {
        minHeight: `${this.globalVars.minHeight}px`
      }
    },
    classes() {
      return {
        'bg-grey-3': true,
        'tw-p-5': true,
        'tw-pr-20': this.$q.screen.gt.sm
      }
    },
    // Remaining space below breadcrumbs
    contentHeight() {
      // Calculate content height
      const contentHeight = this.globalVars.usableHeight - this.$refs.breadcrumbs?.$el.clientHeight
      return isNaN(contentHeight)
        ? this.globalVars.usableHeight - 50
        : contentHeight
    },
    // Check if screen should be split
    shouldSplit() {
      return Array.isArray(this.split) && this.split.length === 2
    },
    // Left column classes
    leftColClasses() {
      // Classes
      const classes = {
        'col-12': true,
        'tw-pr-3': this.$q.screen[this.breakpoint] || this.$q.screen.gt[this.breakpoint],
      }

      // Get split left column offset (0-12)
      const offset = this.split[0]
      // Add classes col-md-[offset]
      classes[`col-${this.breakpoint}-${offset}`] = true

      return classes
    },
    // Right column classes
    rightColClasses() {
      // Classes
      const classes = {
        'col-12': true,
        'tw-mt-3': this.$q.screen.lt[this.breakpoint]
      }

      // Get split left column offset (0-12)
      const offset = this.split[1]
      // Add classes col-md-[offset]
      classes[`col-${this.breakpoint}-${offset}`] = true

      return classes
    }
  }
}
</script>
