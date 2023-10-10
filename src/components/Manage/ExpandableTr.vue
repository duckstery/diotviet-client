<template>
  <q-tr :props="props">
    <!-- Show container first (slide down), then active "inner" to open content (fade in) -->
    <transition name="resize" @after-enter="onAfterShowContainer">
      <q-td v-if="isContainerExpanded" no-hover colspan="100%" :style="containerStyle">
        <!-- Hide content first (fade out), then deactivate "expand" to hide container (slide up) -->
        <transition-group name="fade" @after-enter="onAfterShowContent" @after-leave="onAfterHideContent">
          <div v-if="isContentExpanded">
            <slot :active="isActive"/>
          </div>
        </transition-group>
      </q-td>
    </transition>
  </q-tr>
</template>

<script>

export default {
  name: "ExpandableTr",

  props: {
    props: Object,
    expand: Boolean,
    height: Number,
    width: Number
  },

  data: () => ({
    // Expand supporter
    contentExpand: false,
    // Is active
    isActive: false
  }),

  computed: {
    // Container style
    containerStyle() {
      return {
        height: this.height + 'px',
        width: this.width + 'px',
        overflow: 'hidden'
      }
    },

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
     * After content's enter transition is finished, make <slot/> active
     */
    onAfterShowContent() {
      // Active <slot/> so it can do stuff after animation is finished
      this.isActive = true
      // Make content inactive, so it can be active again
      this.$nextTick(() => this.isActive = false)
    },

    /**
     * After content's leave transition is finished, hide container
     */
    onAfterHideContent() {
      // Save inner expand status
      this.contentExpand = false
      // Make content inactive
      this.isActive = false
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
