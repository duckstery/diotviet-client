<template>
  <router-view/>
</template>

<script>
import {defineComponent} from 'vue'

export default defineComponent({
  name: 'App',

  async mounted() {
    // Initiate locale setting
    this.$i18n.locale = (await this.$env.get("language")) ?? 'en'
    // Initiate display setting
    this.$q.dark.set((await this.$env.get("display")) === 'dark')
    // Check for updates (only for Electron)
    if (this.$q.platform.is.electron) {
      await this.checkForUpdates()
    }
  },

  methods: {
    /**
     * Check for updates
     */
    async checkForUpdates() {
      // onUpdateAvailable
      window.mainAPI.onUpdateAvailable((info) => {
        console.warn(info)
        // Ask if user want to update
        this.$util.promptConfirm(this.$t('message.update_available')).onOk(() => window.mainAPI.updates());
      })

      // Check for updates
      window.mainAPI.checkForUpdates()
    }
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
