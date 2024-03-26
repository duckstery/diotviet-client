<template>
  <Button v-bind="$attrs" icon="settings" color="white" :tooltip="$t('field.settings')">
    <q-popup-proxy>
      <q-card>
        <q-card-section class="tw-p-0 tw-pb-3 tw-h-full">
          <!-- Locale -->
          <q-banner class="tw-mt-1">
            <template v-slot:avatar>
              <IconMage src="images/translation.png"/>
            </template>
            <div class="tw-font-medium">{{ $t('field.language') }}</div>
          </q-banner>
          <div class="tw-mx-3 tw-p-3 tw-border-dotted tw-border-blue-500 tw-rounded-md">
            <Button src="images/en_US.png" color="primary" :flat="!isEnglish" stretch :label="$t('field.en')"
                    class="tw-w-[140px]" @click="onChangeEnv('language', 'en')"/>
            <Button src="images/vi_VN.png" color="primary" :flat="isEnglish" stretch :label="$t('field.vi')"
                    class="tw-w-[140px] tw-ml-2" @click="onChangeEnv('language', 'vi')"/>
          </div>

          <!-- Display mode -->
          <q-banner class="tw-mt-1">
            <template v-slot:avatar>
              <IconMage src="images/dark-mode.png"/>
            </template>
            <div class="tw-font-medium">{{ $t('field.display_mode') }}</div>
          </q-banner>
          <div class="tw-mx-3 tw-p-3 tw-border-dotted tw-border-blue-500 tw-rounded-md">
            <Button src="images/light.png" color="primary" :flat="!isLight" stretch :label="$t('field.light_mode')"
                    class="tw-w-[140px]" @click="onChangeEnv('display', 'light')"/>
            <Button src="images/dark.png" color="primary" :flat="isLight" stretch :label="$t('field.dark_mode')"
                    class="tw-w-[140px] tw-ml-2" @click="onChangeEnv('display', 'dark')"/>
          </div>

          <!-- Optimize -->
          <q-banner class="tw-mt-1">
            <template v-slot:avatar>
              <IconMage src="images/optimize.png"/>
            </template>
            <div class="tw-font-medium">{{ $t('field.optimize') }}</div>
          </q-banner>
          <div class="tw-mx-3 tw-p-3 tw-border-dotted tw-border-blue-500 tw-rounded-md">
            <Button src="images/visual.png" color="primary" :flat="!isOptimizeVisual" stretch
                    :label="$t('field.visual')"
                    class="tw-w-[140px]" @click="onChangeEnv('optimize', 'visual')"/>
            <Button src="images/speed.png" color="primary" :flat="isOptimizeVisual" stretch :label="$t('field.speed')"
                    class="tw-w-[140px] tw-ml-2" @click="onChangeEnv('optimize', 'speed')"/>
          </div>
          <q-banner class="tw-mt-1">
            <template v-slot:avatar>
              <IconMage src="images/print.png"/>
            </template>
            <div class="tw-font-medium">{{ $t('field.print') }}</div>
          </q-banner>
          <div class="tw-mx-3 tw-p-3 tw-border-dotted tw-border-blue-500 tw-rounded-md">
            <Button src="images/setup.png" color="primary" stretch flat :label="$t('field.setup')"
                    class="tw-w-full" @click="onSetup('print')"/>
          </div>
          <q-banner class="tw-mt-1">
            <template v-slot:avatar>
              <IconMage src="images/server.png"/>
            </template>
            <div class="tw-font-medium">{{ $t('field.server') }}</div>
          </q-banner>
          <div class="tw-mx-3 tw-p-3 tw-border-dotted tw-border-blue-500 tw-rounded-md tw-flex">
            <TextField v-model="server" class="tw-max-w-[200px]"/>
            <q-space/>
            <Button flat color="primary" icon="fa-solid fa-floppy-disk" :label="$t('field.save')" @click="saveServer"/>
          </div>
        </q-card-section>
      </q-card>
    </q-popup-proxy>
  </Button>
</template>

<script>
import {env} from "boot/env";

import Button from "components/General/Other/Button.vue";
import IconMage from "components/General/Other/IconMage.vue";
import TextField from "components/General/Other/TextField.vue";
import {Platform} from "quasar";

export default {
  name: 'Setting',

  components: {TextField, IconMage, Button},

  data: () => ({
    language: 'en', // Default is English
    display: 'light', // Default is light
    optimize: 'visual', // Default is visual
    server: 'http://localhost:8080' // Default is [http://localhost:8080]
  }),

  computed: {
    // Check if language is English. Why? cuz it is default value
    isEnglish() {
      return this.language !== 'vi'
    },
    // Check if display is Light. Why? cuz it is default value
    isLight() {
      return this.display !== 'dark'
    },
    // Check if optimize for visual. Why? cuz it is default value
    isOptimizeVisual() {
      return this.optimize !== 'speed'
    }
  },

  methods: {
    /**
     * On change env
     *
     * @param key
     * @param value
     */
    onChangeEnv(key, value) {
      const old = this[key]
      // Set local value
      this[key] = value
      // Set to env
      this.$env.set(key, value).then(() => key === 'optimize' && old !== value && window.location.reload())
    },

    /**
     * To setting page
     *
     * @param key
     */
    onSetup(key) {
      this.$router.push({name: `Setting.${this.$_.capitalize(key)}`})
    },

    /**
     * Save server
     */
    saveServer() {
      // Save to env
      this.$env.set('server', this.server)
      // Apply server to axios
      this.$axios.defaults.baseURL = `${this.server}/api`
      // Notify
      this.$notify(this.$t('message.success', {attr: this.$t('field.operation')}))
    }
  },

  watch: {
    // Handler for changing language
    language(value) {
      this.$i18n.locale = value ?? ''
    },
    // Handler for changing display
    isLight(value) {
      this.$q.dark.set(!value)
    }
  },

  async mounted() {
    this.language = await this.$env.get("language") ?? 'en'
    this.display = await this.$env.get("display") ?? 'light'
    this.optimize = await this.$env.get("optimize") ?? 'visual'
    this.server = await this.$env.get("server") ?? 'http://localhost:8080'
  }
}
</script>
