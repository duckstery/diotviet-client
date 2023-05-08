<template>
  <q-tr :props="props">
    <!-- Show container first (slide down), then active "inner" to open content (fade in) -->
    <transition name="resize" @after-enter="onAfterShowContainer">
      <q-td v-if="isContainerExpanded" colspan="100%" :style="`height: ${height}px`">
        <!-- Hide content first (fade out), then deactivate "expand" to hide container (slide up) -->
        <transition-group name="fade" @after-leave="onAfterHideContent">
          <div v-if="isContentExpanded">
            <slot/>
          </div>
        </transition-group>
      </q-td>
    </transition>
  </q-tr>
</template>

<script>
import {scroll} from "quasar"

const {getScrollTarget, setVerticalScrollPosition} = scroll

export default {
  name: "ExpandableTr",

  props: {
    props: Object,
    expand: Boolean,
    height: Number
  },

  data: () => ({
    // Expand supporter
    contentExpand: false
  }),

  computed: {
    // Container is considered "expand" if itself and its content is expanding
    isContainerExpanded() {
      return this.expand || this.contentExpand
    },

    // Content is considered "expand" if both its container and its self is expanding
    isContentExpanded() {
      return this.expand && this.contentExpand
    },
  },

  methods: {
    /**
     * After container's enter transition is finished, show content
     */
    onAfterShowContainer(el) {
      // Save inner expand status
      this.contentExpand = true
      // Scroll to el
      try {
        window.scrollTo({
          top: el.parentElement.previousElementSibling.getBoundingClientRect().top + window.pageYOffset - 50,
          behavior: 'smooth'
        })
      } catch (e) {/** Ignore */}
    },

    /**
     * After content's leave transition is finished, hide container
     */
    onAfterHideContent() {
      // Save inner expand status
      this.contentExpand = false
    }
  },
}
</script>

<style scoped>
.resize-enter-active, .resize-leave-active {
  transition: all 0.3s;
}

.resize-enter-from, .resize-leave-to {
  opacity: 0;
  height: 0 !important;
  padding-top: 0 !important;
  padding-bottom: 0 !important;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
