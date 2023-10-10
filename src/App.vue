<template>
  <router-view/>
</template>

<script>
import {defineComponent} from 'vue'

export default defineComponent({
  name: 'App',

  mounted() {
    // Initiate locale setting
    this.$i18n.locale = this.$env.get("language") ?? 'en'
    // Initiate display setting
    this.$q.dark.set(this.$env.get("display") === 'dark')
  },

  provide() {
    // Calculation min height
    const minHeight = this.$q.screen.height - 50 ?? 742

    return {
      // Global variables
      globalVars: {
        // Screen min height
        minHeight: minHeight,
        // Height without padding
        usableHeight: minHeight - 20 * 2
      }
    }
  }
})
</script>
