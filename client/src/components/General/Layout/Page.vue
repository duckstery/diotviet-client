<template>
  <q-page class="bg-grey-3">
    <div class="bg-grey-3 tw-p-5 tw-pr-20" :style="style">
      <!-- Breadcrumbs -->
      <Breadcrumbs v-if="breadcrumbs" :items="breadcrumbs" ref="breadcrumbs" class="tw-mb-5"/>
      <div class="row" :style="`min-height: ${contentHeight}px`">
        <slot/>
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
    breadcrumbs: Object
  },

  computed: {
    style() {
      return {
        minHeight: `${this.globalVars.minHeight}px`
      }
    },
    // Remaining space below breadcrumbs
    contentHeight() {
      // Calculate content height
      const contentHeight = this.globalVars.usableHeight - this.$refs.breadcrumbs?.$el.clientHeight
      return isNaN(contentHeight)
        ? this.globalVars.usableHeight - 30
        : contentHeight
    },
  }
}
</script>
