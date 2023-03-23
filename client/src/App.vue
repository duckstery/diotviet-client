<template>
  <router-view />
</template>

<script>
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'App',

  mounted() {
    // Initiate locale setting
    this.$i18n.locale = this.$env.get("language") ?? 'en'
    // Initiate display setting
    this.$q.dark.set(this.$env.get("display") === 'dark')
  }
})
</script>
