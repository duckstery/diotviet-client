<template>
  <router-view/>
</template>

<script>
import {defineComponent} from 'vue'
import {autoUpdater} from "electron-updater";

export default defineComponent({
  name: 'App',

  mounted() {
    // Initiate locale setting
    this.$i18n.locale = this.$env.get("language") ?? 'en'
    // Initiate display setting
    this.$q.dark.set(this.$env.get("display") === 'dark')
    // Check for updates (only for Electron)
    if (this.$q.platform.is.electron) {
      this.checkForUpdates()
    }
  },

  methods: {
    /**
     * Check for updates
     */
    async checkForUpdates() {
      autoUpdater.on('update-available', (info) => {
        console.warn(info)
        this.$util.promptConfirm(this.$t('message.update_available'))
          .onOk(async () => {
            autoUpdater.quitAndInstall()
          });
      })
      await autoUpdater.checkForUpdatesAndNotify()
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
